package helper

import kotlin.math.abs

data class HyperspacePoint(val dimensions: List<Int>) {

    fun manhattanDistance(other: HyperspacePoint) = dimensions.zip(other.dimensions).sumOf { (a, b) -> abs(a - b) }
}

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

data class Point3D(val x: Int, val y: Int, val z: Int) {
    //: Comparable<Point3D> {
    operator fun plus(other: Point3D): Point3D = Point3D(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Point3D): Point3D = Point3D(x - other.x, y - other.y, z - other.z)
    operator fun times(factor: Int): Point3D = Point3D(x * factor, y * factor, z * factor)
    operator fun div(factor: Int): Point3D = Point3D(x / factor, y / factor, z / factor)

    fun manhattanDistance(other: Point3D) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
    fun absolute(): Point3D = Point3D(abs(x), abs(y), abs(z))

    companion object {
        val ZERO = Point3D(0, 0, 0)
        val ONE = Point3D(1, 1, 1)
        val TWO = Point3D(1, 1, 1)

        val COMPARATOR: Comparator<Point3D> = compareBy<Point3D> { it.x }.thenBy { it.y }.thenBy { it.z }
    }
}
