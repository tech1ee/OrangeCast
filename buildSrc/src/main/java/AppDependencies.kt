import org.gradle.api.artifacts.dsl.DependencyHandler

object AppDependencies {

    // App Dependencies
    private const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleKtx}"
    const val lifecycleProcess = "androidx.lifecycle:lifecycle-process:${Versions.lifecycleProcess}"
    private const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    private const val coroutinesPlayServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutines}"
    private const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    const val firebase = "com.google.firebase:firebase-bom:${Versions.firebase}"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebaseCrashlyticsGradle = "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebaseCrashlyticsGradle}"
    const val firebaseConfig = "com.google.firebase:firebase-config-ktx"
    const val appUpdate = "com.google.android.play:app-update-ktx:${Versions.appUpdate}"


    // Jetpack Compose
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    private const val composeCompiler = "androidx.compose.compiler:compiler:${Versions.composeCompiler}"
    private const val composeUi = "androidx.compose.ui:ui"
    private const val composeRuntimeLivedata = "androidx.compose.runtime:runtime-livedata"
    private const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    private const val composeMaterial = "androidx.compose.material:material"
    private const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivity}"
    private const val composeNavigation = "androidx.navigation:navigation-compose:${Versions.composeNavigation}"
    private const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationCompose}"
    private const val composeLottie = "com.airbnb.android:lottie-compose:${Versions.composeLottie}"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    private const val composeConstraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
    private const val composeImmutableCollections = "org.jetbrains.kotlinx:kotlinx-collections-immutable:${Versions.composeImmutableCollections}"

    val composeLibs = listOf(
        composeCompiler,
        composeUi,
        composeRuntimeLivedata,
        composeUiTooling,
        composeMaterial,
        composeActivity,
        composeNavigation,
        hiltNavigationCompose,
        composeLottie,
        composeConstraintLayout,
        composeImmutableCollections,
    )

    // Splash screen
    const val splash = "androidx.core:core-splashscreen:${Versions.splash}"

    // Material
    const val material = "com.google.android.material:material:${Versions.material}"

    // Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"

    // Accompanist
    private const val accompanistSystemUiController = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
    private const val accompanistSwiperefresh = "com.google.accompanist:accompanist-swiperefresh:${Versions.accompanist}"
    private const val accompanistPager = "com.google.accompanist:accompanist-pager:${Versions.accompanist}"
    private const val accompanistPlaceholder = "com.google.accompanist:accompanist-placeholder-material:${Versions.accompanist}"

    val accompanistLibs = listOf(
        accompanistSystemUiController,
        accompanistSwiperefresh,
        accompanistPager,
        accompanistPlaceholder,
    )

    // Exo Player
    const val exoPlayer = "androidx.media3:media3-exoplayer:${Versions.exoPlayer}"
    const val exoPlayerDash = "androidx.media3:media3-exoplayer-dash:${Versions.exoPlayer}"
    const val exoPlayerUI = "androidx.media3:media3-ui:${Versions.exoPlayer}"

    val exoPlayerLibs = listOf(
        exoPlayer,
        exoPlayerDash,
        exoPlayerUI,
    )

    // Room
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    val roomLibs = listOf(
        roomRuntime,
        roomKtx,
    )

    // Coil
    const val coil = "io.coil-kt:coil-compose:${Versions.coil}"

    // RSS Parser
    const val rssParser = "com.prof18.rssparser:rssparser:${Versions.rssParser}"

    // Network
    private const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    private const val adapter = "com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofit}"
    private const val converter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    private const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    private const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"
    private const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    private const val moshiAdapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
    const val moshiKotlinCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    const val securityCrypto = "androidx.security:security-crypto-ktx:${Versions.securityCrypto}"
    val networkLibs = listOf(
        retrofit,
        adapter,
        converter,
        okhttp,
        loggingInterceptor,
        moshiKotlin,
        moshiAdapters,
    )

    val coreLibs = listOf(
        coreKtx,
        appCompat,
        coroutines,
        coroutinesPlayServices,
        timber,
        moshiKotlin,
    )

    private const val workerRuntime = "androidx.work:work-runtime-ktx:${Versions.workRuntime}"
    private const val workerHilt = "androidx.hilt:hilt-work:${Versions.workHilt}"
    const val workerHiltCompiler = "androidx.hilt:hilt-compiler:${Versions.workHilt}"

    val workerLibs = listOf(
        workerRuntime,
        workerHilt,
    )

    fun DependencyHandler.kapt(list: List<String>) {
        list.forEach { dependency ->
            add("kapt", dependency)
        }
    }

    fun DependencyHandler.implementation(list: List<String>) {
        list.forEach { dependency ->
            add("implementation", dependency)
        }
    }

    fun DependencyHandler.androidTestImplementation(list: List<String>) {
        list.forEach { dependency ->
            add("androidTestImplementation", dependency)
        }
    }

    fun DependencyHandler.testImplementation(list: List<String>) {
        list.forEach { dependency ->
            add("testImplementation", dependency)
        }
    }

    fun DependencyHandler.api(list: List<String>) {
        list.forEach { dependency ->
            add("api", dependency)
        }
    }
}