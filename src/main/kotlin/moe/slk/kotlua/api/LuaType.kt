package moe.slk.kotlua.api

enum class LuaType {
    LUA_TNIL,
    LUA_TBOOLEAN,
    LUA_TLIGHTUSERDATA,
    LUA_TNUMBER,
    LUA_TSTRING,
    LUA_TTABLE,
    LUA_TFUNCTION,
    LUA_TUSERDATA,
    LUA_TTHREAD,
    LUA_TNONE, // -1
    ;
}

enum class ArithOp {
    LUA_OPADD, // +
    LUA_OPSUB, // -
    LUA_OPMUL, // *
    LUA_OPMOD, // %
    LUA_OPPOW, // ^
    LUA_OPDIV, // /
    LUA_OPIDIV, // //
    LUA_OPBAND, // &
    LUA_OPBOR, // |
    LUA_OPBXOR, // ~
    LUA_OPSHL, // <<
    LUA_OPSHR, // >>
    LUA_OPUNM, // -
    LUA_OPBNOT, // ~
    ;
}

enum class CmpOp {
    LUA_OPEQ, // ==
    LUA_OPLT, // <
    LUA_OPLE, // <=
    ;

}