package day20

import helper.readInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BuildingMapTest {

    @Test
    fun sample1() {
        val map = BuildingMap("^WNE\$")
        assertEquals(3, map.bfs().first)
    }

    @Test
    fun sample2() {
        val map = BuildingMap("^ENWWW(NEEE|SSE(EE|N))\$")
        assertEquals(10, map.bfs().first)
    }

    @Test
    fun sample3() {
        val map = BuildingMap("^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN\$")
        assertEquals(18, map.bfs().first)
    }

    @Test
    fun sample4() {
        val map = BuildingMap("^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))\$")
        assertEquals(23, map.bfs().first)
    }

    @Test
    fun sample5() {
        val map = BuildingMap("^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$")
        assertEquals(31, map.bfs().first)
    }

    @Test
    fun actual() {
        val input = readInput(20).readText()
        val map = BuildingMap(input)
        val (a, b) = map.bfs()
        println("A: $a")
        assertEquals(4406, a)

        println("B: $b")
        assertEquals(8468, b)
    }
}