package helper


fun <T> List<List<T>>.countType(type : T): Int {
    return this.sumBy { line -> line.count{it == type} }
}

fun <T> performTicksLooped(iterations: Int, initialState: T, tick: (T) -> T): T {
    var state = initialState
    val seenStates = linkedSetOf(initialState)

    var iteration = 0
    do {
        iteration++
        state = tick(state)
        val isNew = seenStates.add(state)
    } while (isNew && iteration < iterations)

    if (iteration == iterations) {
        return state
    }

    val loopStart = seenStates.indexOf(state)
    val loopSize = seenStates.size - loopStart

    val finalIndex = (iterations - iteration) % loopSize + loopStart

    return seenStates.elementAt(finalIndex)
}