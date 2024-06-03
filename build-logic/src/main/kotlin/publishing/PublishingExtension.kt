package publishing

abstract class PublishingExtension {

    var pom: Pom? = null

    fun configure(
        pom: Pom,
    ) {
        this.pom = pom
    }

    data class Pom(
        val name: String,
        val description: String,
    )
}
