package publishing

import com.vanniktech.maven.publish.SonatypeHost
import gradle.kotlin.dsl.accessors._089c671483fb7c20aeca81bf72df85c9.mavenPublishing
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate

private const val ElmslieGitHubUrl = "https://github.com/vivid-money/elmslie"

abstract class PublishingExtension(
    private val project: Project,
) {

    fun configure(
        pom: Pom,
    ) {
        val libraryGroup: String by project
        val libraryVersion: String by project

        with(project.mavenPublishing) {
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            signAllPublications()

            coordinates(libraryGroup, project.name, libraryVersion)

            pom {
                name.set(pom.name)
                description.set(pom.description)
                url.set(ElmslieGitHubUrl)

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("$ElmslieGitHubUrl/issues")
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
                    url.set(ElmslieGitHubUrl)
                }
            }
        }
    }

    data class Pom(
        val name: String,
        val description: String,
    )
}
