package moe.slk.kotlua.vm

const val MAXARG_Bx = (1 shl 18) - 1   // 262143
const val MAXARG_sBx = MAXARG_Bx shr 1 // 131071
const val LFIELDS_PER_FLUSH = 50