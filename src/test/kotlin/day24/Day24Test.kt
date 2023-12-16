package day24

import helper.readInput
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Day24Test {

    @Test
    fun sample1() {
        val input = """Immune System:
17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3

Infection:
801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4
        """.trimIndent()

        assertEquals(5216, solveA(input))
        assertEquals(51, solveB(input))
    }


    @Test
    fun actual() {
        val input = readInput(24).readText()

        val partA = solveA(input)
        assertEquals(16090, partA)
        println("A: $partA")
        val partB = solveB(input)
//        assertEquals(0, partB)
        println("B: $partB")
    }
}