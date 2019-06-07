package moe.slk.kotlua.api

interface LuaVM : LuaState {
    fun fetch(): Int
    fun addPC(n: Int)
    fun getConst(idx: Int)
    fun getRK(rk: Int)
    fun registerCount(): Int
    fun loadVararg(n: Int)
    fun loadProto(idx: Int)
}