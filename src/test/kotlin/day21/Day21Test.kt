package day21

import helper.readInput
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Day21Test {

    @Test
    internal fun testActual() {
        val input = readInput(21).readLines()
        val day21 = Day21(input)

        val partA = day21.partA(28)
        assertEquals(6483199, partA)
        println("A: $partA")

        val partB = day21.partB()
        assertEquals(13338900, partB)
        println("B: $partB")
    }
}