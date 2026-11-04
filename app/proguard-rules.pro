# Moshi / Retrofit DTOs are reflected on; keep them if you enable minify.
-keep class com.sideline.app.data.api.** { *; }
-keepclassmembers class com.sideline.app.data.api.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
