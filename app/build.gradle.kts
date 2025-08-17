import AppDependencies.implementation

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
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
    implementation(project(":shared"))

    // Android Core
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Compose BOM and core components
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.6.0")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    
    // Koin
    implementation("io.insert-koin:koin-android:3.4.2")

    implementation(AppDependencies.lifecycleProcess)

    implementation(platform(AppDependencies.firebase))
    implementation(AppDependencies.firebaseAnalytics)
    implementation(AppDependencies.firebaseCrashlytics)
    implementation(AppDependencies.firebaseConfig)

    // Testing
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}