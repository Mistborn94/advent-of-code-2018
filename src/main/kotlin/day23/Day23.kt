package day23

import helper.Point3D
import helper.combineAll
import java.util.*
import kotlin.math.max
import kotlin.math.min

data class Nanobot(val location: Point3D, var r: Int) {

    constructor(x: Int, y: Int, z: Int, r: Int) : this(Point3D(x, y, z), r)

    fun distance(point: Point3D): Int = location.manhattanDistance(point)

    fun inRange(other: Nanobot): Boolean = distance(other.location) <= r
    fun inRange(point: Point3D): Boolean = distance(point) <= r

    fun intersects(box: Box): Boolean = inRange(box.clamp(location))
}

class Day23(input: List<String>) {

    val regex = "pos=<([-0-9]+),([-0-9]+),([-0-9]+)>, r=([-0-9]+)".toRegex()
    val swarm: List<Nanobot>

    init {
        swarm = input.map(regex::matchEntire)
            .map { it!!.destructured }
            .map{ (x, y, z, r) -> Nanobot(x.toInt(), y.toInt(), z.toInt(), r.toInt()) }
    }

    fun solvePartA(): Int {
        val powerfulBot = swarm.maxBy { it.r }!!
        return swarm.count { powerfulBot.inRange(it) }
    }

    fun partB(): Int {
        val min = swarm.map { it.location }
            .reduce { a, b -> Point3D(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z)) } - Point3D.ONE

        val max = swarm.map { it.location }
            .reduce { a, b -> Point3D(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z)) } + Point3D.ONE

        val comparator = compareBy<Pair<Box, Int>> { it.second }.reversed().thenBy(Point3D.COMPARATOR) { it.first.min }
        val queue = PriorityQueue<Pair<Box, Int>>(comparator)

        var currentParent = Box(min, max)

        while (currentParent.dimensions != Point3D.ONE) {
            val newBoxes = currentParent.divide().map { Pair(it, it.intersectCount(swarm)) }.filter { it.second > 1 }
            queue.addAll(newBoxes)
            currentParent = queue.remove().first
        }

        return currentParent.min.manhattanDistance(Point3D(0, 0, 0))
    }
}

data class Box(val min: Point3D, val max: Point3D, val depth: Int = 1) {

    init {
        if (
            min.x > max.x
            || min.y > max.y
            || min.z > max.z
        ) throw IllegalArgumentException("$max must be greater than $min")
    }

    val dimensions = (max - min)
    private val hasVolume = dimensions.x != 0 && dimensions.y != 0 && dimensions.z != 0

    fun divide(): Collection<Box> {
        val middle = (min + max) / 2
        val coordinates = listOf(min, middle, max)
        val options = CHILD_OFFSETS

        return options.map {
            childrenFromCoordinates(coordinates, it.x, it.y, it.z)
        }.filter { it.hasVolume && it != this }.toSet()
    }

    fun clamp(point: Point3D): Point3D {
        val x = clampDimension(point) { it.x }
        val y = clampDimension(point) { it.y }
        val z = clampDimension(point) { it.z }

        return Point3D(x, y, z)
    }

    private inline fun clampDimension(point: Point3D, selector: (Point3D) -> Int): Int {
        val value = selector(point)
        val min = selector(min)
        val max = selector(max) - 1
        return clamp(min, max, value)
    }

    private fun clamp(min: Int, max: Int, value: Int) =
        if (value < min) min else if (value > max) max else value

    fun intersectCount(swarm: List<Nanobot>) = swarm.count { it.intersects(this) }

    override fun toString(): String {
        return "Box(${coordinate(min)} to ${coordinate(max)})"
    }

    private fun coordinate(point: Point3D): String = point.run { "[$x, $y, $z]" }

    private fun childrenFromCoordinates(
        coordinates: List<Point3D>,
        x: Int,
        y: Int,
        z: Int
    ) = Box(
        Point3D(coordinates[x].x, coordinates[y].y, coordinates[z].z),
        Point3D(coordinates[x + 1].x, coordinates[y + 1].y, coordinates[z + 1].z),
        depth + 1
    )

    companion object {
        private val CHILD_OFFSETS: List<Point3D> = combineAll(0..1, 0..1, 0..1).map { (x,y,z) -> Point3D(x,y,z) }
    }
}

