package moe.slk.kotlua.api

interface LuaState {
    /* basic stack manipulation */
    var top: Int

    fun absIndex(idx: Int): Int
    fun checkStack(n: Int): Boolean
    fun pop(n: Int)
    fun copy(fromIdx: Int, toIdx: Int)
    fun pushValue(idx: Int)
    fun replace(idx: Int)
    fun insert(idx: Int)
    fun remove(idx: Int)
    fun rotate(idx: Int, n: Int)

    /* access functions (stack -> Go); */
    fun typeName(tp: LuaType): String

    fun type(idx: Int): LuaType
    fun isNone(idx: Int): Boolean
    fun isNil(idx: Int): Boolean
    fun isNoneOrNil(idx: Int): Boolean
    fun isBoolean(idx: Int): Boolean
    fun isInteger(idx: Int): Boolean
    fun isNumber(idx: Int): Boolean
    fun isString(idx: Int): Boolean
    fun isTable(idx: Int): Boolean
    fun isThread(idx: Int): Boolean
    fun isFunction(idx: Int): Boolean
    fun toBoolean(idx: Int): Boolean
    fun toInteger(idx: Int): Long
    fun toIntegerX(idx: Int): Long?
    fun toNumber(idx: Int): Double
    fun toNumberX(idx: Int): Double?
    fun toString(idx: Int): String?

    /* push functions (Go -> stack); */
    fun pushNil()

    fun pushBoolean(b: Boolean)
    fun pushInteger(n: Long)
    fun pushNumber(n: Double)
    fun pushString(s: String)
    fun pushGlobalTable()

    fun arith(op: ArithOp)
    fun compare(idx1: Int, idx2: Int, op: CmpOp): Boolean

    /* get functions (Lua -> stack) */
    fun newTable()

    fun createTable(nArr: Int, nRec: Int)
    fun getTable(idx: Int): LuaType
    fun getField(idx: Int, k: String): LuaType
    fun getI(idx: Int, i: Long): LuaType
    fun getGlobal(name: String): LuaType

    /* set functions (stack -> Lua) */
    fun setTable(idx: Int)

    fun setField(idx: Int, k: String)
    fun setI(idx: Int, i: Long)
    fun setGlobal(name: String)

    fun register(name: String, f: KFunction)

    /* miscellaneous functions */
    fun len(idx: Int)

    /* 'load' and 'call' functions (load and run Lua code) */
    fun load(chunk: ByteArray, chunkName: String, mode: String): ThreadStatus

    fun call(nArgs: Int, nResults: Int)

    fun concat(n: Int)

    fun isKFunction(idx: Int): Boolean
    fun toKFunction(idx: Int): KFunction?
    fun pushKFunction(f: KFunction)
}
