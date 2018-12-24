package day14

import kotlin.math.max
import kotlin.math.min

const val LOG = false

class ElfRecipes {

    val recipes = mutableListOf(3, 7)
    var elfA = 0
    var elfB = 1

    fun tick() {
        val sum = recipes[elfA] + recipes[elfB]
        if (sum >= 10) {
            recipes.add(sum / 10)
        }
        recipes.add(sum % 10)

        elfA = (elfA + recipes[elfA] + 1) % recipes.size
        elfB = (elfB + recipes[elfB] + 1) % recipes.size
    }

    override fun toString(): String {
        return recipes.mapIndexed { i, value ->
            when (i) {
                elfA -> "($value)"
                elfB -> "[$value]"
                else -> " $value "
            }
        }.joinToString(separator = "")
    }

    fun endsWith(pattern: List<Int>, offset: Int = 0): Boolean =
        (pattern == sizedSublist(recipes.size - pattern.size - offset, pattern.size))


    private fun sizedSublist(start: Int, size: Int) = recipes.subList(max(0, start), min(start + size, recipes.size))
}

fun scoresOfNextTen(limit: Int): String {
    val lab = ElfRecipes()

    while (lab.recipes.size <= limit + 10) {
        printRecipes(lab)
        lab.tick()
    }

    printRecipes(lab)

    return lab.recipes.subList(limit, limit + 10).joinToString("")
}

fun recipesBefore(pattern: String): Int {
    val patternList = pattern.map { it.toString().toInt() }
    val lab = ElfRecipes()

    while (!lab.endsWith(patternList) && !lab.endsWith(patternList, 1)) {
        printRecipes(lab)
        lab.tick()
    }
    printRecipes(lab)

    return if (lab.endsWith(patternList)) {
        lab.recipes.size - pattern.length
    } else {
        lab.recipes.size - pattern.length - 1
    }
}

private fun printRecipes(recipes: ElfRecipes) {
    if (LOG) {
        println(recipes)
    }
}

