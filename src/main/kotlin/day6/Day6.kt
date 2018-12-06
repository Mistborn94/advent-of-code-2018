package day6

import helper.readInput
import kotlin.math.abs

fun main(args: Array<String>) {

    val coordinates = readInput(6).readLines()
            .map { it.split(", ") }
            .mapIndexed { i, (x, y) -> Coordinate(i, x.toInt(), y.toInt()) }

    val width = coordinates.map { it.x }.max()!!
    val height = coordinates.map { it.y }.max()!!
    val gridSize = width * height
    val grid = Array(gridSize) { i -> GridLocation(i % width, i / width) }

    for (coordinate in coordinates) {
        for (location in grid) {
            location.compareToCoordinate(coordinate)
        }
    }

    //Coordinates on right and bottom edges are infinite
    val topEdge = 0 until width
    val bottomEdge = (gridSize - width) until gridSize
    val leftEdge = IntProgression.fromClosedRange(0, gridSize - width, width)
    val rightEdge = IntProgression.fromClosedRange(width - 1, gridSize, width)

    val infiniteCoordinates = rightEdge
            .plus(bottomEdge)
            .plus(topEdge)
            .plus(leftEdge)
            .map { grid[it].closestPoint }
            .plus(-1)
            .toSet()

    val (coordinateId, area) = grid.groupBy { it.closestPoint }
            .filter { !infiniteCoordinates.contains(it.key) }
            .mapValues { it.value.size }
            .maxBy { it.value }!!

    println("A: $coordinateId, $area")

    val bestArea = grid.filter { it.totalDistance() < 10000 }

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