package day2

import helper.readInput
import helper.readSampleInput

fun main() {

    val checksum = readInput(2).useLines { sequence ->
        sequence.map { line ->
            //Count the number of occurrences of each character, keep only 2 and 3
            line.toCharArray()
                .groupBy { char -> char }
                .map { it.value.size }
                .toSet()
                .filter { it == 2 || it == 3 }
        } //Map to a pair containing a 1 or a 0 for 2 and 3
            .map { Pair(it.hasCount(2), it.hasCount(3)) }
            //Sum the pairs and et the checksum
            .reduce { acc, pair -> acc + pair }
            .run {
                first * second
            }
    }

    println(checksum)
}

private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(first + other.first, second + other.second)
}

private fun List<Int>.hasCount(i: Int) = if (contains(i)) 1 else 0