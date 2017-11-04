package com.accountcreator.riotapi

import com.accountcreator.Credentials
import com.accountcreator.Settings
import com.accountcreator.getOkHttpClient
import com.google.gson.JsonParser
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import io.reactivex.Single

private const val CREATE_ACC_URL = "https://signup-api.leagueoflegends.com/v1/accounts/"
private const val CREATE_ACC_JSON_FORMAT = """
{
  "email": "%s",
  "username": "%s",
  "password": "%s",
  "date_of_birth": "%s",
  "region": "%s",
  "tou_agree": true,
  "newsletter": false,
  "locale": "en",
  "token": "Captcha %s"
}
"""

fun createAccount(credentials: Credentials, settings: Settings, captchaToken: String): Single<String> {
    return Single.fromCallable {
        val requestStr = CREATE_ACC_JSON_FORMAT.format(credentials.email, credentials.name, credentials.password,
                credentials.dateOfBirth, settings.region, captchaToken)

        val requestBody = RequestBody.create(MediaType.parse("application/json"), requestStr)

        val response = getOkHttpClient().newCall(Request.Builder()
                .url(CREATE_ACC_URL)
                .header("origin", "https://signup.euw.leagueoflegends.com")
                .header("referer", "https://signup.euw.leagueoflegends.com/en/signup/index")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                .post(requestBody)
                .build()
        ).execute()

        val json = JsonParser().parse(response.body().string()).asJsonObject
        // Check for error
        if (json.has("code")) {
            var errorMessage = "RiotAPI error: ${json.get("code").asString}:\n${json.get("description").asString}\n"
            if (json.has("fields")) {
                errorMessage += json.getAsJsonObject("fields").toString()
            }
            throw IllegalStateException(errorMessage)
        }
        val accountId = json.get("account").asJsonObject.get("accountId").asString
        accountId
    }
}