plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.musicmuni.voxavis.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.musicmuni.voxavis.sample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // VoxaVis library from Maven Central (or Maven Local during development —
    // run `./gradlew :library:publishToMavenLocal` in voxavis-source first).
    // Transitive Compose/coroutines/lifecycle deps resolve via Gradle Module Metadata.
    implementation("com.musicmuni:voxavis:1.0.0")

    // Sample-only: icon pack used by the demo UI (not a VoxaVis dependency)
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    debugImplementation("org.jetbrains.compose.ui:ui-tooling:1.10.1")
}
