# MKUPortal ProGuard / R8 Rules

# Keep Data Models and GSON Serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class vn.edu.mku.portal.data.network.model.** { *; }
-keep class vn.edu.mku.portal.data.model.** { *; }

# Keep Retrofit API Service & Models
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class vn.edu.mku.portal.data.network.ApiService { *; }

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Android Material & Support Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Application Activities, Services, Application class
-keep class vn.edu.mku.portal.MKUApplication { *; }
-keep class vn.edu.mku.portal.service.** { *; }
-keep class vn.edu.mku.portal.data.crash.** { *; }
