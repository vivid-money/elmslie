@file:Suppress("UnstableApiUsage")

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
    id("com.android.lint")
    id("elmslie.base-lib")
    id("elmslie.detekt")
    id("elmslie.tests-convention")
    `maven-publish`
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.named<Jar>("javadocJar") { from(tasks.named("dokkaJavadoc")) }

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies { catalog.findLibrary("dokka-kotlinAsJavaPlugin").ifPresent { dokkaHtmlPlugin(it) } }

lint {
    checkDependencies = true
    ignoreTestSources = true

    abortOnError = true
    warningsAsErrors = true

    htmlReport = true
    xmlReport = false
}

val libraryGroup: String by project
val libraryVersion: String by project

afterEvaluate {
    publishing.publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifactId = project.name
        groupId = libraryGroup
        version = libraryVersion
    }
}
