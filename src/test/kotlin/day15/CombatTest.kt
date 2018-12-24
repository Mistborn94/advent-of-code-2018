package day15

import helper.readInput
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CombatTest {

    @Test
    fun test_sample1() {
        val input = listOf(
            "#######",
            "#.G...#",
            "#...EG#",
            "#.#.#G#",
            "#..G#E#",
            "#.....#",
            "#######"
        )

        val combatGame = Combat(input)

        val score = combatGame.simulate()
        assertEquals(27730, score)
    }

    @Test
    fun test_sample2() {
        val input = listOf(
            "#######",
            "#G..#E#",
            "#E#E.E#",
            "#G.##.#",
            "#...#E#",
            "#...E.#",
            "#######"
        )

        val combatGame = Combat(input)

        val score = combatGame.simulate()
        assertEquals(36334, score)
    }

    @Test
    fun test_sample3() {
        val input = listOf(
            "#######",
            "#E..EG#",
            "#.#G.E#",
            "#E.##E#",
            "#G..#.#",
            "#..E#.#",
            "#######"
        )

        val combatGame = Combat(input)

        val score = combatGame.simulate()
        assertEquals(39514, score)
    }

    @Test
    fun test_sample4() {
        val input = listOf(
            "#######",
            "#E.G#.#",
            "#.#G..#",
            "#G.#.G#",
            "#G..#.#",
            "#...E.#",
            "#######"
        )

        val combatGame = Combat(input)

        val score = combatGame.simulate()
        assertEquals(27755, score)
    }

    @Test
    fun test_sample5() {
        val input = listOf(
            "#######",
            "#.E...#",
            "#.#..G#",
            "#.###.#",
            "#E#G#G#",
            "#...#G#",
            "#######"
        )

        val combatGame = Combat(input)

        val score = combatGame.simulate()
        assertEquals(28944, score)
    }

    @Test
    fun test_sample6() {
        val input = listOf(
            "#########",
            "#G......#",
            "#.E.#...#",
            "#..##..G#",
            "#...##..#",
            "#...#...#",
            "#.G...G.#",
            "#.....G.#",
            "#########"
        )

        val combatGame = Combat(input)

        val score = combatGame.simulate()
        assertEquals(18740, score)
    }

    @Test
    @Ignore
    fun test_actual() {
        val input = readInput(15).readLines()

        val combatGame = Combat(input)

        val score = combatGame.simulate()
        assertEquals(18740, score)
    }
}