import AppDependencies.api

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.orangecast.base.ui"
    buildFeatures.apply {
        compose = true
    }

    composeOptions.apply {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    api(AppDependencies.coreLibs)
    api(project(":common"))
    implementation(project(":base-domain"))

    api(platform(AppDependencies.composeBom))
    api(AppDependencies.lifecycleKtx)
    api(AppDependencies.composeLibs)
    api(AppDependencies.accompanistLibs)
    api(AppDependencies.coil)

    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltAndroidCompiler)

    debugApi(AppDependencies.composeUiToolingPreview)

}