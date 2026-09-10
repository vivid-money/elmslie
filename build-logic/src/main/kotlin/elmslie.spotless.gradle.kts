plugins { id("com.diffplug.spotless") }

val ktfmtVersion =
  extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion("ktfmt")
    .get()
    .requiredVersion

spotless {
  kotlin {
    ktfmt(ktfmtVersion).googleStyle()
    target("src/**/*.kt")
  }
  kotlinGradle { ktfmt(ktfmtVersion).googleStyle() }
}
