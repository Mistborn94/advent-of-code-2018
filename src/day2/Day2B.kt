package day2

import helper.readInput

fun main() {

    val lines = readInput(2).readLines()

    println(findSimilar(lines))
}

private fun findSimilar(lines: List<String>): String {
    for ((index, first) in lines.subList(0, lines.lastIndex).withIndex()) {
        for (second in lines.subList(index + 1, lines.size)) {
            val matching = getMatching(first, second)
            if (matching.length == first.length - 1) {
                return matching
            }
        }
    }
    throw RuntimeException("No match found")
}

fun getMatching(a: String, b: String): String {
    return a.zip(b)
        .filter { it.first == it.second}
        .joinToString (separator = "") {  it.first.toString() }
}
