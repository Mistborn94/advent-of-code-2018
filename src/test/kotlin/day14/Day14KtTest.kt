package day14

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day14KtTest {

    @Test
    internal fun test_sample() {
        assertEquals("5158916779", scoresOfNextTen(9), "Sample 1 A")
        assertEquals("0124515891", scoresOfNextTen(5), "Sample 2 A")
        assertEquals("9251071085", scoresOfNextTen(18), "Sample 3 A")
        assertEquals("5941429882", scoresOfNextTen(2018),"Sample 4 A")

        assertEquals(9, recipesBefore("51589"), "Sample 1 B")
        assertEquals(5, recipesBefore("01245"), "Sample 2 B")
        assertEquals(18, recipesBefore("92510"), "Sample 3 B")
        assertEquals(2018, recipesBefore("59414"),"Sample 4 B")
    }


    @Test
    internal fun test_actual() {
        val input = 635041

        val scoresOfNextTen = scoresOfNextTen(input)
        assertEquals("1150511382", scoresOfNextTen)
        println("A $scoresOfNextTen")

        val recipesBeforePattern = recipesBefore(input.toString())
        assertNotEquals(37144409, recipesBeforePattern)
        assertNotEquals(20173657, recipesBeforePattern)
        assertTrue(recipesBeforePattern < 20173657)

        println("B $recipesBeforePattern")
    }
}