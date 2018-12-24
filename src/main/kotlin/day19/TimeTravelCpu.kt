package day19

import day16.Instruction

class TimeTravelCpu(
    private val instructionRegister: Int,
    private val instructions: List<Instruction>,
    private val registers: MutableList<Int>
) {

    var instructionPointer: Int = registers[instructionRegister]
        private set

    val running get() = instructionPointer in instructions.indices
    var instructionCount = 0

    fun tick() {
        registers[instructionRegister] = instructionPointer
        instructionCount++

        val instruction = instructions[instructionPointer]
        instruction.executeMutable(registers)

        instructionPointer = registers[instructionRegister] + 1
    }
}