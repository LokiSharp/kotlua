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
        getByte(buf) // 跳过 Upvalue
        val proto = Prototype()
        proto.read(buf)
        return proto // 返回函数原型
    }

    /**
     * 检查二进制 chunk 头部
     */
    private fun checkHeader(buf: ByteBuffer) {
        when {
            !LUA_SIGNATURE.contentEquals(getBytes(buf, 4)) -> throw RuntimeException("not a precompiled chunk!")
            buf.get().toInt() != LUAC_VERSION -> throw RuntimeException("version mismatch!")
            buf.get().toInt() != LUAC_FORMAT -> throw RuntimeException("format mismatch!")
            !LUAC_DATA.contentEquals(getBytes(buf, 6)) -> throw RuntimeException("corrupted!")
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
    fun getString(buf: ByteBuffer): String {
        var size = buf.get().toInt()

        if (size == 0) {
            return ""
        }

        if (size == 0xFF) {
            size = buf.long.toInt()
        }

        return String(getBytes(buf, size - 1))
    }

    /**
     * 从字节流中读取一个字节
     * @return 一个字节
     */
    fun getByte(buf: ByteBuffer) = buf.get()

    /**
     * 从字节流中读取 n 个字节
     * @param n 想要提取的字节数
     * @return n 个字节
     */
    fun getBytes(buf: ByteBuffer, n: Int): ByteArray {
        val a = ByteArray(n)
        buf.get(a)
        return a
    }
}

