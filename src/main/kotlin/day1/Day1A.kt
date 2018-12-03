package day1

import helper.readInput

fun main(args: Array<String>) {

    val frequency = readInput(1).useLines {
        it.map { line -> line.toInt() }.sum()
    }

    println(frequency)
}

