package com.accountcreator

import com.accountcreator.anticaptchaapi.solveCaptcha
import com.accountcreator.riotapi.createAccount
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

const val RECAPTCHA_URL = "https://signup.leagueoflegends.com/en/signup/index"
const val RECAPTCHA_SITE_KEY = "6Lc3HAsUAAAAACsN7CgY9MMVxo2M09n_e4heJEiZ"


fun main(args: Array<String>) {
    println("Zack's account creator version 0.1\n\n")

    val settings = promptSettings()

    var counter = 0
    var finishedCounter = 0
    val disposables = mutableListOf<Disposable>()

    do {
        println("Starting account ${++counter}.")
        val credentials = randomCredentials()
        val disposable = solveCaptcha(settings.anticaptchaKey)
                .flatMap { createAccount(credentials, settings, it) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe({
                    println("Account $it ${credentials.name} created.")
                    writeToAccountsFile(credentials, settings.fileName)
                    println("${++finishedCounter} of ${settings.numberOfAccounts} done.")
                }, {
                    println(it)
                })
        disposables.add(disposable)
        Thread.sleep((settings.delay * 1000).toLong())
    } while (counter < settings.numberOfAccounts)

    while (disposables.any { !it.isDisposed }) {
        Thread.sleep(100)
    }
}

fun promptSettings(): Settings {
    println("EUW OR NA:")
    print("> ")

    var region = ""

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
    input = readLine()
    val fileName = if (input.isNullOrBlank()) "accounts.txt" else input!!

    println("Anticaptcha API key: ")
    print("> ")
    input = readLine()
    val key: String = input!!

    var number: Int? = null
    println("How many accounts do you want to create? (0-100):")
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

    println("Delay between accounts in [s], does not wait for accounts to finish, immediately launches next after X (>= 0.5) seconds: ")
    print(">")
    var delay = 0.0f
    do {
        input = readLine()
        if (input != null) {
            delay = input.toFloatOrNull() ?: 0.0f
        }
    } while (delay < 0.5f)

    println()
    println("Settings:\nRegion: $region\nFilename: $fileName\nAnticaptchaKey: $key\nDelay: $delay seconds\nNumber of accounts: $number\n\nIs that correct? (y/n)")
    print("> ")

    input = readLine()

    return if (input != null && input.toLowerCase() == "y") {
        Settings(region, fileName, key, number, delay)
    } else {
        // Just prompt again.
        promptSettings()
    }
}

data class Settings(val region: String, val fileName: String, val anticaptchaKey: String, val numberOfAccounts: Int, val delay: Float)