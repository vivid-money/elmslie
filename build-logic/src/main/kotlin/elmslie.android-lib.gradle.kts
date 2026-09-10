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
    binariesSource.set(BinariesSource.MAIN_COMPILATION)
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

val abiDumpCompileTaskNames = setOf("compileReleaseKotlin", "compileReleaseJavaWithJavac")

val abiDumpClassfiles =
  files(
    layout.buildDirectory.dir(
      "intermediates/built_in_kotlinc/release/compileReleaseKotlin/classes"
    ),
    layout.buildDirectory.dir("intermediates/javac/release/compileReleaseJavaWithJavac/classes"),
  )

tasks.matching { it.name == "internalDumpKotlinAbi" }.configureEach {
  dependsOn(tasks.matching { it.name in abiDumpCompileTaskNames })
  addAbiDumpClassfiles(abiDumpClassfiles)
}

fun Task.addAbiDumpClassfiles(classfiles: FileCollection) {
  val jvmTargets = javaClass.getMethod("getJvm").invoke(this)
  val jvmTargetsClass = jvmTargets.javaClass
  val current = jvmTargetsClass.getMethod("get").invoke(jvmTargets) as Iterable<*>

  val targetInfoClass =
    javaClass.classLoader.loadClass(
      "org.jetbrains.kotlin.gradle.tasks.abi.KotlinAbiDumpTaskImpl\$JvmTargetInfo"
    )
  val subdirectoryNameOf = targetInfoClass.getMethod("getSubdirectoryName")

  val targets = current.filterNotNull().toMutableList()
  if (targets.any { subdirectoryNameOf.invoke(it) == "" }) return

  val targetInfo =
    targetInfoClass
      .getConstructor(String::class.java, FileCollection::class.java)
      .newInstance("", classfiles)

  targets.add(targetInfo)
  jvmTargetsClass.getMethod("set", Iterable::class.java).invoke(jvmTargets, targets)
}
