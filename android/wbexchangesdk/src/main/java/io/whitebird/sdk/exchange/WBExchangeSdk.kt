package io.whitebird.sdk.exchange

import android.util.Log

class WBExchangeSdk private constructor() {

    private lateinit var config: WBExchangeConfig

    private var updateUI: (() -> Unit)? = null
    private var applyToken: (() -> Unit)? = null
    private var exchangeViewGoBack: (() -> Boolean)? = null

    // -----------------------------------------

    companion object {
        @Volatile
        private var instance: WBExchangeSdk? = null

        fun getInstance(): WBExchangeSdk {
            return instance ?: synchronized(this) {
                instance ?: WBExchangeSdk().also {
                    instance = it
                    it.config = WBExchangeConfig()
                }
            }
        }
    }

    // -----------------------------------------

    fun setUpdateUI(fn: (() -> Unit)?) {
        updateUI = fn
    }

    fun setApplyToken(fn: (() -> Unit)?) {
        applyToken = fn
    }

    fun setGoBack(fn: (() -> Boolean)?) {
        exchangeViewGoBack = fn
    }

    fun goBack(): Boolean {
        return exchangeViewGoBack?.invoke() ?: false
    }

    // -----------------------------------------

    fun config(): WBExchangeConfig {
        return config
    }

    fun setShowLoaderIfNoToken(newValue: Boolean) {
        Log.d("-> WB/sdk: setShowLoaderIfNoToken", newValue.toString())
        config.showLoaderIfNoToken = newValue
        updateUI?.invoke()
    }

    fun setLoginRequired(newValue: Boolean) {
        Log.d("-> WB/sdk: setLoginRequired", newValue.toString())
        config.loginRequired = newValue
        applyToken?.invoke()
    }

    fun setTokens(accessToken: String, refreshToken: String) {

        if (accessToken.isBlank())
            Log.d("-> WB/sdk: setToken", "-> EMPTY")
        else
            Log.d(
                "-> WB/sdk: setToken",
                "${
                    accessToken.substring(
                        0,
                        if (accessToken.length > 20) 20 else accessToken.length
                    )
                }..."
            )

        config.accessToken = accessToken
        config.refreshToken = refreshToken
        applyToken?.invoke()
    }
}