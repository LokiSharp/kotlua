package moe.slk.kotlua.vm.types

enum class OpArgMask {
    OpArgN, // argument is not used
    OpArgU, // argument is used
    OpArgR, // argument is a register or a jump offset
    OpArgK // argument is a constant or register/constant
}