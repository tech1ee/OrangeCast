import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(AppDependencies.firebaseCrashlyticsGradle)
        classpath(AppDependencies.hilt)
    }
}

plugins {
    id("com.android.application") version "8.0.2" apply false
    id("com.android.library") version "8.0.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

fun PluginContainer.applyBaseConfig(project: Project) {
    whenPluginAdded {
        when (this) {
            is AppPlugin -> {
                project.extensions
                    .getByType<AppExtension>()
                    .apply {
                        baseConfig()
                    }
            }

            is LibraryPlugin -> {
                project.extensions
                    .getByType<LibraryExtension>()
                    .apply {
                        baseConfig()
                    }
            }
        }
    }
}

fun BaseExtension.baseConfig() {
    compileSdkVersion(AppConfig.compileSdkVersion)
    buildToolsVersion(AppConfig.buildToolsVersion)
    buildTypes {
        getByName("release") {
            buildConfigField("String", "APP_ID", "\"${AppConfig.appId}${""}\"")
            buildConfigField("String", "BASE_URL", "\"TBD/\"")
           }
        getByName("debug") {
            buildConfigField("String", "APP_ID", "\"${AppConfig.appId}${".debug"}\"")
            buildConfigField("String", "BASE_URL", "\"TBD/\"")
        }
    }

    defaultConfig.apply {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "VERSION_NAME", "\"${AppConfig.versionName}\"")
        buildConfigField("int", "VERSION_CODE", "${AppConfig.versionCode}")
    }

    compileOptions.apply {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}

subprojects {
    project.plugins.applyBaseConfig(project)
    if (project.name.contains("app")) {
        with(project) {
            apply(plugin = "com.android.application")
        }
    }
    if (project.name.contains("ui")) {
        with(project) {
            apply(plugin = "com.android.library")
        }
    }
}