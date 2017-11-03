package com.accountcreator.anticaptchaapi

class APIError(private val endpoint: String, private val errorId: Int) : RuntimeException() {
    override val message: String?
        get() = "$endpoint returned errorID $errorId."
}