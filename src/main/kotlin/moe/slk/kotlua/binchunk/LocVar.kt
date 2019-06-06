package moe.slk.kotlua.binchunk

/**
 * 局部变量定义
 */
data class LocVar(
    val varName: String,
    val startPC: Int,
    val endPC: Int
)