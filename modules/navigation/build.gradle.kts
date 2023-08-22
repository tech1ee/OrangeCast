plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.orangecast.navigation"
    buildFeatures.apply {
        compose = true
    }

    composeOptions.apply {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}


dependencies {
    implementation(project(":main"))
    implementation(project(":base-ui"))
    implementation(project(":discover-ui"))
    implementation(project(":podcasts-ui"))
    implementation(project(":details-ui"))
    implementation(project(":library-ui"))

    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltAndroidCompiler)

}