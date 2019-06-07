package moe.slk.kotlua.binchunk

import moe.slk.kotlua.api.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun unDump(data: ByteArray): Prototype {
    val buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
    val reader = Reader(buf)

    reader.checkHeader()
    reader.readByte() // size_upvalues
    return reader.readProto()
}

/**
 * 二进制 chunk 解析器
 */
private class Reader(val buf: ByteBuffer) {

    /**
     * 解析二进制 chunk
     *
     * @return 函数原型
     */


    /**
     * 检查二进制 chunk 头部
     */
    fun checkHeader() {
        when {
            !LUA_SIGNATURE.contentEquals(readBytes(4)) -> throw RuntimeException("not a precompiled chunk!")
            buf.get().toInt() != LUAC_VERSION -> throw RuntimeException("version mismatch!")
            buf.get().toInt() != LUAC_FORMAT -> throw RuntimeException("format mismatch!")
            !LUAC_DATA.contentEquals(readBytes(6)) -> throw RuntimeException("corrupted!")
            buf.get().toInt() != CINT_SIZE -> throw RuntimeException("int size mismatch!")
            buf.get().toInt() != CSIZET_SIZE -> throw RuntimeException("size_t size mismatch!")
            buf.get().toInt() != INSTRUCTION_SIZE -> throw RuntimeException("instruction size mismatch!")
            buf.get().toInt() != LUA_INTEGER_SIZE -> throw RuntimeException("lua_Integer size mismatch!")
            buf.get().toInt() != LUA_NUMBER_SIZE -> throw RuntimeException("lua_Number size mismatch!")
            buf.long != LUAC_INT.toLong() -> throw RuntimeException("endianness mismatch!")
            buf.double != LUAC_NUM -> throw RuntimeException("float format mismatch!")
        }
    }

    /**
     * 从字节流中读取字符串
     * @return 字符串
     */
    fun readString(): String {
        var size = buf.get().toInt()

        if (size == 0) {
            return ""
        }

        if (size == 0xFF) {
            size = buf.long.toInt()
        }

        return String(readBytes(size - 1))
    }

    /**
     * 从字节流中读取一个字节
     * @return 一个字节
     */
    fun readByte() = buf.get()

    /**
     * 从字节流中读取 n 个字节
     * @param n 想要提取的字节数
     * @return n 个字节
     */
    fun readBytes(n: Int): ByteArray {
        val a = ByteArray(n)
        buf.get(a)
        return a
    }

    /**
     * 从字节流中读取函数原型
     *
     * @param parentSource 父函数的源文件名
     */
    fun readProto(parentSource: String = ""): Prototype {
        var source = readString()
        if (source.isEmpty()) {
            source = parentSource
        }

        return Prototype(
            source = source,
            lineDefined = buf.int,
            lastLineDefined = buf.int,
            numParams = buf.get(),
            isVararg = buf.get(),
            maxStackSize = buf.get(),
            code = readCode(),
            constants = readConstants(),
            upvalues = readUpvalues(),
            protos = readProtos(),
            lineInfo = readLineInfo(),
            localVars = readLocVars(),
            upvalueNames = readUpvalueNames()
        )
    }

    /**
     * 从字节流中读取指令表
     */
    fun readCode(): List<Int> {
        val length = buf.int
        val code = ArrayList<Int>(length)

        repeat(length) {
            code.add(buf.int)
        }

        return code
    }

    /**
     * 从字节流中读取常量表
     */
    fun readConstants(): List<Any?> {
        val length = buf.int
        val constants = ArrayList<Any?>(length)

        repeat(length) {
            constants.add(readConstant())
        }

        return constants
    }

    /**
     * 从字节流中读取常量
     */
    fun readConstant(): Any? {
        return when (buf.get().toInt()) {
            TAG_NIL -> null
            TAG_BOOLEAN -> buf.get().toInt() != 0
            TAG_INTEGER -> buf.long
            TAG_NUMBER -> buf.double
            TAG_SHORT_STR, TAG_LONG_STR -> readString()
            else -> throw RuntimeException("corrupted!")
        }
    }


    /**
     * 从字节流中读取 Upvalue 表
     */
    fun readUpvalues(): List<Upvalue> {
        val length = buf.int
        val upvalues = ArrayList<Upvalue>(length)

        repeat(length) {
            upvalues.add(
                Upvalue(
                    inStack = buf.get(),
                    idx = buf.get()
                )
            )
        }

        return upvalues
    }

    /**
     * 从字节流中读取函数原型表
     */
    fun readProtos(parentSource: String = ""): List<Prototype> {
        val length = buf.int
        val protos = ArrayList<Prototype>(length)

        repeat(length) {
            protos.add(readProto(parentSource))
        }

        return protos
    }

    /**
     * 从字节流中读取行号表
     */
    fun readLineInfo(): List<Int> {
        val size = buf.int
        val lineInfo = ArrayList<Int>()

        repeat(size) {
            lineInfo.add(buf.int)
        }

        return lineInfo
    }

    /**
     * 从字节流中读取局部变量表
     */
    fun readLocVars(): List<LocVar> {
        val size = buf.int
        val locVars = ArrayList<LocVar>(size)

        repeat(size) {
            locVars.add(
                LocVar(
                    varName = readString(),
                    startPC = buf.int,
                    endPC = buf.int
                )
            )
        }

        return locVars
    }

    /**
     * 从字节流中读取 Upvalue 名表
     */
    fun readUpvalueNames(): List<String> {
        val size = buf.int
        val upvalueNames = ArrayList<String>(size)

        repeat(size) {
            upvalueNames.add(readString())
        }

        return upvalueNames
    }
}

