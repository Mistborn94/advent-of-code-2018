package day17

enum class Cell(val symbol: Char) {
    CLAY('#'),
    SAND('.'),
    SPRING('+'),
    FLOWING_WATER('|'),
    SETTLED_WATER('~')

}