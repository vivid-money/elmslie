tasks.withType<Test> { useJUnitPlatform() }

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
  val testImplementation by configurations
  catalog.findLibrary("kotlin-test").ifPresent { testImplementation(it) }
}
