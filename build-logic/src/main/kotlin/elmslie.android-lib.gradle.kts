@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka")
    id("elmslie.base-lib")
    id("elmslie.detekt")
    id("elmslie.tests-convention")
}

android {
    compileSdk = 33
    buildToolsVersion = "31.0.0"

    defaultConfig { minSdk = 21 }

    lint {
        checkReleaseBuilds = false
        checkDependencies = true

        ignoreTestSources = true
        abortOnError = true
        warningsAsErrors = true

        htmlReport = true
        xmlReport = false
    }

    compileOptions {
        targetCompatibility = JvmTarget
        sourceCompatibility = JvmTarget
    }
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
