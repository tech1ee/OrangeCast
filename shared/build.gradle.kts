plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight") version "2.0.0"
}

kotlin {
    android()
    
    // iOS targets temporarily disabled during development
    // listOf(
    //     iosX64(),
    //     iosArm64(),
    //     iosSimulatorArm64()
    // ).forEach {
    //     it.binaries.framework {
    //         baseName = "shared"
    //     }
    // }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
                implementation("io.ktor:ktor-client-core:2.3.2")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
                implementation("io.ktor:ktor-client-cio:2.3.2")
                implementation("io.insert-koin:koin-core:3.4.2")
                implementation("io.insert-koin:koin-compose:1.0.4")
                
                // XML parsing for RSS feeds
                implementation("com.fleeksoft.ksoup:ksoup:0.1.2")
                
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.materialIconsExtended)
                
                // Navigation
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
                
                // SQLDelight
                implementation("app.cash.sqldelight:runtime:2.0.0")
                implementation("app.cash.sqldelight:coroutines-extensions:2.0.0")
            }
        }
        
        
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.3.2")
                implementation("io.ktor:ktor-client-logging:2.3.2")
                implementation("io.coil-kt:coil-compose:2.4.0")
                implementation("app.cash.sqldelight:android-driver:2.0.0")
                implementation("io.insert-koin:koin-android:3.4.2")
                
                // ExoPlayer dependencies
                implementation("androidx.media3:media3-exoplayer:1.1.0")
                implementation("androidx.media3:media3-ui:1.1.0")
                implementation("androidx.media3:media3-common:1.1.0")
            }
        }
        
        // iOS source sets temporarily disabled
        // val iosX64Main by getting
        // val iosArm64Main by getting
        // val iosSimulatorArm64Main by getting
        // val iosMain by creating {
        //     dependsOn(commonMain)
        //     iosX64Main.dependsOn(this)
        //     iosArm64Main.dependsOn(this)
        //     iosSimulatorArm64Main.dependsOn(this)
        //     
        //     dependencies {
        //         implementation("app.cash.sqldelight:native-driver:2.0.0")
        //     }
        // }
    }
}

android {
    namespace = "dev.orangecast.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight {
    databases {
        create("OrangeCastDatabase") {
            packageName.set("dev.orangecast.shared.database")
        }
    }
}