package day10

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

val pattern = "position=< ?(-?\\d+), +(-?\\d+)> velocity=< ?(-?\\d+), +(-?\\d+)>".toRegex()

class StarMessage(val id: String, val maxIterations: Int, points: List<String>) {

    val initialStars: List<Star> = points
            .asSequence()
            .map { pattern.matchEntire(it)!!.destructured }
            .map { (x, y, vx, vy) -> Star(x.toInt(), y.toInt(), vx.toInt(), vy.toInt()) }
            .toList()

    private val basePath = "src/main/resources/day10/output/$id"

    fun getBestMessageDays() {
        val basePath = prepareDirectory()

        val currentStars = initialStars.map { it.copy() }

        for (i in (1..maxIterations)) {

            currentStars.forEach { it.move() }

            val minY = currentStars.map { it.position.y }.min()!!
            val maxY = currentStars.map { it.position.y }.max()!!
            val minX = currentStars.map { it.position.x }.min()!!
            val maxX = currentStars.map { it.position.x }.max()!!

            val height = maxY - minY + 1
            val width = maxX - minX + 1


            if (height < 20) {
                val grid: Array<CharArray> = Array(height) { CharArray(width) { ' ' } }

                currentStars.forEach {
                    grid[it.position.y - minY][it.position.x - minX] = '#'
                }

                val hasBlankLines = hasBlankLines(grid)
                val hasBlankColumns = hasBlankColumns(grid)

                if (!hasBlankLines && hasBlankColumns) {
                    println("Result found at iteration $i")
                    Files.write(File("$basePath/$i.out").toPath(), grid.map { it.joinToString("") })
                }
            }
        }
    }

    private fun prepareDirectory(): File {
        val basePath = File(basePath)

        if (basePath.exists()) {
            Files.walk(basePath.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach { it.delete() }
        }

        Files.createDirectories(basePath.toPath())
        return basePath
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

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}