package day6

import helper.readInput
import java.io.File
import kotlin.math.abs

/*
 * A: 47, 4016
*  B: 46306
 */
fun main(args: Array<String>) {

    val coordinates = buildCoordinates(readInput(6))

    val grid = Grid.fromCoordinates(coordinates)

    val (coordinateId, area) = grid.solveA()
    println("A: $coordinateId, $area")

    val bestArea = grid.solveB()
    println("B: ${bestArea.size}")
}

private fun buildCoordinates(file: File): List<Coordinate> {
    return file.readLines()
        .map { it.split(", ") }
        .mapIndexed { i, (x, y) -> Coordinate(i, x.toInt(), y.toInt()) }
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

class Grid(
    width: Int, height: Int, private val grid: List<GridLocation>,
    private val distanceThreshold: Int
) {
    private val size = width * height

    private val edgeIndices: List<Int>

    init {
        val topEdge = 0 until width
        val bottomEdge = (size - width) until size
        val leftEdge = IntProgression.fromClosedRange(0, size - width, width)
        val rightEdge = IntProgression.fromClosedRange(width - 1, size - 1, width)

        edgeIndices = rightEdge
            .plus(bottomEdge)
            .plus(topEdge)
            .plus(leftEdge)
    }

    private val infiniteCoordinates: Set<Int>
        get() {
            return edgeIndices
                .map { this[it].closestPoint }
                .plus(-1)
                .toSet()
        }

    fun solveA(): Map.Entry<Int, Int> {
        val infiniteCoordinates = infiniteCoordinates

        return grid.groupBy { it.closestPoint }
            .filter { !infiniteCoordinates.contains(it.key) }
            .mapValues { it.value.size }
            .maxBy { it.value }!!
    }

    fun solveB() = grid.filter { it.totalDistance() < distanceThreshold }

    operator fun get(it: Int): GridLocation = grid[it]

    companion object {
        fun fromCoordinates(coordinates: List<Coordinate>, distanceThreshold: Int = 10000): Grid {
            val width = coordinates.map { it.x }.max()!!
            val height = coordinates.map { it.y }.max()!!
            val locations = locationList(width, height)

            for (coordinate in coordinates) {
                for (location in locations) {
                    location.compareToCoordinate(coordinate)
                }
            }

            return Grid(width, height, locations, distanceThreshold)
        }

        private fun locationList(width: Int, height: Int): List<GridLocation> {
            val zip = (0 until width)
                .flatMap { x ->
                    (0 until height).map { y -> Pair(x, y) }
                }

            return zip.map { GridLocation(it.first, it.second) }
        }
    }
}