import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.ktlint.jlleitschuh)
    alias(libs.plugins.mokkery)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun localProperty(name: String): String = localProperties.getProperty(name) ?: ""

kotlin {
    androidTarget {
        // GitLive and kotlin.uuid publish bytecode built at JVM 17.
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
        iosTarget.compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
                }
            }
        }

//        // Cinterop fails without `xcode.frameworks.path` in local.properties pointing at the resolved SPM frameworks.
//        val xcodeFrameworksPath = localProperty("xcode.frameworks.path").takeIf { it.isNotBlank() }
//        if (xcodeFrameworksPath != null) {
//            iosTarget.compilations.getByName("main").cinterops {
//                listOf("GoogleMobileAds", "RevenueCat", "GoogleSignIn").forEach { name ->
//                    create(name) {
//                        defFile(project.file("src/nativeInterop/cinterop/$name.def"))
//                        compilerOpts("-F$xcodeFrameworksPath")
//                        extraOpts("-compiler-option", "-F$xcodeFrameworksPath")
//                    }
//                }
//            }
//        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation
            implementation(libs.androidx.navigation.compose)

            // Paging (multiplatform port)
            implementation(libs.paging.common)
            implementation(libs.paging.compose)

            // Kotlinx
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // HTTP
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            // DI (Koin)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Room runtime (annotation processors are wired separately via KSP)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            // Settings (KMP key-value store)
            implementation(libs.multiplatform.settings.noArg)
            implementation(libs.multiplatform.settings.coroutines)

            // Images
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            // Firebase (multiplatform via GitLive)
            implementation(libs.firebase.gitlive.common)
            implementation(libs.firebase.gitlive.auth)
            implementation(libs.firebase.gitlive.firestore)
            implementation(libs.firebase.gitlive.functions)
            implementation(libs.firebase.gitlive.analytics)
            implementation(libs.firebase.gitlive.crashlytics)
            // App Check is not in GitLive; provider factories are installed natively per platform.
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.koin.test)
            implementation(libs.turbine)
            implementation(libs.mokkery.runtime)
        }

        androidMain.dependencies {
            // Compose tooling preview (Android only — used by @Preview rendering)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)

            // Coroutines Android dispatcher
            implementation(libs.kotlinx.coroutines.android)

            // Ktor engine
            implementation(libs.ktor.client.okhttp)

            // Koin Android
            implementation(libs.koin.android)

            // Native Firebase Android (App Check provider factories — not in GitLive)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.appcheck.playintegrity)
            // App Check Debug is debug-only; declared below since androidMain.dependencies has no debugImplementation.

            // Google Sign-In (Credential Manager)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.playservices.auth)
            implementation(libs.googleid)

            // AdMob
            implementation(libs.play.services.ads)
            implementation(libs.user.messaging.platform)

            // RevenueCat
            implementation(libs.revenuecat.purchases)

            // Play In-App Review
            implementation(libs.play.review.ktx)

            // Splash + lifecycle
            implementation(libs.androidx.splashscreen)
            implementation(libs.androidx.lifecycle.process)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlin.testJunit)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.espresso.core)
            implementation(libs.uitest.junit4.android)
            // ui-test-manifest is registered as debugImplementation at the project level
            // below (KMP source-set DSL has no debugImplementation).
        }
    }
}

android {
    namespace = "com.apptolast.familyfilmapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.apptolast.familyfilmapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        // Release builds via Fastlane override these with -PappVersionCode/-PappVersionName.
        versionCode = (project.findProperty("appVersionCode") as String?)?.toInt() ?: 30
        versionName = (project.findProperty("appVersionName") as String?) ?: "1.1.0"

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "admob_app_id", localProperty("ADMOB_APPLICATION_ID"))
    }

    buildFeatures {
        compose = true
    }

    // CI writes signing.* into local.properties before invoking Fastlane.
    signingConfigs {
        create("release") {
            val storeFilePath = localProperty("signing.storeFile")
            if (storeFilePath.isNotBlank()) {
                storeFile = file(storeFilePath)
                storePassword = localProperty("signing.storePassword")
                keyAlias = localProperty("signing.keyAlias")
                keyPassword = localProperty("signing.keyPassword")
            }
        }
    }

    buildTypes {
        getByName("debug") {
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            // Only attach release signing when configured, so assembleRelease works without secrets.
            if (localProperty("signing.storeFile").isNotBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            merges.add("META-INF/LICENSE-notice.md")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

// Room KSP per target + Firebase App Check Debug (no debugImplementation in androidMain.dependencies).
dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("debugImplementation", libs.firebase.appcheck.debug)
    // ui-test-manifest provides the empty Activity createComposeRule uses;
    // must be debugImplementation because connectedAndroidTest runs against debug.
    add("debugImplementation", libs.uitest.manifest)
}

buildConfig {
    packageName("com.apptolast.familyfilmapp")
    useKotlinOutput { internalVisibility = false }

    // TODO: switch to "release" (or wire a variant-aware flavorConfig) before publishing.
    buildConfigField("BUILD_TYPE", "debug")
    buildConfigField("WEB_ID_CLIENT", localProperty("WEB_ID_CLIENT"))
    buildConfigField("TMDB_ACCESS_TOKEN", localProperty("TMDB_ACCESS_TOKEN"))
    buildConfigField("ADMOB_APPLICATION_ID", localProperty("ADMOB_APPLICATION_ID"))
    buildConfigField("ADMOB_BOTTOM_BANNER_ID", localProperty("ADMOB_BOTTOM_BANNER_ID"))
    buildConfigField("ADMOB_APP_OPEN_ID", localProperty("ADMOB_APP_OPEN_ID"))
    buildConfigField("ADMOB_NATIVE_HOME_ID", localProperty("ADMOB_NATIVE_HOME_ID"))
    buildConfigField("REVENUECAT_PLAY_SDK_KEY", localProperty("REVENUECAT_PLAY_SDK_KEY"))
    buildConfigField("REVENUECAT_PLAY_SDK_KEY_TEST", localProperty("REVENUECAT_PLAY_SDK_KEY_TEST"))
}

ktlint {
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    baseline.set(file("../ktlint-baseline.xml"))
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}
