package day11

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PowerGridTest {

    @Test
    fun testPowerLevel_s1() {
        val build = FuelCell.build(3, 5, 8)
        assertEquals(4, build.powerLevel)
    }

    @Test
    fun testPowerLevel_s2() {
        val build = FuelCell.build(122, 79, 57)
        assertEquals(-5, build.powerLevel)
    }

    @Test
    fun testPowerLevel_s3() {
        val build = FuelCell.build(217, 196, 39)
        assertEquals(0, build.powerLevel)
    }

    @Test
    fun testPowerLevel_s4() {
        val build = FuelCell.build(101, 153, 71)
        assertEquals(4, build.powerLevel)
    }

    @Test
    fun testCoordinate_s1() {
        val grid = PowerGrid(18)
        val bestSubsquare = grid.findBestSubsquare()
        assertEquals(33, bestSubsquare.x)
        assertEquals(45, bestSubsquare.y)

        val bestOverallSquare = grid.findBestSubsquareOfAnySize()
        assertEquals(90, bestOverallSquare.x)
        assertEquals(269, bestOverallSquare.y)
        assertEquals(16, bestOverallSquare.size)
    }

    @Test
    fun testCoordinate_s2() {
        val grid = PowerGrid(42)
        val bestSubsquare = grid.findBestSubsquare()
        assertEquals(21, bestSubsquare.x)
        assertEquals( 61, bestSubsquare.y)

        val bestOverallSquare = grid.findBestSubsquareOfAnySize()
        assertEquals(232, bestOverallSquare.x)
        assertEquals(251, bestOverallSquare.y)
        assertEquals(12, bestOverallSquare.size)
    }

    @Test
    fun testCoordinate_actual() {
        val grid = PowerGrid(3031)
        val bestSubsquare = grid.findBestSubsquare()
        assertEquals(21, bestSubsquare.x)
        assertEquals(76, bestSubsquare.y)

        val bestOverallSquare = grid.findBestSubsquareOfAnySize()
        println("${bestOverallSquare.x},${bestOverallSquare.y},${bestOverallSquare.size}")
        assertEquals(234, bestOverallSquare.x)
        assertEquals(108, bestOverallSquare.y)
        assertEquals(16, bestOverallSquare.size)
    }
}