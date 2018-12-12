package day12

import helper.readInput
import helper.readSampleInput
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class Day12KtTest {

    @Test
    fun sampleInput() {
        val lines = readSampleInput(12, 1).readLines()

        val calculatePlants = calculatePlants(lines, 20)

        assertEquals(325, calculatePlants)

        val calculatePlantsB = calculatePlants(lines, 50_000_000_000)
        assertNotEquals(5467, calculatePlantsB)
        println("B: $calculatePlantsB")
    }

    @Test
    fun actualInput() {
        val lines = readInput(12).readLines()

        val calculatePlantsA = calculatePlants(lines, 20)

        assertEquals(3061, calculatePlantsA)
        println("A: $calculatePlantsA")

        //This is not correct
        val calcualteTest = calculatePlants(lines, 159)

        println("Test: $calcualteTest")

        //This is not correct
        val calculatePlantsB = calculatePlants(lines, 50_000_000_000)
        assertNotEquals(5467L, calculatePlantsB)
        assertGreaterThan(6432L, calculatePlantsB)
        assertGreaterThan(16881L, calculatePlantsB)
        assertNotEquals(8099999996145, calculatePlantsB)

        println("B: $calculatePlantsB")
    }

    private fun <T : Comparable<T>> assertGreaterThan(expected: T, actual: T) {
        assertTrue(actual > expected, "Expected $actual to be > $expected")
    }
}