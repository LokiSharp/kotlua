package moe.slk.kotlua.binchunk.types

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