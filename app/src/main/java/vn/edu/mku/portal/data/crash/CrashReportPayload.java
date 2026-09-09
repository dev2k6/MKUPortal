package vn.edu.mku.portal.data.crash;

import com.google.gson.annotations.SerializedName;

public class CrashReportPayload {

    @SerializedName("app_name")
    private String appName;

    @SerializedName("version_name")
    private String versionName;

    @SerializedName("version_code")
    private int versionCode;

    @SerializedName("build_type")
    private String buildType;

    @SerializedName("student_id")
    private String studentId;

    @SerializedName("device_manufacturer")
    private String deviceManufacturer;

    @SerializedName("device_model")
    private String deviceModel;

    @SerializedName("android_version")
    private String androidVersion;

    @SerializedName("sdk_int")
    private int sdkInt;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("active_screen")
    private String activeScreen;

    @SerializedName("thread_name")
    private String threadName;

    @SerializedName("exception_class")
    private String exceptionClass;

    @SerializedName("exception_message")
    private String exceptionMessage;

    @SerializedName("stack_trace")
    private String stackTrace;

    @SerializedName("available_ram_mb")
    private long availableRamMb;

    @SerializedName("total_ram_mb")
    private long totalRamMb;

    @SerializedName("network_type")
    private String networkType;

    @SerializedName("is_fatal")
    private boolean isFatal;

    public CrashReportPayload() {}

    // Getters and Setters
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }

    public int getVersionCode() { return versionCode; }
    public void setVersionCode(int versionCode) { this.versionCode = versionCode; }

    public String getBuildType() { return buildType; }
    public void setBuildType(String buildType) { this.buildType = buildType; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getDeviceManufacturer() { return deviceManufacturer; }
    public void setDeviceManufacturer(String deviceManufacturer) { this.deviceManufacturer = deviceManufacturer; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getAndroidVersion() { return androidVersion; }
    public void setAndroidVersion(String androidVersion) { this.androidVersion = androidVersion; }

    public int getSdkInt() { return sdkInt; }
    public void setSdkInt(int sdkInt) { this.sdkInt = sdkInt; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getActiveScreen() { return activeScreen; }
    public void setActiveScreen(String activeScreen) { this.activeScreen = activeScreen; }

    public String getThreadName() { return threadName; }
    public void setThreadName(String threadName) { this.threadName = threadName; }

    public String getExceptionClass() { return exceptionClass; }
    public void setExceptionClass(String exceptionClass) { this.exceptionClass = exceptionClass; }

    public String getExceptionMessage() { return exceptionMessage; }
    public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }

    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

    public long getAvailableRamMb() { return availableRamMb; }
    public void setAvailableRamMb(long availableRamMb) { this.availableRamMb = availableRamMb; }

    public long getTotalRamMb() { return totalRamMb; }
    public void setTotalRamMb(long totalRamMb) { this.totalRamMb = totalRamMb; }

    public String getNetworkType() { return networkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }

    public boolean isFatal() { return isFatal; }
    public void setFatal(boolean fatal) { isFatal = fatal; }
}
