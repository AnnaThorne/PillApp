plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.8.10"
}

android {
    namespace = "com.thorne.sdk"
    compileSdk = 34 // Android 14

    defaultConfig {
        minSdk = 29  // Android 10
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
}