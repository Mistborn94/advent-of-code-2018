package day19

import day16.Instruction

class Day19(lines: List<String>) {

    val instructionRegister = lines[0].substring(4).toInt()
    val instructions = lines.subList(1, lines.size).map { Instruction.parseString(it) }

    fun solvePartA(): Int {
        val registers = mutableListOf<Int>(0, 0, 0, 0, 0, 0)

        executeProgram(registers)
        return registers[0]
    }

    fun solvePartB(useShortcut: Boolean = true): Int {
        val registers = mutableListOf<Int>(1, 0, 0, 0, 0, 0)

        executeProgram(registers, useShortcut)
        return registers[0]
    }

    private fun executeProgram(registers: MutableList<Int>, shortcut: Boolean = false) {
        var instructionPointer = registers[instructionRegister]

        while (instructionPointer in instructions.indices) {
            //This part is specifically crafted for my input :(
            //It short circuits the inner loop that increments r4
            if (shortcut && instructionPointer == 3 && registers[4] == 1) {
                registers[1] = 0

                //The condition for r2 == 1L is needed to prevent it being added twice
                if (registers[5] % registers[2] == 0
                    && registers[2] != 1) {
                    registers[0] += registers[2]
                }

                registers[4] = registers[5]

            } else {
                registers[instructionRegister] = instructionPointer
                val instruction = instructions[instructionPointer]
                instruction.executeMutable(registers)


                instructionPointer = registers[instructionRegister].toInt() + 1
            }

        }
    }
}