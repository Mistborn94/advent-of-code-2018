package day18

import helper.readInput
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstructionProjectTest {

    @Test
    fun testSampleInput() {
        val map = """
        .#.#...|#.
        .....#|##|
        .|..|...#.
        ..|#.....#
        #.#|||#|#|
        ...#.||...
        .|....|...
        ||...#|.#|
        |.||||..|.
        ...#.|..|.
        """.lines().map { it.trim() }.filter { it.isNotEmpty() }

        val project = ConstructionProject.build(map)

        val solutionA = project.partA()

        assertEquals(1147, solutionA)
    }

    @Test
    fun testActual() {
        val map = readInput(18).readLines()
        val project = ConstructionProject.build(map)

        val solutionA = project.partA()

        assertEquals(623583, solutionA)

        val solutionB = project.partB()
        assertEquals(107912, solutionB)
    }
}


