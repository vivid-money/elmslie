@file:Suppress("UnstableApiUsage")

plugins { `kotlin-dsl` }

dependencies {
  implementation(libs.android.gradlePlugin)
  implementation(libs.detekt.gradlePlugin)
  implementation(libs.dokka.gradlePlugin)
  implementation(libs.kotlin.gradlePlugin)
  implementation(libs.mavenPublishPlugin)
  implementation(libs.spotless.gradlePlugin)
}
