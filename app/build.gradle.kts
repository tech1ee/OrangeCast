import AppDependencies.implementation

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.firebase.crashlytics")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    defaultConfig {
        applicationId = AppConfig.appId
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
    }

    lint {
        lintConfig = file("$rootDir/buildSrc/src/main/xml/LintConfig.xml")
    }

    buildFeatures.apply {
        compose = true
    }

    composeOptions.apply {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            manifestPlaceholders["appName"] = "OrangeCast"
        }
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            manifestPlaceholders["appName"] = "OrangeCastDebug"
        }
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "com.orangecast.app"
}

dependencies {
    implementation(project(":base-ui"))

    implementation(AppDependencies.lifecycleProcess)

    implementation(AppDependencies.hiltAndroid)
    implementation(AppDependencies.workerLibs)
    kapt(AppDependencies.hiltAndroidCompiler)
    kapt(AppDependencies.workerHiltCompiler)

    implementation(platform(AppDependencies.firebase))
    implementation(AppDependencies.firebaseAnalytics)
    implementation(AppDependencies.firebaseCrashlytics)
    implementation(AppDependencies.firebaseConfig)

    androidTestImplementation(platform(AppDependencies.composeBom))
}

kapt {
    correctErrorTypes = true
}