@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalAbiValidation::class)

import org.jetbrains.kotlin.gradle.dsl.abi.BinariesSource
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
  id("com.android.library")
  id("org.jetbrains.dokka")
  id("org.jetbrains.dokka-javadoc")
  id("elmslie.base-lib")
  id("elmslie.detekt")
  id("elmslie.spotless")
  id("elmslie.tests-convention")
}

kotlin {
  explicitApi()

  abiValidation {
    binariesSource.set(BinariesSource.MAVEN_PUBLICATIONS)
    referenceDumpDir.set(layout.projectDirectory.dir("api"))
  }
}

android {
  compileSdk = AndroidCompileSdk

  defaultConfig { minSdk = AndroidMinSdk }

  lint {
    checkReleaseBuilds = false
    checkDependencies = true

    ignoreTestSources = true
    abortOnError = true
    warningsAsErrors = true
  }

  compileOptions {
    targetCompatibility = JvmVersion
    sourceCompatibility = JvmVersion
  }
}
