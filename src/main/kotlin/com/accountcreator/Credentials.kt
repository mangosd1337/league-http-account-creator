package com.accountcreator

import java.io.File
import java.security.SecureRandom

data class Credentials(val name: String, val password: String, val email: String, val dateOfBirth: String)
val random = SecureRandom()

fun randomCredentials(suffixLen: Int = 2, passwordMinLen: Int = 11, passwordMaxLen: Int = 15): Credentials {


    val names = File("resources/firstnames.txt").readLines()
    val adjectives = File("resources/adjectives.txt").readLines()

    val letters = "abcdefghijklmnoprstuvwxyzABCDEFGHIJKLMNOPRSTUVXYZ"
    val numbers = "0123456789"
    val special = "!§$%&()=?"


    val name = (adjectives.takeRandomStr() + names.takeRandomStr() + letters.takeRandom(suffixLen))
            .replace("\\s".toRegex(), "")


    var password = ""
    while (password.none { it.isDigit() }) {
        val passwordLength = (random.nextDouble() * (passwordMaxLen - passwordMinLen + 1)).toInt() + passwordMinLen
        password = (letters + numbers + special).takeRandom(passwordLength)
    }

    val email = names.takeRandomStr().replace("\\s".toRegex(), "") +
            "@" + letters.takeRandom((random.nextDouble() * 5).toInt() + 7) + ".com"

    val year = (random.nextDouble() * 15).toInt() + 1980
    val month = (random.nextDouble() * 11).toInt() + 1
    val day = (random.nextDouble() * 29).toInt() + 1

    val birthDate = String.format("%4d-%02d-%02d", year, month, day)

    return Credentials(name, password, email, birthDate)
}

fun String.takeRandom(n: Int): String {
    var ret = ""
    for (i in 1..n) {
        ret += this.takeRandom()
    }
    return ret
}

fun String.takeRandom(): Char {
    return this[(random.nextDouble() * this.length).toInt()]
}


fun <T> Iterable<T>.takeRandom(n: Int = 1): List<T> {
    val list = toList()

    var i = n

    val ret = mutableListOf<T>()

    while (i-- > 0) {
        ret += list[(random.nextDouble() * list.size).toInt()]
    }

    return ret
}

fun <T> Iterable<T>.takeRandomStr(n: Int = 1): String = takeRandom(n).joinToString("")
