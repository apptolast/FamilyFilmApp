# Keep stack traces readable in release builds
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Domain models — preserved for kotlinx.serialization, Room and Firestore mapping
-keep class com.apptolast.familyfilmapp.model.** { *; }

# kotlinx.serialization (companion objects + descriptors)
-keepclasseswithmembers class **$$serializer { *; }
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }

# RevenueCat
-keep class com.revenuecat.purchases.** { *; }

# Google Mobile Ads (kept lightweight; AdMob SDK ships its own consumer rules)
-dontwarn com.google.android.gms.**

# kotlin-xmlpull
-dontwarn org.xmlpull.v1.**
-dontnote org.xmlpull.v1.**
-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }

# Required on JVM for JNA-based integrations.
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

# Required when using FileKit Dialogs on Linux (XDG Desktop Portal / DBus).
-keep class org.freedesktop.dbus.** { *; }
-keep class io.github.vinceglb.filekit.dialogs.platform.xdg.** { *; }
-keepattributes Signature,InnerClasses,RuntimeVisibleAnnotations
