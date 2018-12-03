package day3

import helper.readInput

const val FABRIC_SIZE = 1000
val pattern = """#(\d+) @ (\d+),(\d+): (\d+)x(\d+)""".toRegex()

fun main(args: Array<String>) {
    // #1 @ 1,3: 4x4
    val rectangles = readInput(3).readLines()
            .map { pattern.matchEntire(it)!!.destructured }
            .map { (id, left, top, width, height) -> Rectangle(id.toInt(), left.toInt(), top.toInt(), width.toInt(), height.toInt()) }

    val fabric = buildFabricDiagram(rectangles)

    println("A: " + fabric.map { it.count { value -> value > 1 } }.sum())

    val uniqueRectangle = findNonOverlapping(rectangles, fabric)

    println("B: " + uniqueRectangle.id)
}

fun findNonOverlapping(rectangles: List<Rectangle>, fabric: Array<Array<Int>>): Rectangle {
    return rectangles.first { rect ->
        fabric.sliceArray(rect.horizontalRange())
                .map {
                    it.sliceArray(rect.verticalRange()).count { it > 1 }
                }.sum() == 0
    }
}

private fun buildFabricDiagram(rectangles: List<Rectangle>): Array<Array<Int>> {
    val fabric = Array(FABRIC_SIZE) { Array(FABRIC_SIZE) { 0 } }

    rectangles.forEach { rect ->
        val widthSlice = fabric.sliceArray(rect.horizontalRange())
        widthSlice.forEach {
            for (i in rect.verticalRange()) {
                it[i] += 1
            }
        }
    }
    return fabric
}

data class Rectangle(
        val id: Int,
        private val x: Int,
        private val y: Int,
        private val width: Int,
        private val height: Int) {

    fun horizontalRange() = x until x + width
    fun verticalRange() = y until y + height
}

