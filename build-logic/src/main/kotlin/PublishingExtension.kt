abstract class PublishingExtension {

    internal lateinit var pom: Pom

    fun pom(
        block: Pom.() -> Unit,
    ) {
        pom = Pom().apply(block)
    }

    data class Pom(var name: String = "", var description: String = "")
}
