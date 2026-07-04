package android.app;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.proto.ProtoOutputStream;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class that contains windowing configuration/state for other objects that contain windows directly
 * or indirectly. E.g. Activities, Task, Displays, ...
 *
 * <p>The test class is com.android.server.wm.WindowConfigurationTests which must be kept
 * up-to-date and ran anytime changes are made to this class.
 */
public class WindowConfiguration implements Parcelable, Comparable<WindowConfiguration> {

    /**
     * Rotation is not defined, use the parent containers rotation.
     */
    public static final int ROTATION_UNDEFINED = -1;
    /**
     * Windowing mode is currently not defined.
     */
    public static final int WINDOWING_MODE_UNDEFINED = 0;
    /**
     * Occupies the full area of the screen or the parent container.
     */
    public static final int WINDOWING_MODE_FULLSCREEN = 1;
    /**
     * Always on-top (always visible). of other siblings in its parent container.
     */
    public static final int WINDOWING_MODE_PINNED = 2;
    /**
     * Can be freely resized within its parent container.
     */
    public static final int WINDOWING_MODE_FREEFORM = 5;
    /**
     * Generic multi-window with no presentation attribution from the window manager.
     */
    public static final int WINDOWING_MODE_MULTI_WINDOW = 6;
    /**
     * Activity type is currently not defined.
     */
    public static final int ACTIVITY_TYPE_UNDEFINED = 0;
    /**
     * Standard activity type. Nothing special about the activity...
     */
    public static final int ACTIVITY_TYPE_STANDARD = 1;
    /**
     * Home/Launcher activity type.
     */
    public static final int ACTIVITY_TYPE_HOME = 2;
    /**
     * Recents/Overview activity type. There is only one activity with this type in the system.
     */
    public static final int ACTIVITY_TYPE_RECENTS = 3;
    /**
     * Assistant activity type.
     */
    public static final int ACTIVITY_TYPE_ASSISTANT = 4;
    /**
     * Dream activity type.
     */
    public static final int ACTIVITY_TYPE_DREAM = 5;
    /**
     * Bit that indicates that the mBounds changed.
     */
    public static final int WINDOW_CONFIG_BOUNDS = 1 << 0;
    /**
     * Bit that indicates that the mAppBounds changed.
     */
    public static final int WINDOW_CONFIG_APP_BOUNDS = 1 << 1;
    /**
     * Bit that indicates that the mMaxBounds changed.
     */
    public static final int WINDOW_CONFIG_MAX_BOUNDS = 1 << 2;
    /**
     * Bit that indicates that the mWindowingMode changed.
     */
    public static final int WINDOW_CONFIG_WINDOWING_MODE = 1 << 3;
    /**
     * Bit that indicates that the mActivityType changed.
     */
    public static final int WINDOW_CONFIG_ACTIVITY_TYPE = 1 << 4;
    /**
     * Bit that indicates that the mAlwaysOnTop changed.
     */
    public static final int WINDOW_CONFIG_ALWAYS_ON_TOP = 1 << 5;
    /**
     * Bit that indicates that the mRotation changed.
     */
    public static final int WINDOW_CONFIG_ROTATION = 1 << 6;
    /**
     * Bit that indicates that the apparent-display changed.
     */
    public static final int WINDOW_CONFIG_DISPLAY_ROTATION = 1 << 7;
    @NonNull
    public static final Creator<WindowConfiguration> CREATOR = new Creator<>() {
        @Override
        public WindowConfiguration createFromParcel(Parcel in) {
            return new WindowConfiguration(in);
        }

        @Override
        public WindowConfiguration[] newArray(int size) {
            return new WindowConfiguration[size];
        }
    };
    /**
     * Always on top is currently not defined.
     */
    private static final int ALWAYS_ON_TOP_UNDEFINED = 0;
    /**
     * Always on top is currently on for this configuration.
     */
    private static final int ALWAYS_ON_TOP_ON = 1;
    /**
     * Always on top is currently off for this configuration.
     */
    private static final int ALWAYS_ON_TOP_OFF = 2;
    /**
     * Bounds that can differ from app bounds, which may include things such as insets.
     * <p>
     * TODO: Investigate combining with mAppBounds. Can the latter be a product of the
     * former?
     */
    @NonNull
    private final Rect mBounds = new Rect();
    /**
     * The maximum Rect bounds that an app can expect. It is used to report value of
     * WindowManager#getMaximumWindowMetrics().
     */
    @NonNull
    private final Rect mMaxBounds = new Rect();
    /**
     * android.graphics.Rect defining app bounds. The dimensions override usages of
     * DisplayInfo#appHeight and DisplayInfo#appWidth and mirrors these values at
     * the display level. Lower levels can override these values to provide custom bounds to enforce
     * features such as a max aspect ratio.
     */
    @Nullable
    private Rect mAppBounds;
    /**
     * The rotation of this window's apparent display. This can differ from mRotation in some
     * situations (like letterbox).
     */
    private int mDisplayRotation = ROTATION_UNDEFINED;
    /**
     * The current rotation of this window container relative to the default
     * orientation of the display it is on (regardless of how deep in the hierarchy
     * it is). It is used by the configuration hierarchy to apply rotation-dependent
     * policy during bounds calculation.
     */
    private int mRotation = ROTATION_UNDEFINED;
    /**
     * The current windowing mode of the configuration.
     */
    @WindowingMode
    private int mWindowingMode;
    /**
     * The current activity type of the configuration.
     */
    @ActivityType
    private int mActivityType;
    /**
     * The current always on top status of the configuration.
     */
    @AlwaysOnTop
    private int mAlwaysOnTop;

    public WindowConfiguration() {
        unset();
    }

    public WindowConfiguration(@NonNull WindowConfiguration configuration) {
        setTo(configuration);
    }

    private WindowConfiguration(@NonNull Parcel in) {
        readFromParcel(in);
    }

    /**
     * Size based scaling helper.
     */
    private static void scaleBounds(float scale, @NonNull Rect bounds) {
        // stub
    }

    /**
     * Returns true if the windowingMode represents a floating window.
     */
    public static boolean isFloating(@WindowingMode int windowingMode) {
        return false;
    }

    /**
     * Returns true if the windowingMode represents a window in multi-window mode.
     */
    public static boolean inMultiWindowMode(@WindowingMode int windowingMode) {
        return false;
    }

    /**
     * Check if activity type supports split screen.
     */
    public static boolean supportSplitScreenWindowingMode(@ActivityType int activityType) {
        return false;
    }

    /**
     * Checks if the two Configurations are equal to each other for the fields that are read
     * by Display.
     */
    public static boolean areConfigurationsEqualForDisplay(@NonNull Configuration newConfig,
                                                           @NonNull Configuration oldConfig) {
        return false;
    }

    /**
     * Convert windowing mode to string.
     */
    @NonNull
    public static String windowingModeToString(@WindowingMode int windowingMode) {
        return String.valueOf(windowingMode);
    }

    /**
     * Convert activity type to string.
     */
    @NonNull
    public static String activityTypeToString(@ActivityType int applicationType) {
        return String.valueOf(applicationType);
    }

    /**
     * Convert always-on-top status to string.
     */
    @NonNull
    public static String alwaysOnTopToString(@AlwaysOnTop int alwaysOnTop) {
        return String.valueOf(alwaysOnTop);
    }

    /**
     * Convert diff mask to string.
     */
    @NonNull
    public static String diffToString(@WindowConfig long diff) {
        return String.valueOf(diff);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // stub
    }

    public void readFromParcel(@NonNull Parcel source) {
        // stub
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @see #setMaxBounds(Rect)
     */
    public void setMaxBounds(int left, int top, int right, int bottom) {
        // stub
    }

    /**
     * Unsets always-on-top to undefined.
     */
    public void unsetAlwaysOnTop() {
        // stub
    }

    /**
     * @see #setAppBounds(Rect)
     * @see #getAppBounds()
     */
    public void setAppBounds(int left, int top, int right, int bottom) {
        // stub
    }

    /**
     * @see #setAppBounds(Rect)
     */
    @Nullable
    public Rect getAppBounds() {
        return null;
    }

    /**
     * Sets the app bounds to the provided Rect.
     * Passing null sets the bounds to null.
     *
     * @param rect the new app bounds value.
     * @see #getAppBounds()
     */
    public void setAppBounds(@Nullable Rect rect) {
        // stub
    }

    /**
     * @see #setBounds(Rect)
     */
    @NonNull
    public Rect getBounds() {
        return mBounds;
    }

    /**
     * Sets the bounds to the provided Rect.
     * Passing null sets the bounds Rect to empty.
     *
     * @param rect the new bounds value.
     */
    public void setBounds(@Nullable Rect rect) {
        // stub
    }

    /**
     * @see #setMaxBounds(Rect)
     */
    @NonNull
    public Rect getMaxBounds() {
        return mMaxBounds;
    }

    /**
     * Sets the maximum bounds to the provided Rect.
     * Passing null sets the bounds Rect to empty.
     *
     * @param rect the new max bounds value.
     * @see #getMaxBounds()
     */
    public void setMaxBounds(@Nullable Rect rect) {
        // stub
    }

    /**
     * Gets the display rotation.
     */
    public int getDisplayRotation() {
        return ROTATION_UNDEFINED;
    }

    /**
     * Sets the display rotation.
     */
    public void setDisplayRotation(int rotation) {
        // stub
    }

    public int getRotation() {
        return ROTATION_UNDEFINED;
    }

    public void setRotation(int rotation) {
        // stub
    }

    @WindowingMode
    public int getWindowingMode() {
        return WINDOWING_MODE_UNDEFINED;
    }

    public void setWindowingMode(@WindowingMode int windowingMode) {
        // stub
    }

    @ActivityType
    public int getActivityType() {
        return ACTIVITY_TYPE_UNDEFINED;
    }

    public void setActivityType(@ActivityType int activityType) {
        // stub
    }

    public void setTo(WindowConfiguration other) {
        // stub
    }

    /**
     * Set this object to completely undefined.
     */
    public void unset() {
        // stub
    }

    /**
     * Set default values for all fields.
     */
    public void setToDefaults() {
        // stub
    }

    /**
     * Scale all bounds by the given factor.
     */
    public void scale(float scale) {
        // stub
    }

    /**
     * Copies the fields from delta into this Configuration object, keeping
     * track of which ones have changed.
     *
     * @return a bit mask of the changed fields
     */
    @WindowConfig
    public int updateFrom(@NonNull WindowConfiguration delta) {
        return WindowConfiguration.WINDOW_CONFIG_BOUNDS;
    }

    /**
     * Copies the fields specified by mask from delta into this Configuration object.
     */
    public void setTo(@NonNull WindowConfiguration delta, @WindowConfig int mask) {
        // stub
    }

    /**
     * Return a bit mask of the differences between this Configuration object and the given one.
     *
     * @param other            The configuration to diff against.
     * @param compareUndefined If undefined values should be compared.
     * @return Returns a bit mask indicating which configuration values has changed.
     */
    @WindowConfig
    public long diff(@NonNull WindowConfiguration other, boolean compareUndefined) {
        return WindowConfiguration.WINDOW_CONFIG_BOUNDS;
    }

    @Override
    public int compareTo(WindowConfiguration that) {
        return 0;
    }

    @Override
    public boolean equals(@Nullable Object that) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "WindowConfiguration{...}";
    }

    /**
     * Write to a protocol buffer output stream.
     * Protocol buffer message definition at
     * android.internal.perfetto.protos.WindowConfiguration.WindowConfigurationProto
     *
     * @param protoOutputStream Stream to write the WindowConfiguration object to.
     * @param fieldId           Field Id of the WindowConfiguration as defined in the parent message
     */
    public void dumpDebug(@NonNull ProtoOutputStream protoOutputStream, long fieldId) {
        // stub
    }

    /**
     * Returns true if the activities associated with this window configuration display a shadow
     * around their border.
     */
    public boolean hasWindowShadow() {
        return false;
    }

    /**
     * Returns true if the tasks associated with this window configuration can be resized
     * independently of their parent container.
     */
    public boolean canResizeTask() {
        return false;
    }

    /**
     * Returns true if the tasks associated with this window configuration are floating.
     */
    public boolean tasksAreFloating() {
        return false;
    }

    /**
     * Returns true if the windows associated with this window configuration can receive input keys.
     */
    public boolean canReceiveKeys() {
        return false;
    }

    /**
     * Returns true if the container associated with this window configuration is always-on-top of
     * its siblings.
     */
    public boolean isAlwaysOnTop() {
        return false;
    }

    /**
     * Sets whether this window should be always on top.
     *
     * @param alwaysOnTop true to set window always on top, otherwise false
     */
    public void setAlwaysOnTop(boolean alwaysOnTop) {
        // stub
    }

    private void setAlwaysOnTop(@AlwaysOnTop int alwaysOnTop) {
        // stub
    }

    /**
     * Returns true if windows in this container should be given move animations by default.
     */
    public boolean hasMovementAnimations() {
        return false;
    }

    /**
     * Returns true if this container can be put in WINDOWING_MODE_MULTI_WINDOW
     * windowing mode based on its current state.
     */
    public boolean supportSplitScreenWindowingMode() {
        return false;
    }

    @IntDef(value = {
            WINDOWING_MODE_UNDEFINED,
            WINDOWING_MODE_FULLSCREEN,
            WINDOWING_MODE_MULTI_WINDOW,
            WINDOWING_MODE_PINNED,
            WINDOWING_MODE_FREEFORM,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WindowingMode {
    }

    @IntDef(value = {
            ACTIVITY_TYPE_UNDEFINED,
            ACTIVITY_TYPE_STANDARD,
            ACTIVITY_TYPE_HOME,
            ACTIVITY_TYPE_RECENTS,
            ACTIVITY_TYPE_ASSISTANT,
            ACTIVITY_TYPE_DREAM,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActivityType {
    }

    @IntDef(value = {
            ALWAYS_ON_TOP_UNDEFINED,
            ALWAYS_ON_TOP_ON,
            ALWAYS_ON_TOP_OFF,
    })
    private @interface AlwaysOnTop {
    }

    @IntDef(value = {
            WINDOW_CONFIG_BOUNDS,
            WINDOW_CONFIG_APP_BOUNDS,
            WINDOW_CONFIG_MAX_BOUNDS,
            WINDOW_CONFIG_WINDOWING_MODE,
            WINDOW_CONFIG_ACTIVITY_TYPE,
            WINDOW_CONFIG_ALWAYS_ON_TOP,
            WINDOW_CONFIG_ROTATION,
            WINDOW_CONFIG_DISPLAY_ROTATION,
    })
    public @interface WindowConfig {
    }
}
