package day15

import helper.Point
import helper.readInput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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

        val score = combatGame.simulate().first
        assertEquals(27730, score)

        val partB = findBestElvenAttackScore(input)
        assertEquals(4988, partB)
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

        val score = combatGame.simulate().first
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

        val score = combatGame.simulate().first
        assertEquals(39514, score)

        val partB = findBestElvenAttackScore(input)
        assertEquals(31284, partB)
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

        val score = combatGame.simulate().first
        assertEquals(27755, score)

        val partB = findBestElvenAttackScore(input)
        assertEquals(3478, partB)
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

        val score = combatGame.simulate().first
        assertEquals(28944, score)

        val partB = findBestElvenAttackScore(input)
        assertEquals(6474, partB)
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

        val score = combatGame.simulate().first
        assertEquals(18740, score)

        val partB = findBestElvenAttackScore(input)
        assertEquals(1140, partB)
    }

    @Test
    fun test_actual() {
        val input = readInput(15).readLines()

        val combatGame = Combat(input)

        val score = combatGame.simulate().first
        assertEquals(201123, score)

        val partB = findBestElvenAttackScore(input)
        println("B: $partB")

        assertNotEquals(14, partB)
        assertNotEquals(13, partB)
        assertNotEquals(7, partB)
        assertNotEquals(15, partB)
    }


    @Test
    fun test_movementEdgeCase_1() {
        val input = listOf(
            "#######",
            "#.E..G#",
            "#.#####",
            "#G#####",
            "#######"
        )

        val combatGame = Combat(input, 15)
        val elf = combatGame.elves.first()
        val (target, _) = combatGame.bfs(elf)!!

        assertEquals(Point(3, 1), target.position)
    }
}