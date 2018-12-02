package helper

import java.io.File

fun readInput(day: Int) = File("src/day$day/input.txt")
fun readSampleInput(day: Int, id: Int = 1) = File("src/day$day/sample_input_$id.txt")