package day7

import helper.readInput
import helper.readSampleInput
import kotlin.test.Test
import kotlin.test.assertEquals

class Day7KtTest {

    @Test
    fun testSampleInput1() {

        val instructions = readSampleInput(7, 1).parseInstructions()
        val graph = Graph(instructions, 2, 0)

        val instructionOrder = graph.getInstructionOrder()
        assertEquals("CABDFE", instructionOrder)

        val executionTime = graph.getExecutionTime()
        assertEquals(15, executionTime)
    }

    @Test
    fun testActualInput1() {

        val instructions = readInput(7).parseInstructions()
        val graph = Graph(instructions, 5, 60)

        val instructionOrder = graph.getInstructionOrder()
        assertEquals("PFKQWJSVUXEMNIHGTYDOZACRLB", instructionOrder)
        println("A: $instructionOrder")

        val executionTime = graph.getExecutionTime()
        println("B: $executionTime")
        assertEquals(864, executionTime)
    }
}