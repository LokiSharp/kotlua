package moe.slk.kotlua.binchunk

val LUA_SIGNATURE = byteArrayOf(0x1b, 'L'.toByte(), 'u'.toByte(), 'a'.toByte())
const val LUAC_VERSION = 0x53
const val LUAC_FORMAT = 0
val LUAC_DATA = byteArrayOf(0x19, 0x93.toByte(), '\r'.toByte(), '\n'.toByte(), 0x1a, '\n'.toByte())
const val CINT_SIZE = 4
const val CSIZET_SIZE = 8
const val INSTRUCTION_SIZE = 4
const val LUA_INTEGER_SIZE = 8
const val LUA_NUMBER_SIZE = 8
const val LUAC_INT = 0x5678
const val LUAC_NUM = 370.5

const val TAG_NIL = 0x00
const val TAG_BOOLEAN = 0x01
const val TAG_NUMBER = 0x03
const val TAG_INTEGER = 0x13
const val TAG_SHORT_STR = 0x04
const val TAG_LONG_STR = 0x14