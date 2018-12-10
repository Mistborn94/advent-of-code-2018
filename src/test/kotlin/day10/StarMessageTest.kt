package day10

import helper.readInput
import helper.readSampleInput
import org.junit.jupiter.api.Test


internal class StarMessageTest {

    @Test
    fun getInitialStars_sample() {
        val message = StarMessage("sample1", 10, readSampleInput(10, 1).readLines())

        message.getBestMessageDays()
    }


    @Test
    fun getInitialStars_actual() {
        val message = StarMessage("actual", 15000, readInput(10).readLines())

        println("Starting")
        message.getBestMessageDays()
    }
}