package com.applocklara.applocklara.core.navigation

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.applocklara.applocklara.AppLockApplication
import com.applocklara.applocklara.core.utils.LogUtils
import com.applocklara.applocklara.data.repository.PreferencesRepository
import com.applocklara.applocklara.features.antiuninstall.ui.AntiUninstallScreen
import com.applocklara.applocklara.features.appintro.ui.AppIntroScreen
import com.applocklara.applocklara.features.applist.ui.MainScreen
import com.applocklara.applocklara.features.lockscreen.ui.AlphanumericPasswordOverlayScreen
import com.applocklara.applocklara.features.lockscreen.ui.PinPasswordOverlayScreen
import com.applocklara.applocklara.features.lockscreen.ui.PatternLockScreen
import com.applocklara.applocklara.features.setpassword.ui.AlphanumericSetPasswordScreen
import com.applocklara.applocklara.features.setpassword.ui.PatternSetPasswordScreen
import com.applocklara.applocklara.features.setpassword.ui.SetPasswordScreen
import com.applocklara.applocklara.features.settings.ui.SettingsScreen
import com.applocklara.applocklara.features.triggerexclusions.ui.TriggerExclusionsScreen
import com.applocklara.applocklara.features.intruder.ui.IntruderLogsScreen
import com.applocklara.applocklara.features.intruder.ui.IntruderSettingsScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    val application = LocalContext.current.applicationContext as AppLockApplication

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
                    scaleIn(initialScale = SCALE_INITIAL, animationSpec = tween(ANIMATION_DURATION))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
                    scaleIn(initialScale = SCALE_INITIAL, animationSpec = tween(ANIMATION_DURATION))
        },
    ) {
        composable(Screen.AppIntro.route) {
            AppIntroScreen(navController)
        }

        composable(Screen.SetPassword.route) {
            SetPasswordScreen(navController, isFirstTimeSetup = true)
        }

        composable(Screen.ChangePassword.route) {
            when (application.appLockRepository.getLockType()) {
                PreferencesRepository.LOCK_TYPE_PATTERN -> {
                    PatternSetPasswordScreen(navController, false)
                }
                PreferencesRepository.LOCK_TYPE_PASSWORD -> {
                    AlphanumericSetPasswordScreen(navController, false)
                }
                else -> {
                    SetPasswordScreen(navController, isFirstTimeSetup = false)
                }
            }
        }

        composable(Screen.SetPasswordPattern.route) {
            PatternSetPasswordScreen(navController, isFirstTimeSetup = true)
        }

        composable(Screen.SetPasswordAlphanumeric.route) {
            AlphanumericSetPasswordScreen(navController, isFirstTimeSetup = true)
        }

        composable(Screen.Main.route) {
            MainScreen(navController)
        }

        composable(Screen.PasswordOverlay.route) {
            val context = LocalContext.current as FragmentActivity
            val lockType = application.appLockRepository.getLockType()

            when (lockType) {
                PreferencesRepository.LOCK_TYPE_PATTERN -> {
                    PatternLockScreen(
                        fromMainActivity = true,
                        onPatternAttempt = { pattern ->
                            val isValid = application.appLockRepository.validatePattern(pattern)
                            if (isValid) {
                                handleAuthenticationSuccess(navController)
                            }
                            isValid
                        },
                        onBiometricAuth = {
                            handleBiometricAuthentication(context, navController)
                        }
                    )
                }

                PreferencesRepository.LOCK_TYPE_PASSWORD -> {
                    AlphanumericPasswordOverlayScreen(
                        showBiometricButton = application.appLockRepository.isBiometricAuthEnabled(),
                        fromMainActivity = true,
                        onBiometricAuth = {
                            handleBiometricAuthentication(context, navController)
                        },
                        onAuthSuccess = {
                            handleAuthenticationSuccess(navController)
                        }
                    )
                }

                else -> {
                    PinPasswordOverlayScreen(
                        showBiometricButton = application.appLockRepository.isBiometricAuthEnabled(),
                        fromMainActivity = true,
                        onBiometricAuth = {
                            handleBiometricAuthentication(context, navController)
                        },
                        onAuthSuccess = {
                            handleAuthenticationSuccess(navController)
                        }
                    )
                }
            }
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Screen.TriggerExclusions.route) {
            TriggerExclusionsScreen(navController)
        }

        composable(Screen.AntiUninstall.route) {
            AntiUninstallScreen(navController)
        }

        composable(Screen.IntruderLogs.route) {
            IntruderLogsScreen(navController)
        }

        composable(Screen.IntruderSettings.route) {
            IntruderSettingsScreen(navController)
        }
    }
}

private fun handleBiometricAuthentication(
    context: FragmentActivity,
    navController: NavHostController
) {
    try {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            context,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.w(TAG, "Biometric authentication error: $errString ($errorCode)")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    LogUtils.d(TAG, "Biometric authentication succeeded")
                    navigateToMain(navController)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w(TAG, "Biometric authentication failed (not recognized)")
                }
            }
        )

        val promptInfo = createBiometricPromptInfo()
        biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
        Log.e(TAG, "Error during biometric authentication", e)
    }
}

private fun createBiometricPromptInfo(): BiometricPrompt.PromptInfo {
    return BiometricPrompt.PromptInfo.Builder()
        .setTitle(BIOMETRIC_TITLE)
        .setSubtitle(BIOMETRIC_SUBTITLE)
        .setNegativeButtonText(BIOMETRIC_NEGATIVE_BUTTON)
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
        )
        .setConfirmationRequired(false)
        .build()
}

private fun handleAuthenticationSuccess(navController: NavHostController) {
    if (navController.previousBackStackEntry != null) {
        navController.popBackStack()
    } else {
        navigateToMain(navController)
    }
}

private fun navigateToMain(navController: NavHostController) {
    navController.navigate(Screen.Main.route) {
        popUpTo(Screen.PasswordOverlay.route) { inclusive = true }
    }
}

private const val TAG = "AppNavHost"
private const val ANIMATION_DURATION = 400
private const val SCALE_INITIAL = 0.9f
private const val BIOMETRIC_TITLE = "Confirm password"
private const val BIOMETRIC_SUBTITLE = "Confirm biometric to continue"
private const val BIOMETRIC_NEGATIVE_BUTTON = "Use PIN"
