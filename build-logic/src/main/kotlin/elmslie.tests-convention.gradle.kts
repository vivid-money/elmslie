tasks.withType<Test> { useJUnitPlatform() }

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    val testImplementation by configurations
    catalog.findLibrary("junit").ifPresent { testImplementation(it) }
}
