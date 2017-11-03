package com.accountcreator

import com.accountcreator.anticaptchaapi.solveCaptcha
import com.accountcreator.riotapi.createAccount

const val RECAPTCHA_URL = "https://signup.leagueoflegends.com/en/signup/index"
const val RECAPTCHA_SITE_KEY = "6Lc3HAsUAAAAACsN7CgY9MMVxo2M09n_e4heJEiZ"


fun main(args: Array<String>) {
    val settings = promptSettings()

    var counter = 0

    do {
        val credentials = randomCredentials()
        solveCaptcha(settings.anticaptchaKey)
                .flatMap { createAccount(credentials, settings, it) }
                .toObservable()
                .blockingSubscribe({
                    counter++
                    println("Account $it ${credentials.name} created.")
                    writeToAccountsFile(credentials, settings.fileName)
                    println("$counter of ${settings.numberOfAccounts} done.")
                }, {
                    println(it)
                })
    } while (counter < settings.numberOfAccounts)
}

fun promptSettings(): Settings {
    println("EUW OR NA:")
    print("> ")

    var region: String = ""

    var input: String?
    do {
        input = readLine()?.toLowerCase()
        if (input == "euw") {
            region = "EUW1"
        } else if (input == "na") {
            region = "NA1"
        }
    } while (input == null || (input != "euw" && input != "na"))

    println("Filename (leave empty for accounts.txt):")
    print("> ")
    input = readLine()?.trim()
    val fileName = input ?: "accounts.txt"

    println("Anticaptcha API key: ")
    print("> ")
    input = readLine()
    val key: String = input!!

    var number: Int? = null
    println("How many accounts do you want to create? (0-100)")
    do {
        input = readLine()


        try {
            number = input?.toInt()
            if (number != null && (number < 1 || number > 100)) {
                number = null
            }
        } catch (ex: NumberFormatException) {

        }
    } while (number == null)

    println()
    println("Settings: Region $region Filename $fileName AnticaptchaKey $key Number $number, is that correct? (y/n)")
    print("> ")

    input = readLine()

    return if (input != null && input.toLowerCase() == "y") {
        Settings(region, fileName, key, number)
    } else {
        // Just prompt again.
        promptSettings()
    }
}

data class Settings(val region: String, val fileName: String, val anticaptchaKey: String, val numberOfAccounts: Int)