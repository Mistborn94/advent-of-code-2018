package helper

class Grid<T>(
    private val dimensions: Int = 2,
    private val sizes: List<Int>,
    private val offsets: List<Int> = Array(dimensions) { 0 }.asList(),
    init: (IntArray) -> T
): Iterable<T> {

    val size = sizes.reduce { a, i -> a * i }
    private val elements: MutableList<T> = ArrayList(size)
    private val foldedDimensionSizes = sizes.fold(listOf(1)) { acc: List<Int>, i -> acc + i * acc.last() }
    private val ranges = offsets.zip(sizes) { a, b -> a until (a + b) }

    init {
        for (i in 0 until size) {
            elements.add(init(fromIndex(i)))
        }
    }

    operator fun get(vararg coordinate: Int): T {
        validateCoordinate(coordinate)
        return elements[toIndex(*coordinate)]
    }

    operator fun set(vararg coordinate: Int, value: T) {
        validateCoordinate(coordinate)
        elements[toIndex(*coordinate)] = value
    }

    fun inRange(vararg coordinate: Int): Boolean {
        return coordinate.withIndex().all { (dimension, c) -> c in ranges[dimension] }
    }

    private fun validateCoordinate(coordinate: IntArray) {
        if (coordinate.size != dimensions) {
            throw IllegalArgumentException("Cannot use coordinate $coordinate for $dimensions dimensional grid")
        }

        coordinate.forEachIndexed { dimension, c ->
            if (c !in ranges[dimension]) {
                throw IndexOutOfBoundsException("$c out of range for dimension $dimension")
            }
        }
    }

    fun toIndex(vararg coordinate: Int): Int {
        validateCoordinate(coordinate)

        val normalizedCoordinates = coordinate.mapIndexed { i, c ->
            c - offsets[i]
        }

        var total = normalizedCoordinates[0]
        for (i in 1 until dimensions) {
            total += normalizedCoordinates[i] * foldedDimensionSizes[i]
        }
        return total
    }

    fun fromIndex(index: Int): IntArray {
        if (index >= size) {
            throw IndexOutOfBoundsException("$index not in grid ${sizes.joinToString("x")}")
        }

        val coordinate = IntArray(dimensions)

        for (i in 0 until dimensions) {
            coordinate[i] = index % foldedDimensionSizes[i + 1] / foldedDimensionSizes[i] + offsets[i]
        }

        return coordinate
    }

    fun indexOf(element: T): IntArray = fromIndex(elements.indexOf(element))

    fun contains(element: T): Boolean = elements.contains(element)

    override fun iterator(): Iterator<T> = elements.iterator()

    companion object {
        fun <T> fromMinMax(dimensionCount: Int, min: List<Int>, max: List<Int>, init: (IntArray) -> T): Grid<T> {
            val sizes = max.zip(min) { a, b -> a - b + 1 }
            return Grid(dimensionCount, sizes, min, init)
        }

        fun <T> fromMinMax(dimensionCount: Int, min: List<Int>, max: List<Int>): Grid<T?> {
            return fromMinMax(dimensionCount, min, max) { null }
        }
    }
}