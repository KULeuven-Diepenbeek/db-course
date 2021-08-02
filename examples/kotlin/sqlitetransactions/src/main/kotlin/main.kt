data class Magic(val name: String, val rarity: String = "common")

fun isItWorthIt(card: Magic): Double {
    return when(card) {
        Magic("The Scarab God", "rare") -> 15.0
        Magic("Binding Mummy") -> 0.1
        else -> 0.0
    }
}

fun main(args: Array<String>) {
    val theScarabGod = Magic("The Scarab God", "rare")
    print(isItWorthIt(theScarabGod))

}