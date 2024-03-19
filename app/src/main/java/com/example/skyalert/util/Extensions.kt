package com.example.skyalert.util

import java.util.Locale

fun String.toCapitalizedWords(): String {
    val words = this.split(" ").toMutableList()
    for (i in words.indices)
        words[i] =
            words[i].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    return words.joinToString(" ")


}
