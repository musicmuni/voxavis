pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // mavenLocal() first so local dev builds (from voxavis-source:
        // ./gradlew :library:publishToMavenLocal) are picked up before
        // falling back to the published Maven Central release.
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "VoxaVisSample"
include(":app")
