package helper

fun <T> Iterable<Iterable<T>>.countType(type: T): Int {
    return this.sumBy { line -> line.count { it == type } }
}

fun <T> MutableCollection<T>.removeFirst(): T {
    val item = first()
    remove(item)
    return item
}

fun <T> MutableCollection<T>.removeLast(): T {
    val item = last()
    remove(item)
    return item
}

inline fun <T1, T2, R> Iterable<T1>.combineAll(other: Iterable<T2>, transform: (T1, T2) -> R): List<R> =
    flatMap { a ->
        other.map { b -> transform(a, b) }
    }

fun <T> Iterable<Iterable<T>>.combineAll(other: Iterable<T>): List<List<T>> =
    flatMap { a ->
        other.map { b -> a + b }
    }

fun <T1, T2> Iterable<T1>.combineAllToPair(other: Iterable<T2>): List<Pair<T1, T2>> =
    flatMap { a ->
        other.map { b -> Pair(a, b) }
    }

fun <T> combineAll(vararg iterable: Iterable<T>): List<List<T>> = iterable.fold(listOf(emptyList()))
{ acc, value ->
    acc.combineAll(value)
}
