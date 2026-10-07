package com.vai.pokemonexplorer.ui.theme

import androidx.compose.ui.graphics.Color

fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "normal" -> Color(0xFFAAAA99)
        "fire" -> Color(0xFFFF4422)
        "water" -> Color(0xFF3399FF)
        "electric" -> Color(0xFFFFCC33)
        "grass" -> Color(0xFF77CC55)
        "ice" -> Color(0xFF66CCFF)
        "fighting" -> Color(0xFFBB5544)
        "poison" -> Color(0xFFAA5599)
        "ground" -> Color(0xFFDDBB55)
        "flying" -> Color(0xFF8899FF)
        "psychic" -> Color(0xFFFF5599)
        "bug" -> Color(0xFFAABB22)
        "rock" -> Color(0xFFBBAA66)
        "ghost" -> Color(0xFF6666BB)
        "dragon" -> Color(0xFF7766EE)
        "dark" -> Color(0xFF775544)
        "steel" -> Color(0xFFAAAABB)
        "fairy" -> Color(0xFFEE99EE)
        else -> Color.Gray
    }
}
