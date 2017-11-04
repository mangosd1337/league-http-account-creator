package com.accountcreator.anticaptchaapi

import com.accountcreator.RECAPTCHA_SITE_KEY
import com.accountcreator.RECAPTCHA_URL
import com.accountcreator.getOkHttpClient
import com.google.gson.JsonParser
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import io.reactivex.Single


private const val CREATE_TASK_URL = "https://api.anti-captcha.com/createTask"
private const val GET_TASK_RESULT_URL = "https://api.anti-captcha.com/getTaskResult"

//region JSON format strings

private const val CREATE_TASK_JSON_FORMAT = """
    {
    "clientKey":"%s",
    "task":
        {
            "type":"NoCaptchaTaskProxyless",
            "websiteURL":"%s",
            "websiteKey":"%s"
        },
    "softId":0,
    "languagePool":"en"
}
"""

private const val GET_TASK_RESULT_JSON_FORMAT = """
{
    "clientKey":"%s",
    "taskId": %s
}
"""
//endregion

/**
 * @return A Single that returns a ReCaptcha token.
 */
fun solveCaptcha(apiKey: String): Single<String> {
    return Single.fromCallable {

        var response = getOkHttpClient().newCall(Request.Builder()
                .url(CREATE_TASK_URL)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        CREATE_TASK_JSON_FORMAT.format(apiKey, RECAPTCHA_URL, RECAPTCHA_SITE_KEY)))
                .build()
        ).execute()

        // Extract task ID from response
        var json = JsonParser().parse(response.body().string()).asJsonObject
        var errorId = json.get("errorId").asInt
        if (errorId != 0) {
            throw APIError("CreateTask", errorId)
        } else {
            val taskId = json.get("taskId").asString

            // Now periodically check whether task finished
            val checkDelay = 2000L
            val ret: String

            do {
                Thread.sleep(checkDelay)

                response = getOkHttpClient().newCall(Request.Builder()
                        .url(GET_TASK_RESULT_URL)
                        .post(RequestBody.create(MediaType.parse("application/json"),
                                GET_TASK_RESULT_JSON_FORMAT.format(apiKey, taskId)))
                        .build()
                ).execute()

                json = JsonParser().parse(response.body().string()).asJsonObject
                errorId = json.get("errorId").asInt
                if (errorId != 0) {
                    throw APIError("GetTaskResult", errorId)
                } else {
                    val status = json.get("status").asString
                    if (status == "ready") {
                        // Nice, let's get solution.
                        ret = json.getAsJsonObject("solution").get("gRecaptchaResponse").asString
                        break
                    }
                }
            } while (true)
            ret
        }
    }
}

