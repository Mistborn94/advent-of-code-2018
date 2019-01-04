package day10

import helper.Point

val pattern = "position=< ?(-?\\d+), +(-?\\d+)> velocity=< ?(-?\\d+), +(-?\\d+)>".toRegex()

class StarMessage(id: String, private val maxIterations: Int, points: List<String>, private val textHeight: Int = 10) {

    private val initialStars: List<Star> = points
            .asSequence()
            .map { pattern.matchEntire(it)!!.destructured }
            .map { (x, y, vx, vy) -> Star(x.toInt(), y.toInt(), vx.toInt(), vy.toInt()) }
            .toList()

    fun getBestMessageDays(): Pair<Int, List<String>> {

        val currentStars = initialStars.map { it.copy() }

        for (i in (1..maxIterations)) {

            currentStars.forEach { it.move() }

            val minY = currentStars.map { it.position.y }.min()!!
            val maxY = currentStars.map { it.position.y }.max()!!
            val minX = currentStars.map { it.position.x }.min()!!
            val maxX = currentStars.map { it.position.x }.max()!!

            val height = maxY - minY + 1
            val width = maxX - minX + 1

            if (height <= textHeight) {
                val grid: Array<CharArray> = Array(height) { CharArray(width) { ' ' } }

                currentStars.forEach {
                    grid[it.position.y - minY][it.position.x - minX] = '#'
                }

                val hasBlankLines = hasBlankLines(grid)
                val hasBlankColumns = hasBlankColumns(grid)

                if (!hasBlankLines && hasBlankColumns) {
                    val map = grid.map { it.joinToString("") }
                    return Pair(i , map)
                }
            }
        }

        throw IllegalStateException("No solution found")
    }

    private fun hasBlankLines(grid: Array<CharArray>) = grid.any { !it.contains('#') }

    private fun hasBlankColumns(grid: Array<CharArray>): Boolean {
        for (i in 0..grid[0].lastIndex) {
            val map = grid.map { it[i] }
            if (!map.contains('#'))
                return true
        }
        return false
    }

}

data class Star(var position: Point, val velocity: Point) {

    constructor(xPos: Int, yPos: Int, xVel: Int, yVel: Int) : this(Point(xPos, yPos), Point(xVel, yVel))

    fun move() {
        this.position = this.position + this.velocity
    }
}
