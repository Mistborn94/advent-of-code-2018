package day1

import helper.readInput

fun main() {

    val frequencyChanges = readInput(1).readLines().map { line -> line.toLong() }
    val reachedFrequencies = mutableSetOf<Long>()

    var currentFrequency = 0L
    var unique = true
    var index = 0

    while (unique) {
        currentFrequency += frequencyChanges[index++]
        unique = reachedFrequencies.add(currentFrequency)

        if (index > frequencyChanges.lastIndex) {
            index = 0
        }
    }

    println(currentFrequency)
}

