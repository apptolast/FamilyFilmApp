# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keepattributes Signature
#-keepattributes *Annotation*

-keep class com.apptolast.familyfilmapp.model.local.** { *; }
-keep class com.apptolast.familyfilmapp.model.local.types.** { *; }



#-keep class androidx.compose.ui.tooling.PreviewActivity { <init>(); }
#-keep class androidx.core.app.CoreComponentFactory { <init>(); }
#-keep class androidx.credentials.playservices.CredentialProviderMetadataHolder { <init>(); }
#-keep class androidx.credentials.playservices.HiddenActivity { <init>(); }
#-keep class androidx.profileinstaller.ProfileInstallReceiver { <init>(); }
#-keep class androidx.room.MultiInstanceInvalidationService { <init>(); }
#-keep class androidx.work.impl.background.systemalarm.ConstraintProxy$BatteryChargingProxy { <init>(); }
#-keep class androidx.work.impl.background.systemalarm.ConstraintProxy$BatteryNotLowProxy { <init>(); }
#-keep class androidx.work.impl.background.systemalarm.ConstraintProxy$NetworkStateProxy { <init>(); }
#-keep class androidx.work.impl.background.systemalarm.ConstraintProxy$StorageNotLowProxy { <init>(); }
#-keep class androidx.work.impl.background.systemalarm.ConstraintProxyUpdateReceiver { <init>(); }
#-keep class androidx.work.impl.background.systemalarm.RescheduleReceiver { <init>(); }
#-keep class androidx.work.impl.background.systemalarm.SystemAlarmService { <init>(); }
#-keep class androidx.work.impl.background.systemjob.SystemJobService { <init>(); }
#-keep class androidx.work.impl.diagnostics.DiagnosticsReceiver { <init>(); }
#-keep class androidx.work.impl.foreground.SystemForegroundService { <init>(); }
#-keep class androidx.work.impl.utils.ForceStopRunnable$BroadcastReceiver { <init>(); }
#-keep class com.apptolast.familyfilmapp.FamilyFilmApp { <init>(); }
#-keep class com.apptolast.familyfilmapp.MainActivity { <init>(); }
#-keep class com.google.android.datatransport.runtime.backends.TransportBackendDiscovery { <init>(); }
#-keep class com.google.android.datatransport.runtime.scheduling.jobscheduling.AlarmManagerSchedulerBroadcastReceiver { <init>(); }
#-keep class com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService { <init>(); }
#-keep class com.google.android.gms.auth.api.signin.RevocationBoundService { <init>(); }
#-keep class com.google.android.gms.auth.api.signin.internal.SignInHubActivity { <init>(); }
#-keep class com.google.android.gms.common.api.GoogleApiActivity { <init>(); }
#-keep class com.google.android.gms.measurement.AppMeasurementJobService { <init>(); }
#-keep class com.google.android.gms.measurement.AppMeasurementReceiver { <init>(); }
#-keep class com.google.android.gms.measurement.AppMeasurementService { <init>(); }
#-keep class com.google.android.play.core.common.PlayCoreDialogWrapperActivity { <init>(); }
#-keep class com.google.firebase.auth.internal.GenericIdpActivity { <init>(); }
#-keep class com.google.firebase.auth.internal.RecaptchaActivity { <init>(); }
#-keep class com.google.firebase.components.ComponentDiscoveryService { <init>(); }
#-keep class com.google.firebase.provider.FirebaseInitProvider { <init>(); }
#-keep class com.google.firebase.sessions.SessionLifecycleService { <init>(); }
#-keep class androidx.browser.browseractions.BrowserActionsFallbackMenuView { <init>(android.content.Context, android.util.AttributeSet); }

