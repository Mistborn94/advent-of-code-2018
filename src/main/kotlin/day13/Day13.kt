package day13

class Map(val tracks: List<List<PathSegment?>>, val carts: MutableList<Triple<Int, Int, Direction>>, val log: Boolean = false) {

    private val cartComparator = Comparator.comparing { c: Cart -> c.y }
        .thenComparing { c: Cart -> c.x }

    fun findFirstCrashLocation(): Pair<Int, Int> {
        return move().firstCollision
    }

    fun findLastCartLocation(): Pair<Int, Int>? {
        return move().lastCart
    }

    private fun move(): Status {
        var iteration = 0
        var firstCollision: Pair<Int, Int>? = null
        val sortedCarts = carts.map { Cart(it.first, it.second, it.third) }.sortedWith(cartComparator).toMutableList()

        while (sortedCarts.count { !it.crashed } > 1) {
            printMap(iteration, sortedCarts)
            for (cart in sortedCarts) {
                if (!cart.crashed) {
                    val track =
                        tracks[cart.y][cart.x] ?: throw IllegalStateException("No track at ${cart.x}, ${cart.y}")

                    track.move(cart)
                    val colliding = findCollisions(sortedCarts, cart)
                    val crashed = colliding.isNotEmpty()

                    if (crashed) {
                        firstCollision = firstCollision ?: cart.position
                        cart.crashed = true
                        sortedCarts.filter { it.position == cart.position }.forEach { it.crashed = true }
                    }
                }
            }

            sortedCarts.sortWith(cartComparator)
            iteration++
        }

        printMap(iteration, sortedCarts)

        return Status(firstCollision!!, sortedCarts.firstOrNull { !it.crashed }?.position)
    }

    private fun findCollisions(
        sortedCarts: MutableList<Cart>,
        cart: Cart
    ): List<Cart> {
        val collisions = sortedCarts.filter { it.position == cart.position && !it.crashed }

        return if (collisions.size > 1) collisions else emptyList()
    }

    fun printMap(iteration: Int, carts: List<Cart>) {
        if (log) {
            val chars = tracks.map {
                it.map { it?.symbol ?: ' ' }.toMutableList()
            }.toMutableList()

            for (cart in carts.filter { !it.crashed }) {
                chars[cart.y][cart.x] = cart.facing.symbol
            }

            println("Iteration $iteration")
            chars.forEach { println(it.joinToString("")) }
            println()
        }
    }

    companion object {
        fun build(lines: List<String>): Map {
            val carts = mutableListOf<Triple<Int, Int, Direction>>()

            val path = lines.mapIndexed { y, line ->
                line.mapIndexed { x, char ->
                    pathSegment(char, x, y, carts)
                }

            }

            return Map(path, carts)
        }

        private fun pathSegment(
            char: Char, x: Int, y: Int, carts: MutableList<Triple<Int, Int, Direction>>
        ): PathSegment? {
            return when (char) {
                ' ' -> null
                '-', '|' -> StraightPath(char)
                '+' -> Intersection()
                '/' -> CurvedTopRight()
                '\\' -> CurvedTopLeft()
                '>' -> {
                    carts.add(Triple(x, y, Direction.RIGHT)); StraightPath('-')
                }
                '<' -> {
                    carts.add(Triple(x, y, Direction.LEFT)); StraightPath('-')
                }
                '^' -> {
                    carts.add(Triple(x, y, Direction.UP)); StraightPath('|')
                }
                'v' -> {
                    carts.add(Triple(x, y, Direction.DOWN)); StraightPath('|')
                }

                else -> throw IllegalArgumentException("Unknown Character $char")
            }
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
    var intersections = 0
    var crashed = false

    val position: Pair<Int, Int>
        get() = Pair(x, y)

    fun moveInFacingDirection() {
        x = facing.moveX(x)
        y = facing.moveY(y)
    }

    fun handleIntersection() {
        facing = when (intersections) {
            0 -> facing.turnLeft()
            1 -> facing
            2 -> facing.turnRight()
            else -> throw Exception()
        }

        intersections = (intersections + 1) % 3
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

class CurvedTopLeft : PathSegment('\'') {

    override fun move(cart: Cart) {
        if (cart.facing.horizontal) {
            cart.facing = cart.facing.turnRight()
        } else {
            cart.facing = cart.facing.turnLeft()
        }

        cart.moveInFacingDirection()
    }
}

class CurvedTopRight : PathSegment('/') {

    override fun move(cart: Cart) {
        if (cart.facing.horizontal) {
            cart.facing = cart.facing.turnLeft()
        } else {
            cart.facing = cart.facing.turnRight()
        }

        cart.moveInFacingDirection()
    }
}

class Intersection : PathSegment('+') {

    override fun move(cart: Cart) {
        cart.handleIntersection()
        cart.moveInFacingDirection()
    }
}

data class Status(val firstCollision: Pair<Int, Int>, val lastCart: Pair<Int, Int>?)
