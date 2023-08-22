plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.orangecast.library.ui"
    buildFeatures.apply {
        compose = true
    }

    composeOptions.apply {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}


dependencies {
    implementation(project(":base-ui"))
    implementation(project(":podcasts-ui"))
    implementation(project(":podcasts-domain"))
    implementation(project(":library-domain"))

    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltAndroidCompiler)
    kaptAndroidTest(AppDependencies.hiltAndroidCompiler)
}