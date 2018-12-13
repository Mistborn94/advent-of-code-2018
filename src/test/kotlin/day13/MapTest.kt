package day13

import helper.readInput
import helper.readSampleInput
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MapTest {

    @Test
    fun sample1() {

        val readLines = readSampleInput(13, 1).readLines()
        val map = Map.build(readLines)

        assertEquals(Pair(0, 3), map.findFirstCrashLocation())
    }

    @Test
    fun sample2() {

        val readLines = readSampleInput(13, 2).readLines()
        val map = Map.build(readLines)

        assertEquals(Pair(7, 3), map.findFirstCrashLocation())
    }

    @Test
    fun actual() {
        val readLines = readInput(13).readLines()
        val map = Map.build(readLines)

        val crashLocation = map.findFirstCrashLocation()
        assertNotEquals(Pair(55, 40), crashLocation)
        println(crashLocation)
    }

    @Test
    fun sample3() {
        val readLines = listOf("->>--")
        val map = Map.build(readLines)

        assertNotEquals(Pair(0, 2), map.findFirstCrashLocation())
    }

}