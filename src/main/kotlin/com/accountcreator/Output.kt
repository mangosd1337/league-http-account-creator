package com.accountcreator

import java.io.File

fun writeToAccountsFile(credentials: Credentials, fileName: String) {
    File(fileName).appendText("${credentials.name}:${credentials.password}\n")
}
