abstract class PublishingExtension {

  internal var pom: Pom = Pom()

  fun pom(block: Pom.() -> Unit) {
    pom = Pom().apply(block)
  }

  data class Pom(var name: String = "", var description: String = "")
}
