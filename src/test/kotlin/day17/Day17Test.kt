package day17

import helper.readInput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class Day17Test {

    @Test
    fun testSampleInput() {
        val input = listOf(
            "x=495, y=2..7",
            "y=7, x=495..501",
            "x=501, y=3..7",
            "x=498, y=2..4",
            "x=506, y=1..2",
            "x=498, y=10..13",
            "x=504, y=10..13",
            "y=13, x=498..504"
        )

        val day17 = Day17(input)
        val partA = day17.solvePartA()

        assertEquals(57, partA)

        val partB = day17.solvePartB()
        assertEquals(29, partB)

    }

    @Test
    fun testActualInput() {
        val input = readInput(17).readLines()

        val day17 = Day17(input)

        day17.solvePartA()
        val partA = day17.solvePartA()

        println("A: $partA")
        assertEquals(31383, partA)

        val partB = day17.solvePartB()
        println("B: $partB")
        assertEquals(25376, partB)
    }
}