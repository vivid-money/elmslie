tasks.withType<Test> { useJUnitPlatform() }

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies { catalog.findLibrary("kotlin-test").ifPresent { "testImplementation"(it) } }
