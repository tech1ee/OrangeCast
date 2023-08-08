import AppDependencies.api

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

dependencies {
    api(AppDependencies.coreLibs)
    api(project(":common"))
    implementation(project(":base-domain"))
    implementation(project(":details-data"))
    implementation(project(":genres-domain"))

    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltAndroidCompiler)
}
android {
    namespace = "com.orangecast.details.domain"
}