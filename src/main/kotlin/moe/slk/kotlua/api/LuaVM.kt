package moe.slk.kotlua.api

interface LuaVM : LuaState {
    fun getPC(): Int
    fun addPC(n: Int)
    fun fetch(): Int
    fun getConst(idx: Int)
    fun getRK(rk: Int)
}