package day1

import helper.readInput

fun main() {

    val frequency = readInput(1).useLines {
        it.map { line -> line.toInt() }.sum()
    }

    println(frequency)
}

