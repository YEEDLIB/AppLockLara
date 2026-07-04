package com.applocklara.applocklara.features.intruder

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class IntruderSelfieLog(
    val id: String,
    val photoPath: String,
    val dateTime: String,
    val appName: String,
    val failedAttemptCount: Int
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("photoPath", photoPath)
            put("dateTime", dateTime)
            put("appName", appName)
            put("failedAttemptCount", failedAttemptCount)
        }
    }

    companion object {
        fun fromJsonObject(jsonObject: JSONObject): IntruderSelfieLog {
            return IntruderSelfieLog(
                id = jsonObject.getString("id"),
                photoPath = jsonObject.getString("photoPath"),
                dateTime = jsonObject.getString("dateTime"),
                appName = jsonObject.getString("appName"),
                failedAttemptCount = jsonObject.getInt("failedAttemptCount")
            )
        }
    }
}

object IntruderSelfieManager {
    private const val TAG = "IntruderSelfieManager"
    private const val LOGS_FILE_NAME = "intruder_logs.json"
    private const val DIR_NAME = "intruder_selfies"
    private const val TIMEOUT_MS = 5000L

    fun getSelfiesDirectory(context: Context): File {
        val dir = File(context.filesDir, DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun loadLogs(context: Context): List<IntruderSelfieLog> {
        val file = File(getSelfiesDirectory(context), LOGS_FILE_NAME)
        if (!file.exists()) return emptyList()
        return try {
            val jsonStr = file.readText()
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<IntruderSelfieLog>()
            for (i in 0 until jsonArray.length()) {
                list.add(IntruderSelfieLog.fromJsonObject(jsonArray.getJSONObject(i)))
            }
            list.sortedByDescending { it.dateTime } // newest first
        } catch (e: Exception) {
            Log.e(TAG, "Error loading logs", e)
            emptyList()
        }
    }

    fun saveLogs(context: Context, logs: List<IntruderSelfieLog>) {
        val file = File(getSelfiesDirectory(context), LOGS_FILE_NAME)
        try {
            val jsonArray = JSONArray()
            logs.forEach { jsonArray.put(it.toJsonObject()) }
            file.writeText(jsonArray.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error saving logs", e)
        }
    }

    fun deleteLog(context: Context, logId: String) {
        val logs = loadLogs(context).toMutableList()
        val logToDelete = logs.find { it.id == logId }
        if (logToDelete != null) {
            try {
                val file = File(logToDelete.photoPath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting photo file", e)
            }
            logs.remove(logToDelete)
            saveLogs(context, logs)
        }
    }

    fun clearAllLogs(context: Context) {
        val logs = loadLogs(context)
        logs.forEach { log ->
            try {
                val file = File(log.photoPath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting photo file during clear", e)
            }
        }
        saveLogs(context, emptyList())
    }

    @SuppressLint("MissingPermission")
    fun takeSelfie(context: Context, appName: String, failedAttemptCount: Int) {
        // 1. Check permission
        if (context.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Camera permission not granted. Cannot take intruder selfie.")
            return
        }

        // 2. Get CameraManager
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        if (cameraManager == null) {
            Log.e(TAG, "CameraManager not available.")
            return
        }

        // 3. Find front camera ID
        var frontCameraId: String? = null
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = id
                    break
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding front camera", e)
            return
        }

        if (frontCameraId == null) {
            Log.e(TAG, "Front camera not found.")
            return
        }

        Log.d(TAG, "Starting silent background photo capture using front camera: $frontCameraId")

        // 4. Set up background thread for Camera2 callbacks
        val backgroundThread = HandlerThread("IntruderSelfieCameraThread").apply { start() }
        val backgroundHandler = Handler(backgroundThread.looper)

        // Keep references for cleanup
        var cameraDevice: CameraDevice? = null
        var captureSession: CameraCaptureSession? = null
        var imageReader: ImageReader? = null
        var isCaptured = false

        // Cleanup helper function
        fun cleanup() {
            try {
                captureSession?.close()
                cameraDevice?.close()
                imageReader?.close()
                backgroundThread.quitSafely()
                Log.d(TAG, "Camera resources cleaned up successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Error during cleanup", e)
            }
        }

        // Timeout fallback
        backgroundHandler.postDelayed({
            if (!isCaptured) {
                Log.w(TAG, "Camera capture timed out. Cleaning up.")
                cleanup()
            }
        }, TIMEOUT_MS)

        // 5. Configure ImageReader to receive JPEG
        val width = 640
        val height = 480
        imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2)
        imageReader.setOnImageAvailableListener({ reader ->
            isCaptured = true
            var image = reader?.acquireLatestImage()
            if (image != null) {
                try {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)

                    // Save file securely
                    val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
                    val timestampString = sdf.format(Date())
                    val filename = "intruder_${timestampString}_${UUID.randomUUID().toString().take(6)}.jpg"
                    val photoFile = File(getSelfiesDirectory(context), filename)

                    FileOutputStream(photoFile).use { out ->
                        out.write(bytes)
                    }

                    // Save log entry
                    val displaySdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val displayTimestamp = displaySdf.format(Date())
                    val newLog = IntruderSelfieLog(
                        id = UUID.randomUUID().toString(),
                        photoPath = photoFile.absolutePath,
                        dateTime = displayTimestamp,
                        appName = appName,
                        failedAttemptCount = failedAttemptCount
                    )

                    val logs = loadLogs(context).toMutableList()
                    logs.add(0, newLog)
                    saveLogs(context, logs)

                    Log.d(TAG, "Intruder selfie captured and saved securely to: ${photoFile.absolutePath}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save captured intruder image", e)
                } finally {
                    image.close()
                    cleanup()
                }
            } else {
                cleanup()
            }
        }, backgroundHandler)

        // 6. Open Camera
        try {
            cameraManager.openCamera(frontCameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    if (isCaptured) {
                        camera.close()
                        return
                    }

                    try {
                        val readerSurface = imageReader.surface
                        val targets = listOf(readerSurface)

                        // Create session
                        camera.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(session: CameraCaptureSession) {
                                captureSession = session
                                if (isCaptured) {
                                    session.close()
                                    camera.close()
                                    return
                                }

                                try {
                                    val captureRequest = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
                                        addTarget(readerSurface)
                                        // Reduce shutter sound or ensure silent where possible (Camera2 is usually quiet on ImageReader target)
                                        set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                                        set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
                                    }

                                    session.capture(captureRequest.build(), object : CameraCaptureSession.CaptureCallback() {
                                        override fun onCaptureFailed(
                                            session: CameraCaptureSession,
                                            request: CaptureRequest,
                                            failure: CaptureFailure
                                        ) {
                                            super.onCaptureFailed(session, request, failure)
                                            Log.e(TAG, "Still capture request failed")
                                            cleanup()
                                        }
                                    }, backgroundHandler)

                                } catch (e: Exception) {
                                    Log.e(TAG, "Error during capture call", e)
                                    cleanup()
                                }
                            }

                            override fun onConfigureFailed(session: CameraCaptureSession) {
                                Log.e(TAG, "Camera capture session configuration failed")
                                cleanup()
                            }
                        }, backgroundHandler)

                    } catch (e: Exception) {
                        Log.e(TAG, "Error configuring capture session", e)
                        cleanup()
                    }
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Log.w(TAG, "Camera disconnected")
                    camera.close()
                    if (camera == cameraDevice) {
                        cameraDevice = null
                    }
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e(TAG, "Camera device error: $error")
                    camera.close()
                    if (camera == cameraDevice) {
                        cameraDevice = null
                    }
                    cleanup()
                }
            }, backgroundHandler)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening camera", e)
            cleanup()
        }
    }
}
