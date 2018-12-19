package day16

import day16.TimeTravelOpCode.*
import helper.readInput
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Day16KtTest {

    @Test
    fun test_findMatchingOpCodes() {
        val before = listOf(3,2,1,1)
        val instruction = listOf(9, 2, 1, 2)
        val after = listOf(3, 2, 2, 1)

        val instructionSample = InstructionSample.fromInput(before, after, instruction)

        assertEquals(setOf(MULR, ADDI, SETI), instructionSample.possibleOperations)
    }

    @Test
    fun actualInput() {
        val day16 = Day16(readInput(16).readLines())

        val partA = day16.solvePartA()

        println("A: $partA")
        assertEquals(509, partA)

        val partB = day16.solvePartB()
        println("B: $partB")
        assertEquals(496, partB)
    }
}