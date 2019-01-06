package day23

import helper.readInput
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Day23Test {

    @Test
    fun sample1() {
        val input = listOf(
            "pos=<0,0,0>, r=4",
            "pos=<1,0,0>, r=1",
            "pos=<4,0,0>, r=3",
            "pos=<0,2,0>, r=1",
            "pos=<0,5,0>, r=3",
            "pos=<0,0,3>, r=1",
            "pos=<1,1,1>, r=1",
            "pos=<1,1,2>, r=1",
            "pos=<1,3,1>, r=1"
        )

        val day23 = Day23(input)

        assertEquals(7, day23.solvePartA())
    }

    @Test
    fun sample2() {
        val input = listOf(
            "pos=<10,12,12>, r=2",
            "pos=<12,14,12>, r=2",
            "pos=<16,12,12>, r=4",
            "pos=<14,14,14>, r=6",
            "pos=<50,50,50>, r=200",
            "pos=<10,10,10>, r=5"
        )

        val day23 = Day23(input)

        assertEquals(36, day23.solvePartB())
    }

    @Test
    fun actual() {
        val input = readInput(23).readLines()

        val day23 = Day23(input)

        val partA = day23.solvePartA()
        println("A: $partA")
        val partB = day23.solvePartB()
        println("B: $partB")
//        assertEquals(B, partB)
    }
}