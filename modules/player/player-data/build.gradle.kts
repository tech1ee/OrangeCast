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
    api(project(":base-data"))
    api(AppDependencies.networkLibs)
    api(AppDependencies.exoPlayerLibs)

    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltAndroidCompiler)
    kapt(AppDependencies.moshiKotlinCodegen)
}
android {
    namespace = "com.orangecast.player.data"
}