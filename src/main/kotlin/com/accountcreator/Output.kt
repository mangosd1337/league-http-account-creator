package com.accountcreator

import java.nio.file.Paths

fun writeToAccountsFile(credentials: Credentials, fileName: String) {
    Paths.get(fileName).toFile().appendText("${credentials.name}:${credentials.password}\n")
}
