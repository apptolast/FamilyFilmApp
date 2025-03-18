import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.kotlin.serialize)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.ktlint.jlleitschuh)
    alias(libs.plugins.room)
}

// Read properties from local.properties
val localProperties = Properties()
val localFile = rootProject.file("local.properties")

if (localFile.exists()) {
    localProperties.load(FileInputStream(localFile))
}

val webIdClient: String = localProperties.getProperty("WEB_ID_CLIENT")
val tmdbApiKey: String = localProperties.getProperty("TMDB_ACCESS_TOKEN")
val admobAppId: String = localProperties.getProperty("ADMOB_APPLICATION_ID")
val admobBottomBanner: String = localProperties.getProperty("ADMOB_BOTTOM_BANNER_ID")

android {
    namespace = "com.apptolast.familyfilmapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.apptolast.familyfilmapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 15
        versionName = "0.3.13"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "WEB_ID_CLIENT", "\"$webIdClient\"")
        buildConfigField("String", "TMDB_ACCESS_TOKEN", "\"$tmdbApiKey\"")
        buildConfigField("String", "ADMOB_APPLICATION_ID", "\"$admobAppId\"")
        buildConfigField("String", "ADMOB_BOTTOM_BANNER_ID", "\"$admobBottomBanner\"")

        resValue("string", "admob_app_id", admobAppId)
    }

//    signingConfigs {
//        create("release") {
//            storeFile = file(localProperties.getProperty("storeFile"))
//            storePassword = localProperties.getProperty("storePassword")
//            keyAlias = localProperties.getProperty("keyAlias")
//            keyPassword = localProperties.getProperty("keyPassword")
//        }
//    }

    buildTypes {
        getByName("debug") { }

        getByName("release") {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

//            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {

    // Androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.compose.ui.text.google.fonts)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewModel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.paging.compose)

    // Splash
    implementation(libs.core.splashscreen)

    // Swipe
    implementation(libs.swipe)

    // Navigation
    implementation(libs.hilt.navigation.compose)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.compose.animation)

    // Google credentials
    implementation(libs.bundles.google.credentials)

    // Retrofit
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.work.manager)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    kspTest(libs.hilt.android.compiler)

    // Navigation Con Safe Arguments
    implementation(libs.gson)
    implementation(libs.navigation.compose)
    implementation(libs.compose.annotation)
    ksp(libs.compose.annotation.processor)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.firestore.ktx)

    // Timber
    implementation(libs.timber)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // Ktlint RuleSet
    ktlintRuleset(libs.ktlint.ruleset)

    // Room
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Admob
    implementation(libs.play.services.ads)

    // Turbine
    testImplementation(libs.turbine)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.test.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.test.rules)

    testImplementation(libs.mockk)

    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)

    // Tests
    testImplementation(libs.junit)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
}

ktlint {
    version = "1.4.1"
    debug = true
    verbose = true
    android = false
    outputToConsole = true
    outputColorName = "RED"
    ignoreFailures = false
    enableExperimentalRules = true
    baseline.set(file("ktlint-baseline.xml"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

androidComponents.onVariants { variant ->
    kotlin.sourceSets.findByName(variant.name)?.kotlin?.srcDirs(
        file("$buildFile/generated/ksp/${variant.name}/kotlin"),
    )
}

ksp {
    arg("ignoreGenericArgs", "false")
}

kotlin {
    sourceSets.configureEach {
        languageSettings.enableLanguageFeature("ExplicitBackingFields")
    }
}
