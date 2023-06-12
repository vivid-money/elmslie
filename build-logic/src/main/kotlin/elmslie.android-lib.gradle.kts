@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka")
    id("elmslie.base-lib")
    id("elmslie.detekt")
    id("elmslie.tests-convention")
    `maven-publish`
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

    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }
    }

    compileOptions {
        targetCompatibility = JvmTarget
        sourceCompatibility = JvmTarget
    }
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies { catalog.findLibrary("dokka-kotlinAsJavaPlugin").ifPresent { dokkaHtmlPlugin(it) } }

val libraryGroup: String by project
val libraryVersion: String by project

afterEvaluate {
    publishing.publications.create<MavenPublication>("release") {
        from(components["release"])
        artifactId = project.name
        groupId = libraryGroup
        version = libraryVersion
    }
}
