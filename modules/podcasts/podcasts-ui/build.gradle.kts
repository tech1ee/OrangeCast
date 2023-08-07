plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.orangecast.podcasts.ui"
    buildFeatures.apply {
        compose = true
    }

    composeOptions.apply {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}


dependencies {
    implementation(project(":base-ui"))
    implementation(project(":genres-ui"))
    implementation(project(":genres-domain"))
    implementation(project(":podcasts-domain"))

    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltAndroidCompiler)
    kaptAndroidTest(AppDependencies.hiltAndroidCompiler)
}