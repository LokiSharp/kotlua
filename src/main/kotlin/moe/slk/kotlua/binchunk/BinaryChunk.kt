package moe.slk.kotlua.binchunk

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 二进制 chunk 解析器
 */
object BinaryChunk {

    /**
     * 解析二进制 chunk
     *
     * @return 函数原型
     */
    fun unDump(data: ByteArray): Prototype {
        val buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        checkHeader(buf) // 校验函数头
        readByte(buf) // 跳过 Upvalue
        return readProto(buf) // 返回函数原型
    }

    /**
     * 检查二进制 chunk 头部
     */
    private fun checkHeader(buf: ByteBuffer) {
        when {
            !LUA_SIGNATURE.contentEquals(readBytes(buf, 4)) -> throw RuntimeException("not a precompiled chunk!")
            buf.get().toInt() != LUAC_VERSION -> throw RuntimeException("version mismatch!")
            buf.get().toInt() != LUAC_FORMAT -> throw RuntimeException("format mismatch!")
            !LUAC_DATA.contentEquals(readBytes(buf, 6)) -> throw RuntimeException("corrupted!")
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
     * 从字节流中读取函数原型
     */
    private fun readProto(buf: ByteBuffer, parentSource: String = ""): Prototype {
        var source = readString(buf)
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
            code = readCode(buf),
            constants = readConstants(buf),
            upvalues = readUpvalues(buf),
            protos = readProtos(buf),
            lineInfo = readLineInfo(buf),
            localVars = readLocVars(buf),
            upvalueNames = readUpvalueNames(buf)
        )
    }

    /**
     * 从字节流中读取指令表
     */
    private fun readCode(buf: ByteBuffer): List<Int> {
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
    private fun readConstants(buf: ByteBuffer): List<Any?> {
        val length = buf.int
        val constants = ArrayList<Any?>(length)

        repeat(length) {
            constants.add(readConstant(buf))
        }

        return constants
    }

    /**
     * 从字节流中读取常量
     */
    private fun readConstant(buf: ByteBuffer): Any? {
        return when (buf.get().toInt()) {
            TAG_NIL -> null
            TAG_BOOLEAN -> buf.get().toInt() != 0
            TAG_INTEGER -> buf.long
            TAG_NUMBER -> buf.double
            TAG_SHORT_STR, TAG_LONG_STR -> readString(buf)
            else -> throw RuntimeException("corrupted!")
        }
    }

    /**
     * 从字节流中读取字符串
     * @return 字符串
     */
    private fun readString(buf: ByteBuffer): String {
        var size = buf.get().toInt()

        if (size == 0) {
            return ""
        }

        if (size == 0xFF) {
            size = buf.long.toInt()
        }

        return String(readBytes(buf, size - 1))
    }

    /**
     * 从字节流中读取 Upvalue 表
     */
    private fun readUpvalues(buf: ByteBuffer): List<Upvalue> {
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
    private fun readProtos(buf: ByteBuffer, parentSource: String = ""): List<Prototype> {
        val length = buf.int
        val protos = ArrayList<Prototype>(length)

        repeat(length) {
            protos.add(readProto(buf, parentSource))
        }

        return protos
    }

    /**
     * 从字节流中读取行号表
     */
    private fun readLineInfo(buf: ByteBuffer): List<Int> {
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
    private fun readLocVars(buf: ByteBuffer): List<LocVar> {
        val size = buf.int
        val locVars = ArrayList<LocVar>(size)

        repeat(size) {
            locVars.add(
                LocVar(
                    varName = readString(buf),
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
    private fun readUpvalueNames(buf: ByteBuffer): List<String> {
        val size = buf.int
        val upvalueNames = ArrayList<String>(size)

        repeat(size) {
            upvalueNames.add(readString(buf))
        }

        return upvalueNames
    }

    /**
     * 从字节流中读取一个字节
     * @return 一个字节
     */
    private fun readByte(buf: ByteBuffer) = buf.get()

    /**
     * 从字节流中读取 n 个字节
     * @param n 想要提取的字节数
     * @return n 个字节
     */
    private fun readBytes(buf: ByteBuffer, n: Int): ByteArray {
        val a = ByteArray(n)
        buf.get(a)
        return a
    }
}

