package moe.slk.kotlua.api

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