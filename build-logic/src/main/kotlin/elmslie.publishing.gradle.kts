import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar

plugins { id("com.vanniktech.maven.publish") }

private val elmslieGitHubUrl = "https://github.com/vivid-money/elmslie"

val publishingExtension =
  project.extensions.create("elmsliePublishing", PublishingExtension::class.java)

val libraryGroup = providers.gradleProperty("libraryGroup")
val libraryVersion = providers.gradleProperty("libraryVersion")

val skipSigning =
  providers.gradleProperty("elmslie.skipSigning").map(String::toBoolean).getOrElse(false)

plugins.withId("org.jetbrains.kotlin.multiplatform") {
  mavenPublishing {
    configure(
      KotlinMultiplatform(
        javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
        sourcesJar = SourcesJar.Sources(),
      )
    )
  }
}

tasks.withType<AbstractArchiveTask>().configureEach {
  isPreserveFileTimestamps = false
  isReproducibleFileOrder = true
}

afterEvaluate {
  val pom = publishingExtension.pom
  with(project.mavenPublishing) {
    checkPomRequiredFields(pom)
    publishToMavenCentral()
    if (!skipSigning) signAllPublications()

    coordinates(libraryGroup.get(), project.name, libraryVersion.get())

    pom {
      name.set(pom.name)
      description.set(pom.description)
      url.set(elmslieGitHubUrl)

      licenses {
        license {
          name.set("The Apache License, Version 2.0")
          url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        }
      }

      issueManagement {
        system.set("GitHub Issues")
        url.set("$elmslieGitHubUrl/issues")
      }

      developers {
        developer {
          id.set("DeveloperMobile")
          name.set("Developer Mobile")
          email.set("developer.mobile@vivid.money")
        }
      }

      scm {
        connection.set("scm:git:git://github.com/vivid-money/elmslie.git")
        developerConnection.set("scm:git:ssh://github.com/vivid-money/elmslie.git")
        url.set(elmslieGitHubUrl)
      }
    }
  }
}

fun checkPomRequiredFields(pom: PublishingExtension.Pom) {
  if (pom.name.isBlank()) {
    throw IllegalArgumentException(
      """Pom.name cannot be empty
            | Please, call elmsliePublishing { pom { name = "Lib name" } }
            """
        .trimMargin()
    )
  }
}
