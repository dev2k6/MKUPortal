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
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @com.google.gson.annotations.Expose <fields>;
}

# Retrofit & OkHttp & Okio Rules
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }
-keep interface okio.** { *; }

# Android Lifecycle & ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# Android Material & UI Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-keep class androidx.appcompat.** { *; }
-dontwarn androidx.appcompat.**
-keep class androidx.constraintlayout.** { *; }
-keep class androidx.recyclerview.widget.** { *; }

# Application Classes, Data Models, Repositories, UI
-keep class vn.edu.mku.portal.MKUApplication { *; }
-keep class vn.edu.mku.portal.data.** { *; }
-keepclassmembers class vn.edu.mku.portal.data.** { *; }
-keep class vn.edu.mku.portal.domain.** { *; }
-keepclassmembers class vn.edu.mku.portal.domain.** { *; }
-keep class vn.edu.mku.portal.service.** { *; }
-keepclassmembers class vn.edu.mku.portal.service.** { *; }
-keep class vn.edu.mku.portal.ui.** { *; }
-keepclassmembers class vn.edu.mku.portal.ui.** { *; }


