package day19

import helper.readInput
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Day19Test {

    @Test
    fun testSample1() {
        val input = listOf(
            "#ip 0",
            "seti 5 0 1",
            "seti 6 0 2",
            "addi 0 1 0",
            "addr 1 2 3",
            "setr 1 0 0",
            "seti 8 0 4",
            "seti 9 0 5"
            )

        val day19 = Day19(input)
        assertEquals(6, day19.solvePartA())
    }

    @Test
    fun testActual() {
        val input = readInput(19).readLines()

        val day19 = Day19(input)
        assertEquals(1374, day19.solvePartA())

        val partB = day19.solvePartB()
        println("B: $partB")
        assertEquals(15826974, partB)

    }

}