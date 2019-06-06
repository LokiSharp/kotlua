package moe.slk.kotlua.binchunk

/**
 * 二进制 chunk 头定义
 *
 * 头部总共占用约 30 个字节（因平台而异），其中包含签名、版本号、格式号、各种整数类型占用的字节数，以及大小端和浮点数格
 * 式识别信息等。
 *
 * @param signature 用于快速识别文件格式
 * @param version 用于检验 chunk 文件版本是否与虚拟机匹配
 * @param format 用于检验 chunk 文件格式是否与虚拟机匹配
 * @param luacData 二次校验
 * @param cintSize cint 类型宽度
 * @param sizetSize size_t 类型宽度
 * @param instructionSize 虚拟机指令宽度
 * @param luaIntegerSize 整数类型宽度
 * @param luaNumberSize 浮点数类型宽度
 * @param luacInt 用于检测二进制 chunk 的大小端方式
 * @param luacNum 用于检测二进制 chunk 的浮点数格式
 */
data class Header(
    val signature: ByteArray,
    val version: Byte,
    val format: Byte,
    val luacData: ByteArray,
    val cintSize: Byte,
    val sizetSize: Byte,
    val instructionSize: Byte,
    val luaIntegerSize: Byte,
    val luaNumberSize: Byte,
    val luacInt: Long,
    val luacNum: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Header

        if (!signature.contentEquals(other.signature)) return false
        if (version != other.version) return false
        if (format != other.format) return false
        if (!luacData.contentEquals(other.luacData)) return false
        if (cintSize != other.cintSize) return false
        if (sizetSize != other.sizetSize) return false
        if (instructionSize != other.instructionSize) return false
        if (luaIntegerSize != other.luaIntegerSize) return false
        if (luaNumberSize != other.luaNumberSize) return false
        if (luacInt != other.luacInt) return false
        if (luacNum != other.luacNum) return false

        return true
    }

    override fun hashCode(): Int {
        var result = signature.contentHashCode()
        result = 31 * result + version
        result = 31 * result + format
        result = 31 * result + luacData.contentHashCode()
        result = 31 * result + cintSize
        result = 31 * result + sizetSize
        result = 31 * result + instructionSize
        result = 31 * result + luaIntegerSize
        result = 31 * result + luaNumberSize
        result = 31 * result + luacInt.hashCode()
        result = 31 * result + luacNum.hashCode()
        return result
    }
}