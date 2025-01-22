package com.dk.piley.model.pile

enum class PileColor(val hexCode: String) {
    NONE(""),
    RED("#EF476F"),
    YELLOW("#FFD166"),
    GREEN("#06D6A0"),
    BLUE("#118AB2"),
    DARK_BLUE("#073B4C");
}

fun String.hexToPileColor(): PileColor {
    return when (this) {
        PileColor.NONE.hexCode -> PileColor.NONE
        PileColor.RED.hexCode -> PileColor.RED
        PileColor.YELLOW.hexCode -> PileColor.YELLOW
        PileColor.GREEN.hexCode -> PileColor.GREEN
        PileColor.BLUE.hexCode -> PileColor.BLUE
        PileColor.DARK_BLUE.hexCode -> PileColor.DARK_BLUE
        else -> PileColor.NONE
    }
}