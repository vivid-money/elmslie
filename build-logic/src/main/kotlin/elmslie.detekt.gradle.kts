import dev.detekt.gradle.Detekt

plugins { id("dev.detekt") }

detekt {
  parallel = true
  buildUponDefaultConfig = true
  config.setFrom(rootProject.layout.projectDirectory.file("detekt/detekt.yml"))
}

tasks.withType<Detekt> { reports { html.required.set(true) } }

val detektAll =
  tasks.register("detektAll") {
    group = "verification"
    description = "Runs detekt over every source set of this module."
    dependsOn(tasks.withType<Detekt>())
  }

plugins.withId("base") { tasks.named("check") { dependsOn(detektAll) } }
