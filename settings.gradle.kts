pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "OrangeCast"

include(
        ":app",
)

fun includeCommonModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/$name")
}
includeCommonModule("common")
includeCommonModule("main")
includeCommonModule("navigation")


fun includeBaseModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/base/$name")
}
includeBaseModule("base-ui")
includeBaseModule("base-data")
includeBaseModule("base-domain")


fun includeDiscoverModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/discover/$name")
}
includeDiscoverModule("discover-ui")

fun includePodcastsModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/podcasts/$name")
}
includePodcastsModule("podcasts-data")
includePodcastsModule("podcasts-domain")
includePodcastsModule("podcasts-ui")

fun includeGenresModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/genres/$name")
}
includeGenresModule("genres-data")
includeGenresModule("genres-domain")
includeGenresModule("genres-ui")

fun includeSplashModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/splash/$name")
}
includeSplashModule("splash-ui")

fun includeDetailsModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/details/$name")
}
includeDetailsModule("details-data")
includeDetailsModule("details-domain")
includeDetailsModule("details-ui")

fun includePlayerModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/player/$name")
}
includePlayerModule("player-data")
includePlayerModule("player-domain")
includePlayerModule("player-ui")

fun includeLibraryModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/library/$name")
}
includeLibraryModule("library-data")
includeLibraryModule("library-domain")
includeLibraryModule("library-ui")

fun includeSearchModule(name: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, "modules/search/$name")
}
includeSearchModule("search-ui")
