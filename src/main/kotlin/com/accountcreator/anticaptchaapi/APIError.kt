package com.accountcreator.anticaptchaapi

class APIError(private val endpoint: String, private val errorId: Int)
    : RuntimeException("$endpoint returned errorID $errorId.")