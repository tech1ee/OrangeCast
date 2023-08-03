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
