package moe.slk.kotlua.api

enum class ThreadStatus {
    LUA_OK,
    LUA_YIELD,
    LUA_ERRRUN,
    LUA_ERRSYNTAX,
    LUA_ERRMEM,
    LUA_ERRGCMM,
    LUA_ERRERR,
    LUA_ERRFILE
}