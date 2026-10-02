import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// The VoxaVis release to build against, declared once as `voxavisVersion` in
// gradle.properties. Override it on the command line to build against a local
// Maven build: ./gradlew assembleDebug -PvoxavisVersion=<version>
val voxavisVersion: String = providers.gradleProperty("voxavisVersion").get()

// The licence key, from the gitignored local.properties beside settings.gradle.kts:
//   voxavis.apiKey=sk_...
// Missing is allowed: the app then builds, runs, and shows how to set it up on
// the screens that need a licence.
val voxavisApiKey: String = rootProject.file("local.properties")
    .takeIf { it.exists() }
    ?.let { file -> Properties().apply { file.inputStream().use { load(it) } } }
    ?.getProperty("voxavis.apiKey")
    ?.trim()
    .orEmpty()

android {
    namespace = "com.musicmuni.voxavis.sample"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.musicmuni.voxavis.sample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "VOXAVIS_API_KEY", "\"$voxavisApiKey\"")
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
        buildConfig = true
    }
}

dependencies {
    // VoxaVis from Maven Central, or from Maven Local when a local build has
    // been published there (settings.gradle.kts lists mavenLocal() first).
    implementation("com.musicmuni:voxavis:$voxavisVersion")

    // VoxaVis brings Compose (runtime, foundation, UI, Material 3) onto the
    // compile classpath. The demo adds what it uses beyond that.
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")

    // Sample-only: icon pack used by the demo UI (not a VoxaVis dependency)
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    debugImplementation("org.jetbrains.compose.ui:ui-tooling:1.10.1")
}
