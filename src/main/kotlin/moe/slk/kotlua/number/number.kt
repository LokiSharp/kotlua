package moe.slk.kotlua.number

/**
 * 判断浮点数是否为整数
 * @param  f 待判断的浮点数
 * @return 布尔值
 */
fun isInteger(f: Double): Boolean {
    return f == f.toLong().toDouble()
}

/**
 * 将字符串转换为整型
 * @param  str 待转换的字符串
 * @return 整型
 */
fun parseInteger(str: String): Long? {
    return try {
        str.toLong()
    } catch (e: NumberFormatException) {
        null
    }

}

/**
 * 将字符串转换为浮点型
 * @param  str 待转换的字符串
 * @return 浮点型
 */
fun parseFloat(str: String): Double? {
    return try {
        str.toDouble()
    } catch (e: NumberFormatException) {
        null
    }
}