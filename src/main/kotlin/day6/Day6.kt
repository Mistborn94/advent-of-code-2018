package day6

import helper.readInput
import kotlin.math.abs

/*
 * A: 47, 4016
*  B: 46306
 */
fun main(args: Array<String>) {

    val grid = Grid.fromCoordinates(readInput(6).readLines()
             .map { it.split(", ") }
             .mapIndexed { i, (x, y) -> Coordinate(i, x.toInt(), y.toInt()) })

    //Coordinates on right and bottom edges are infinite
    val edges = grid.getEdgeIndices()

    val infiniteCoordinates = edges
            .map { grid[it].closestPoint }
            .plus(-1)
            .toSet()

    val (coordinateId, area) = grid.grid.groupBy { it.closestPoint }
            .filter { !infiniteCoordinates.contains(it.key) }
            .mapValues { it.value.size }
            .maxBy { it.value }!!

    println("A: $coordinateId, $area")

    val bestArea = grid.grid.filter { it.totalDistance() < 10000 }

    println("B: ${bestArea.size}")
}

data class Coordinate(val id: Int, val x: Int, val y: Int)

data class GridLocation(val x: Int, val y: Int, var closestPoint: Int, var closestDistance: Int) {
    constructor(x: Int, y: Int) : this(x, y, -1, Int.MAX_VALUE)

    private val allDistances = mutableMapOf<Int, Int>()

    fun compareToCoordinate(coordinate: Coordinate) {
        val distance = abs(x - coordinate.x) + abs(y - coordinate.y)

        if (distance < this.closestDistance) {
            closestPoint = coordinate.id
            this.closestDistance = distance
        } else if (distance == this.closestDistance) {
            closestPoint = -1
        }

        allDistances[coordinate.id] = distance
    }

    fun totalDistance(): Int {
        return allDistances.values.sum()
    }
}

class Grid(val width: Int, val height: Int, val grid: List<GridLocation>) {
    val size = width * height

    operator fun get(it: Int): GridLocation = grid[it]

    fun getEdgeIndices(): List<Int> {
        val topEdge = 0 until width
        val bottomEdge = (size - width) until size
        val leftEdge = IntProgression.fromClosedRange(0, size - width, width)
        val rightEdge = IntProgression.fromClosedRange(width - 1, size, width)

        val edges = rightEdge
                .plus(bottomEdge)
                .plus(topEdge)
                .plus(leftEdge)
        return edges
    }

    companion object {
        fun fromCoordinates(coordinates: List<Coordinate>): Grid {
            val width = coordinates.map { it.x }.max()!!
            val height = coordinates.map { it.y }.max()!!
            val locations  = locationList(width, height)

            for (coordinate in coordinates) {
                for (location in locations) {
                    location.compareToCoordinate(coordinate)
                }
            }

            return Grid(width, height, locations)
        }

        private fun locationList(width: Int, height: Int) = (0 until width)
                .zip(0 until height)
                .map { GridLocation(it.first, it.second) }
    }

}