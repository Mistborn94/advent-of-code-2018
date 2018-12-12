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
        println()

        //This is not correct
        val calcualteTest = calculatePlants(lines, 160)
        println("Test: $calcualteTest")
        println()

        //This is not correct
        val calculatePlantsB = calculatePlants(lines, 50_000_000_000)
        assertEquals(4_049_999_998_575, calculatePlantsB)


        println("B: $calculatePlantsB")
    }
}