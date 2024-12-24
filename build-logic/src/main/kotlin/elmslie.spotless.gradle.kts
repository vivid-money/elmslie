plugins { id("com.diffplug.spotless") }

spotless {
  val ktfmtVersion = "0.53"
  kotlin {
    ktfmt(ktfmtVersion).googleStyle()
    target("src/**/*.kt")
  }
  kotlinGradle { ktfmt(ktfmtVersion).googleStyle() }
}
