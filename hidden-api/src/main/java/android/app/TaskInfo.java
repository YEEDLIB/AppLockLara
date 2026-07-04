package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.LocusId;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcel;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Stores information about a particular Task.
 */
public class TaskInfo {
    /**
     * The value to use when the property has not a specific value.
     */
    public static final int PROPERTY_VALUE_UNSET = -1;
    /**
     * Self-movable state is not set.
     */
    public static final int SELF_MOVABLE_UNSET = -1;
    /**
     * Self-movable state is not defined. WM core uses freeform windowing mode to decide.
     */
    public static final int SELF_MOVABLE_DEFAULT = 0;
    /**
     * Self-moving is allowed. Note that there are permission checks in addition to this flag for
     * apps calling android.app.ActivityManager.AppTask#moveTaskToFront.
     */
    public static final int SELF_MOVABLE_ALLOWED = 1;
    /**
     * Self-moving is denied.
     */
    public static final int SELF_MOVABLE_DENIED = 2;
    private static final String TAG = "TaskInfo";
    /**
     * The current configuration of the task.
     */
    @NonNull
    public final Configuration configuration = new Configuration();
    /**
     * The id of the user the task was running as if this is a leaf task. The id of the current
     * running user of the system otherwise.
     */
    public int userId;

    /**
     * The identifier for this task.
     */
    public int taskId;

    /**
     * The current effective uid of the identity of this task.
     */
    public int effectiveUid;

    /**
     * Whether or not this task has any running activities.
     */
    public boolean isRunning;

    /**
     * The base intent of the task (generally the intent that launched the task). This intent can
     * be used to relaunch the task (if it is no longer running) or brought to the front if it is.
     */
    @NonNull
    public Intent baseIntent;

    /**
     * The component of the first activity in the task, can be considered the "application" of this
     * task.
     */
    @Nullable
    public ComponentName baseActivity;

    /**
     * The component of the top activity in the task, currently showing to the user.
     */
    @Nullable
    public ComponentName topActivity;

    /**
     * The component of the target activity if this task was started from an activity alias.
     * Otherwise, this is null.
     */
    @Nullable
    public ComponentName origActivity;

    /**
     * The component of the activity that started this task (may be the component of the activity
     * alias).
     */
    @Nullable
    public ComponentName realActivity;

    /**
     * The number of activities in this task (including running).
     */
    public int numActivities;

    /**
     * The last time this task was active since boot (including time spent in sleep).
     */
    public long lastActiveTime;

    /**
     * The id of the display this task is associated with.
     */
    public int displayId;

    /**
     * The feature id of com.android.server.wm.TaskDisplayArea this task is associated with.
     */
    public int displayAreaFeatureId = 0;

    /**
     * The recent activity values for the highest activity in the stack to have set the values.
     * Activity#setTaskDescription(android.app.ActivityManager.TaskDescription).
     */
    @Nullable
    public ActivityManager.TaskDescription taskDescription;

    /**
     * The locusId of the task.
     */
    @Nullable
    public LocusId mTopActivityLocusId;

    /**
     * Whether this task supports multi windowing modes based on the device settings and the
     * root activity resizability and configuration.
     */
    public boolean supportsMultiWindow;

    /**
     * The resize mode of the task. See ActivityInfo#resizeMode.
     */
    public int resizeMode;
    /**
     * The PictureInPictureParams for the Task, if set.
     */
    @Nullable
    public PictureInPictureParams pictureInPictureParams;
    public boolean shouldDockBigOverlays;
    /**
     * The task id of the host Task of the launch-into-pip Activity, i.e., it points to the Task
     * the launch-into-pip Activity is originated from.
     */
    public int launchIntoPipHostTaskId;
    /**
     * The task id of the parent Task of the launch-into-pip Activity, i.e., if task have more than
     * one activity it will create new task for this activity, this id is the origin task id and
     * the pip activity will be reparent to origin task when it exit pip mode.
     */
    public int lastParentTaskIdBeforePip;
    /**
     * The Rect copied from DisplayCutout#getSafeInsets() if the cutout is not of
     * (LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES, LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS),
     * null otherwise.
     */
    @Nullable
    public Rect displayCutoutInsets;
    /**
     * The activity type of the top activity in this task.
     */
    public @WindowConfiguration.ActivityType int topActivityType;
    /**
     * The ActivityInfo of the top activity in this task.
     */
    @Nullable
    public ActivityInfo topActivityInfo;
    /**
     * Whether this task is resizable. Unlike resizeMode (which is what the top activity
     * supports), this is what the system actually uses for resizability based on other policy and
     * developer options.
     */
    public boolean isResizeable;
    /**
     * Minimal width of the task when it's resizeable.
     */
    public int minWidth;
    /**
     * Minimal height of the task when it's resizeable.
     */
    public int minHeight;
    /**
     * The default minimal size of the task used when a minWidth or minHeight is not specified.
     */
    public int defaultMinSize;
    /**
     * Relative position of the task's top left corner in the parent container.
     */
    public Point positionInParent;
    /**
     * The launch cookies associated with activities in this task if any.
     *
     * @see ActivityOptions setLaunchCookie(IBinder)
     */
    public ArrayList<IBinder> launchCookies = new ArrayList<>();
    /**
     * The identifier of the parent task that is created by organizer, otherwise
     * ActivityTaskManager#INVALID_TASK_ID.
     */
    public int parentTaskId;
    /**
     * Whether this task is focused on the display. This means the task receives input events that
     * target the display.
     * CAUTION: This can be true for multiple tasks especially when multiple displays are connected
     * in the system.
     */
    public boolean isFocused;
    /**
     * Whether this task is visible.
     */
    public boolean isVisible;
    /**
     * Whether this task is request visible.
     */
    public boolean isVisibleRequested;
    /**
     * Whether the top activity is to be displayed. See android.R.attr#windowNoDisplay.
     */
    public boolean isTopActivityNoDisplay;
    /**
     * Whether this task is sleeping due to sleeping display.
     */
    public boolean isSleeping;
    /**
     * Whether the top activity fillsParent() is false.
     */
    public boolean isTopActivityTransparent;
    /**
     * Whether fillsParent() is false for every activity in the tasks stack.
     */
    public boolean isActivityStackTransparent;
    /**
     * The last non-fullscreen bounds the task was launched in or resized to.
     */
    @Nullable
    public Rect lastNonFullscreenBounds;
    /**
     * The URI of the intent that generated the top-most activity opened using a URL.
     */
    @Nullable
    public Uri capturedLink;
    /**
     * The time of the last launch of the activity opened using the capturedLink.
     */
    public long capturedLinkTimestamp;
    /**
     * The requested visible types of insets.
     */
    public int requestedVisibleTypes;
    /**
     * The timestamp of the top activity's last request to show the "Open in Browser" education.
     */
    public long topActivityRequestOpenInBrowserEducationTimestamp;
    /**
     * Whether the Task should be an App Bubble.
     * Please use this with caution. This is just a short-term solution which should be migrated
     * to a more generic model vs. implying the Task is an App Bubble here.
     * <p>
     * TODO(b/407669465): remove it once migrated to the new approach
     */
    public boolean isAppBubble;
    /**
     * The top activity's main window frame if it doesn't match the top activity bounds.
     * null, otherwise.
     */
    @Nullable
    public Rect topActivityMainWindowFrame;
    /**
     * Whether the Task should be an Interactive Picture-in-Picture window.
     */
    public boolean isInteractivePictureInPicture;

    TaskInfo() {
        // stub
    }

    public TaskInfo(Parcel source) {
        // stub
    }

    /**
     * Returns the task id.
     */
    public int getTaskId() {
        return 0;
    }

    /**
     * Whether this task is visible.
     */
    public boolean isVisible() {
        return false;
    }

    @NonNull
    public Configuration getConfiguration() {
        return null;
    }

    @Nullable
    public PictureInPictureParams getPictureInPictureParams() {
        return null;
    }

    public boolean shouldDockBigOverlays() {
        return false;
    }

    @WindowConfiguration.WindowingMode
    public int getWindowingMode() {
        return WindowConfiguration.WINDOWING_MODE_UNDEFINED;
    }

    public boolean isFreeform() {
        return false;
    }

    @WindowConfiguration.ActivityType
    public int getActivityType() {
        return WindowConfiguration.ACTIVITY_TYPE_UNDEFINED;
    }

    public void addLaunchCookie(IBinder cookie) {
        // stub
    }

    /**
     * @return true if this task contains the launch cookie.
     */
    public boolean containsLaunchCookie(@NonNull IBinder cookie) {
        return false;
    }

    /**
     * @return The parent task id of this task.
     */
    public int getParentTaskId() {
        return 0;
    }

    public boolean hasParentTask() {
        return false;
    }

    /**
     * @return The id of the display this task is associated with.
     */
    public int getDisplayId() {
        return 0;
    }

    /**
     * Returns true if the parameters that are important for task organizers are equal
     * between this TaskInfo and that.
     */
    public boolean equalsForTaskOrganizer(@Nullable TaskInfo that) {
        return false;
    }

    /**
     * @return true if parameters that are important for size compat have changed.
     */
    public boolean equalsForCompatUi(@Nullable TaskInfo that) {
        return false;
    }

    /**
     * Reads the TaskInfo from a parcel.
     */
    void readTaskFromParcel(Parcel source) {
        // stub
    }

    /**
     * Writes the TaskInfo to a parcel.
     */
    public void writeTaskToParcel(Parcel dest, int flags) {
        // stub
    }

    @Override
    public String toString() {
        return "TaskInfo{...}";
    }

    @IntDef(
            value = {
                    SELF_MOVABLE_UNSET,
                    SELF_MOVABLE_DEFAULT,
                    SELF_MOVABLE_ALLOWED,
                    SELF_MOVABLE_DENIED,
            })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SelfMovable {
    }
}
