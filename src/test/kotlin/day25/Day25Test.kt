package day25

import helper.readInput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class Day25Test {

    @Test
    fun sample1() {
        val input = """0,0,0,0
3,0,0,0
0,3,0,0
0,0,3,0
0,0,0,3
0,0,0,6
9,0,0,0
12,0,0,0""".trimIndent()

        assertEquals(2, solve(input))
    }

    @Test
    fun sample2() {
        val input = """-1,2,2,0
0,0,2,-2
0,0,0,-2
-1,2,0,0
-2,-2,-2,2
3,0,2,-1
-1,3,2,2
-1,0,-1,0
0,2,1,-2
3,0,0,0""".trimIndent()

        assertEquals(4, solve(input))
    }

    @Test
    fun sample3() {
        val input = """1,-1,0,1
2,0,-1,0
3,2,-1,0
0,0,3,1
0,0,-1,-1
2,3,-2,0
-2,2,0,0
2,-2,0,-1
1,-1,0,-1
3,2,0,2""".trimIndent()

        assertEquals(3, solve(input))
    }

    @Test
    fun sample4() {
        val input = """1,-1,-1,-2
-2,-2,0,1
0,2,1,3
-2,3,-2,1
0,2,3,-2
-1,-1,1,-2
0,-2,-1,0
-2,2,3,-1
1,2,2,0
-1,-2,0,-2""".trimIndent()

        assertEquals(8, solve(input))
    }
    @Test
    fun actual() {
        val input = readInput(25).readText()

        val partA = solve(input)
        assertEquals(305, partA)
        println("A: $partA")
    }
}