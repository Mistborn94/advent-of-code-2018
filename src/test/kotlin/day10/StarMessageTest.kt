package day10

import helper.readInput
import helper.readSampleInput
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


internal class StarMessageTest {

    @Test
    fun getInitialStars_sample() {
        val message = StarMessage("sample1", 10, readSampleInput(10, 1).readLines())
        val (iteration, map) = message.getBestMessageDays()
        assertEquals(3, iteration)
        map.forEach { println(it) }
    }

    @Test
    fun getInitialStars_actual() {
        val message = StarMessage("actual", 15000, readInput(10).readLines())
        val (iteration, map) = message.getBestMessageDays()
        assertEquals(10634, iteration)
        map.forEach { println(it) }
    }
}