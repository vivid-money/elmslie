@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt

plugins { id("io.gitlab.arturbosch.detekt") }

detekt {
  parallel = true
  config.setFrom("$rootDir/detekt/detekt.yml")
}

tasks.withType<Detekt> {
  reports {
    html.required.set(true)
    xml.required.set(false)
    txt.required.set(false)
  }
}
