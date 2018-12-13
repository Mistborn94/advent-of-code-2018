package day13

class Map(val tracks: List<List<PathSegment?>>, val carts: List<Cart>) {

    private val cartComparator = Comparator.comparing { c: Cart -> c.y }
            .thenComparing { c: Cart -> c.x }

    fun reset() {
        carts.forEach { it.reset() }
    }

    fun findFirstCrashLocation(): Pair<Int, Int> {
        reset()
        var iteration = 0
        while (true) {
//            printMap(iteration, carts)

            val sortedCarts = carts.sortedWith(cartComparator)

            val cartLocations = mutableSetOf<Pair<Int, Int>>()

            for (cart in sortedCarts) {
                val track = tracks[cart.y][cart.x] ?: throw IllegalStateException("No track at ${cart.x}, ${cart.y}")

                track.move(cart)
                val element = Pair(cart.x, cart.y)
                val added = cartLocations.add(element)

                if (!added) {
                    return element
                }
            }

            iteration++
        }
    }

    fun printMap(iteration: Int, carts: List<Cart>) {
        val chars = tracks.map {
            it.map { it?.symbol ?: ' ' }.toMutableList()
        }.toMutableList()

        for (cart in carts) {
            chars[cart.y][cart.x] = cart.facing.symbol
        }

        println("Iteration $iteration")
        chars.forEach { println(it.joinToString ("" )) }
        println()
    }

    companion object {
        fun build(lines: List<String>): Map {
            val carts = mutableListOf<Cart>()

            val path = lines.mapIndexed { y, line ->
                line.mapIndexed { x, char ->
                    when (char) {
                        ' ' -> null
                        '-', '|' -> StraightPath(char)
                        '+' -> Intersection(char)
                        '>' -> {
                            carts.add(Cart(x, y, Direction.RIGHT)); StraightPath('-')
                        }
                        '<' -> {
                            carts.add(Cart(x, y, Direction.LEFT)); StraightPath('-')
                        }
                        '^' -> {
                            carts.add(Cart(x, y, Direction.UP)); StraightPath('|')
                        }
                        'v' -> {
                            carts.add(Cart(x, y, Direction.DOWN)); StraightPath('|')
                        }
                        '/', '\\' -> {
                            val horizontal = if (x == 0 || !isHorizontal(lines[y][x - 1])) {
                                Direction.RIGHT
                            } else {
                                Direction.LEFT
                            }

                            val vertical = if (y == 0 || !isVertical(lines[y - 1][x])) {
                                Direction.DOWN
                            } else {
                                Direction.UP
                            }

                            CurvedPath(vertical, horizontal, char)
                        }

                        else -> throw IllegalArgumentException("Unknown Character $char")

                    }
                }

            }

            return Map(path, carts)
        }

        private fun isHorizontal(c: Char): Boolean {
            return setOf('-', '>', '<', '+').contains(c)
        }

        private fun isVertical(c: Char): Boolean {
            return setOf('|', '^', 'v', '+').contains(c)
        }
    }
}

enum class Direction {
    UP {
        override val horizontal: Boolean = false
        override val symbol: Char = '^'

        override fun moveX(x: Int): Int = x
        override fun moveY(y: Int): Int = y - 1

        override fun turnLeft(): Direction = LEFT
        override fun turnRight(): Direction = RIGHT
    },
    RIGHT {
        override val horizontal: Boolean = true
        override val symbol: Char = '>'

        override fun moveX(x: Int): Int = x + 1
        override fun moveY(y: Int): Int = y

        override fun turnLeft(): Direction = UP
        override fun turnRight(): Direction = DOWN
    },
    DOWN {
        override val horizontal: Boolean = false
        override val symbol: Char = 'v'

        override fun moveX(x: Int): Int = x
        override fun moveY(y: Int): Int = y + 1

        override fun turnLeft(): Direction = RIGHT
        override fun turnRight(): Direction = LEFT
    },
    LEFT {
        override val horizontal: Boolean = true
        override val symbol: Char = '<'

        override fun moveX(x: Int): Int = x - 1
        override fun moveY(y: Int): Int = y

        override fun turnLeft(): Direction = DOWN
        override fun turnRight(): Direction = UP
    };

    abstract fun moveX(x: Int): Int
    abstract fun moveY(y: Int): Int

    abstract val horizontal: Boolean
    abstract val symbol: Char

    abstract fun turnLeft(): Direction
    abstract fun turnRight(): Direction
}

data class Cart(var x: Int, var y: Int, var facing: Direction) {
    val initialX = x
    val initialY = y
    val intialFacing = facing

    var intersections = 0

    private val newDirections = listOf(
            { this.facing.turnLeft() },
            { this.facing },
            { this.facing.turnRight() })

    fun moveInFacingDirection() {
        x = facing.moveX(x)
        y = facing.moveY(y)
    }

    fun handleIntersection() {
        val instructionIndex = intersections++ % 3
        this.facing = newDirections[instructionIndex]()
    }

    fun reset() {
        x = initialX
        y = initialY
        facing = intialFacing
    }
}

abstract class PathSegment(val symbol: Char) {

    abstract fun move(cart: Cart)

    override fun toString(): String {
        return symbol.toString()
    }
}

class StraightPath(symbol: Char) : PathSegment(symbol) {

    override fun move(cart: Cart) {
        cart.moveInFacingDirection()
    }
}

class CurvedPath(private val verticalDirection: Direction, private val horizontalDirection: Direction, symbol: Char) : PathSegment(symbol) {

    override fun move(cart: Cart) {
        if (cart.facing.horizontal) {
            cart.facing = verticalDirection
        } else {
            cart.facing = horizontalDirection
        }

        cart.moveInFacingDirection()
    }
}

class Intersection(symbol: Char) : PathSegment(symbol) {

    override fun move(cart: Cart) {
        cart.handleIntersection()
        cart.moveInFacingDirection()
    }
}
