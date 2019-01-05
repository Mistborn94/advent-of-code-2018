package day22

import helper.Point
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test


internal class CaveSystemTest {

    @Test
    fun sample1() {
        val caveSystem = CaveSystem(Point(10, 10), 510)

        assertEquals(RegionType.ROCKY, caveSystem.regionType(Point(0, 0)))
        assertEquals(RegionType.WET, caveSystem.regionType(Point(1, 0)))
        assertEquals(RegionType.ROCKY, caveSystem.regionType(Point(0, 1)))

        assertEquals(1805, caveSystem.erosionLevel(Point(1, 1)))
        assertEquals(145722555, caveSystem.geologicalIndex(Point(1, 1)))
        assertEquals(2, caveSystem.riskLevel(Point(1, 1)))
        assertEquals(RegionType.NARROW, caveSystem.regionType(Point(1, 1)))

        assertEquals(RegionType.ROCKY, caveSystem.regionType(Point(10, 10)))

        assertEquals(114, caveSystem.totalRiskLevel())
        assertEquals(45, caveSystem.rescueTime())
    }

    @Test
    fun actual() {
        val caveSystem = CaveSystem(Point(14, 785), 4080)

        val totalRiskLevel = caveSystem.totalRiskLevel()
        println("A: $totalRiskLevel")

        assertEquals(11843, totalRiskLevel)

        val totalTime = caveSystem.rescueTime()
        println("B: $totalTime")

        assertEquals(1078, totalTime)
    }
}