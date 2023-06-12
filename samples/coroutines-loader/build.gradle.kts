@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "vivid.money.elmslie.samples.coroutines.timer"

    compileSdk = 33
    buildToolsVersion = "31.0.0"

    buildFeatures { buildConfig = true }

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation(projects.elmslieAndroid)
    implementation(projects.elmslieCore)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragmentKtx)
    implementation(libs.google.material)
    implementation(libs.kotlinx.coroutinesCore)
}
