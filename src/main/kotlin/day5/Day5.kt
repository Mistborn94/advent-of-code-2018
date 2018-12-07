package day5

import helper.readInput
import java.util.*
import kotlin.math.abs

fun main(args: Array<String>) {

    val originalPolymer = readInput(5).readText().trim()

    val polymer = collapsePolymer(originalPolymer)

    println("A: ${polymer.size}")

    val minimumLength = ('A'..'Z').asSequence()
            .map { originalPolymer.replace(it.toString(), "", ignoreCase = true) }
            .map { collapsePolymer(it) }
            .map { it.size }
            .min()

    println("B: $minimumLength")
}

private fun collapsePolymer(originalPolymer: String): CharArray {
    var polymer = originalPolymer.toCharArray()

    do {
        var changed = false

        val newPolymer = Stack<Char>()
        for (current in polymer) {
            if (newPolymer.isEmpty()) {
                newPolymer.push(current)
            } else {
                val previous = newPolymer.peek()
                if (abs(current - previous) == 32) {
                    newPolymer.pop()
                    changed = true
                } else {
                    newPolymer.push(current)
                }
            }
        }
        polymer = newPolymer.toCharArray()
    } while (changed)

    return polymer
}