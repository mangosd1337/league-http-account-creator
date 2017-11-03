package com.accountcreator.riotapi

import com.accountcreator.Credentials
import io.reactivex.Completable
import io.reactivex.Single


fun createAccount(credentials: Credentials, captchaToken: String): Single<String> {
    return Single.fromCallable {
        ""
    }
}