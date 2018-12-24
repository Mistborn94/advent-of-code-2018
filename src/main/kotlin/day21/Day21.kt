package day21

import day16.Instruction
import day19.TimeTravelCpu
import kotlin.math.max

class Day21(lines: List<String>) {

    private val instructionRegister = lines[0].substring(4).toInt()
    private val instructions = lines.subList(1, lines.size).map { Instruction.parseString(it) }

    fun partA(endInstruction: Int = 28): Int {
        val registers = mutableListOf(0, 0, 0, 0, 0, 0)
        val program = TimeTravelCpu(instructionRegister, instructions, registers)

        while (program.instructionPointer != endInstruction) {
            program.tick()
        }

        return registers[1]
    }

    fun partB(endInstruction: Int = 28): Int {
        val comparisonRegister = max(instructions[endInstruction].a,  instructions[endInstruction].b)

        val registers = mutableListOf(0, 0, 0, 0, 0, 0)
        val program = TimeTravelCpu(instructionRegister, instructions, registers)
        val comparedValues = linkedSetOf<Int>()

        do {
            while (program.instructionPointer != endInstruction) {
                program.tick()
            }

            val comparedValue = registers[comparisonRegister]
            program.tick()

        } while (comparedValues.add(comparedValue))

        return comparedValues.last()
    }
}