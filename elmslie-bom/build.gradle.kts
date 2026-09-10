plugins {
  `java-platform`
  id("elmslie.publishing")
}

elmsliePublishing {
  pom {
    name = "Elmslie BOM"
    description =
      "Bill of materials for Elmslie, a minimalistic reactive implementation of TEA/ELM"
  }
}

dependencies {
  constraints {
    api(projects.elmslieAndroid)
    api(projects.elmslieCore)
  }
}
