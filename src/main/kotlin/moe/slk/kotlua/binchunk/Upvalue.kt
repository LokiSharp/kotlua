package moe.slk.kotlua.binchunk

/**
 * Upvalue 定义
 */
data class Upvalue(
    val inStack: Byte,
    val idx: Byte
)