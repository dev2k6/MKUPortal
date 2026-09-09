# MKUPortal ProGuard / R8 Rules

# Preserve Java reflection metadata and attributes
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable

# GSON Serialization and Deserialization
-dontwarn sun.misc.**
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers class * extends com.google.gson.reflect.TypeToken { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keepclassmembers class com.google.gson.reflect.TypeToken { *; }

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @com.google.gson.annotations.Expose <fields>;
}

# Keep All Data Models, DTOs and Local Storage Entities
-keep class vn.edu.mku.portal.data.network.model.** { *; }
-keepclassmembers class vn.edu.mku.portal.data.network.model.** { *; }
-keep class vn.edu.mku.portal.data.model.** { *; }
-keepclassmembers class vn.edu.mku.portal.data.model.** { *; }
-keep class vn.edu.mku.portal.data.local.** { *; }
-keepclassmembers class vn.edu.mku.portal.data.local.** { *; }
-keep class vn.edu.mku.portal.data.crash.** { *; }
-keepclassmembers class vn.edu.mku.portal.data.crash.** { *; }
-keep class vn.edu.mku.portal.data.update.** { *; }
-keepclassmembers class vn.edu.mku.portal.data.update.** { *; }

# Keep Retrofit API Service & Interfaces
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class vn.edu.mku.portal.data.network.ApiService { *; }
-keep interface vn.edu.mku.portal.data.network.ApiService { *; }

# OkHttp & Okio Rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Android Material & Support Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Application Lifecycle, Activities, WorkManager, Services
-keep class vn.edu.mku.portal.MKUApplication { *; }
-keep class vn.edu.mku.portal.service.** { *; }
-keep class vn.edu.mku.portal.ui.** { *; }

