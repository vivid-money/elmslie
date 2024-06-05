import com.vanniktech.maven.publish.SonatypeHost
import gradle.kotlin.dsl.accessors._089c671483fb7c20aeca81bf72df85c9.mavenPublishing
import java.lang.IllegalArgumentException

plugins {
    id("com.vanniktech.maven.publish")
}

private val elmslieGitHubUrl = "https://github.com/vivid-money/elmslie"

val publishingExtension =
    project.extensions.create("elmsliePublishing", PublishingExtension::class.java)

val libraryGroup: String by project
val libraryVersion: String by project

afterEvaluate {
    val pom = publishingExtension.pom
    with(project.mavenPublishing) {
        checkPomRequiredFields(pom)
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()

        coordinates(libraryGroup, project.name, libraryVersion)

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
            """.trimMargin(),
        )
    }
}
