package day13

import helper.readInput
import helper.readSampleInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class MapTest {

    @Test
    fun sample1() {
        val readLines = readSampleInput(13, 1).readLines()
        val map = Map.build(readLines)

        assertEquals(Pair(0, 3), map.findFirstCrashLocation())
        assertEquals(Pair(0, 6), map.findLastCartLocation())
    }

    @Test
    fun sample2() {

        val readLines = readSampleInput(13, 2).readLines()
        val map = Map.build(readLines)

        assertEquals(Pair(7, 3), map.findFirstCrashLocation())
    }

    @Test
    fun sample3() {
        val readLines = readSampleInput(13, 3).readLines()
        val map = Map.build(readLines)

        assertEquals(Pair(6, 4), map.findLastCartLocation())
    }

    @Test
    fun sample4() {
        val readLines = readSampleInput(13, 4).readLines()
        val map = Map.build(readLines)

        assertEquals(Pair(83, 49), map.findFirstCrashLocation(), "A must be correct")
        assertEquals(Pair(73, 36), map.findLastCartLocation(), "B must be correct")
    }

    @Test
    fun actual() {
        val readLines = readInput(13).readLines()
        val map = Map.build(readLines)

        val crashLocation = map.findFirstCrashLocation()
        assertEquals(Pair(41, 17), crashLocation)
        println("A: $crashLocation")

        val lastLocation = map.findLastCartLocation()
        assertEquals(Pair(134,117), lastLocation)
        println("B: $lastLocation")
    }

    @Test
    fun edgeCase1() {
        val readLines = listOf("->>>-")
        val map = Map.build(readLines)

        assertEquals(Pair(2, 0), map.findFirstCrashLocation())
        assertEquals(Pair(4, 0), map.findLastCartLocation())
    }

    @Test
    fun edgeCase3() {
        val readLines = listOf("/>>->\\", "    |")
        val map = Map.build(readLines)

        assertEquals(Pair(2, 0), map.findFirstCrashLocation())
        assertEquals(Pair(5, 0), map.findLastCartLocation())
    }

    @Test
    fun edgeCase2() {
        val readLines = listOf("/", "v", "v", "v", "|")
        val map = Map.build(readLines)

        assertEquals(Pair(0, 2), map.findFirstCrashLocation())
        assertEquals(Pair(0, 4), map.findLastCartLocation())
    }

}