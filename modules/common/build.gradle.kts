import AppDependencies.implementation

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

dependencies {
    implementation(AppDependencies.coreLibs)
    implementation(AppDependencies.coil)

    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltAndroidCompiler)
}
android {
    namespace = "com.orangecast.common"
}