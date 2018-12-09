package day9

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.Test
import kotlin.test.assertEquals

class MarbleGameTest {
    companion object {
        private val samplePattern = "(\\d+) players; last marble is worth (\\d+) points: high score is (\\d+)".toRegex()
        private val actualPattern = "(\\d+) players; last marble is worth (\\d+) points".toRegex()

        @JvmStatic
        fun sampleData() =
            listOf(
                "10 players; last marble is worth 1618 points: high score is 8317",
                "13 players; last marble is worth 7999 points: high score is 146373",
                "17 players; last marble is worth 1104 points: high score is 2764",
                "21 players; last marble is worth 6111 points: high score is 54718",
                "30 players; last marble is worth 5807 points: high score is 37305"
            ).map {
                samplePattern.matchEntire(it)!!.destructured
            }.map { ( players, lastMarble, score) ->
                arrayOf(players.toInt(), lastMarble.toInt(), score.toLong())
            }.toTypedArray()

    }

    @ParameterizedTest(name = "Sample {index}: {0} players, {1} points")
    @MethodSource("sampleData")
    fun testSamples(playerCount: Int, highestMarble: Int, expectedScorePartA: Long) {
        val game = MarbleGame(playerCount, highestMarble)
        val highScore = game.highestScore
        assertEquals(expectedScorePartA, highScore)
    }

    @Test
    fun testActual() {
        val input = "446 players; last marble is worth 71522 points"

        val (players, points) = actualPattern.matchEntire(input)!!
            .groupValues
            .slice(1.. 2)
            .map { it.toInt() }

        val gameA = MarbleGame(players, points)
        assertEquals(390592, gameA.highestScore)

        val gameB = MarbleGame(players, points*100)
        assertEquals(3277920293, gameB.highestScore)
    }

}