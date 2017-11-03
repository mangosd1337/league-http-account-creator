package com.accountcreator

import java.io.File

data class Credentials(val name: String, val password: String, val email: String, val dateOfBirth: String)

fun randomCredentials(suffixLen: Int = 2, passwordMinLen: Int = 11, passwordMaxLen: Int = 15): Credentials {
    val names = File("src/main/resources/firstnames.txt").readLines()
    val adjectives = File("src/main/resources/adjectives.txt").readLines()
    val letters = ('a'..'z') + ('A'..'Z')
    val numbers = 0..9
    val special = "!§$%&/()=?+#-"

    val name = (adjectives.takeRandomStr() + names.takeRandomStr() + numbers.takeRandomStr() + letters.takeRandomStr(suffixLen))
            .replace("\\s".toRegex(), "")

    val password = (letters + numbers + special).takeRandomStr(
            (Math.random() * (passwordMaxLen - passwordMinLen + 1) + passwordMinLen).toInt()) + "6"

    val email = (letters + numbers).takeRandomStr(7) + "@" + (letters + numbers).takeRandomStr(12) + ".com"
    val year = (Math.random() * 15).toInt() + 1980
    val month = (Math.random() * 11).toInt() + 1
    val day = (Math.random() * 29).toInt() + 1

    return Credentials(name, password, email, "$year-$month-$day")
}

fun <T> Iterable<T>.takeRandom(n: Int = 1): List<T> {
    val list = toList()

    var i = n

    val ret = mutableListOf<T>()

    while (i-- > 0) {
        ret += list[(Math.random() * list.size).toInt()]
    }

    return ret
}

fun <T> Iterable<T>.takeRandomStr(n: Int = 1): String = takeRandom(n).joinToString("")
