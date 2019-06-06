package moe.slk.kotlua.vm

/**
 * 指令码操作数定义
 */
enum class OpArgMask {
    OpArgN, // argument is not used
    OpArgU, // argument is used
    OpArgR, // argument is a register or a jump offset
    OpArgK // argument is a constant or register/constant
}