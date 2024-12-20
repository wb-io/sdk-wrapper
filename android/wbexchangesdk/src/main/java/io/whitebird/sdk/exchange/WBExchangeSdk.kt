package io.whitebird.sdk.exchange

import android.util.Log
import io.whitebird.sdk.exchange.BuildConfig.SDKLOG_ENABLED

class WBExchangeSdk private constructor()
{
    private lateinit var config: WBExchangeConfig

    // -----------------------------------------

    companion object
    {
        @Volatile
        private var instance: WBExchangeSdk? = null

        fun getInstance(debugMessage: String = ""): WBExchangeSdk
        {
            sdklog("-> WB/sdk: getInstance", "$debugMessage -> $instance")
            return instance ?: synchronized(this) {
                instance ?: WBExchangeSdk().also {
                    instance = it
                    it.config = WBExchangeConfig()
                }
            }
        }

        fun sdklog(tag: String, msg: String = "")
        {
            if (SDKLOG_ENABLED) Log.d(tag, msg)
        }
    }

    // -----------------------------------------

    fun setup(
        mode: WBExchangeSdkMode,
        merchantId: String,

        // mode = WBExchangeSdkMode.TokensMode
        accessToken: String = "",
        refreshToken: String = "",

        // mode = WBExchangeSdkMode.AuthMode
        onLogin: ((accessToken: String, refreshToken: String, isUserVerified: Boolean) -> Unit)? = null,

        showBackButtonOnHomePage: Boolean = false,
        onExit: (() -> Unit)? = null,

        disableAddCard: Boolean = false
    )
    {
        sdklog("-> WB/sdk: setup", "")

        sdklog("-> ... mode", mode.toString())
        sdklog("-> ... merchantId", merchantId)

        config.mode = mode
        config.merchantId = merchantId

        if (config.isTokensMode)
        {
            sdklog("-> ... accessToken", accessToken.takeLast(10))
            sdklog("-> ... refreshToken", refreshToken.takeLast(10))
            config.accessToken = accessToken
            config.refreshToken = refreshToken
        }

        if (config.isAuthMode)
        {
            sdklog("-> ... onLogin", if (onLogin != null) "true" else "false")
            config.onLoginHandler = onLogin
        }

        config.showBackButtonOnHomePage = showBackButtonOnHomePage
//        if (showBackButtonOnHomePage)
//        {
        sdklog("-> ... onExit", if (onExit != null) "true" else "false")
        config.onExitHandler = onExit
//        }

        config.disableAddCard = disableAddCard

        config.updateWebViewUrl?.invoke()
    }

    // -----------------------------------------

    fun invokeOnChangeTokensHandler(accessToken: String, refreshToken: String, isUserVerified: Boolean)
    {
        sdklog(
            "-> WB/sdk: invokeOnChangeTokensHandler",
            "accessToken = ${accessToken.takeLast(10)}, refreshToken = ${refreshToken.takeLast(10)}, isUserVerified = $isUserVerified"
        )
        config.onLoginHandler?.invoke(accessToken, refreshToken, isUserVerified)
    }

    // -----------------------------------------

    fun invokeOnExitHandler(debugMsg: String = "")
    {
        sdklog("-> WB/sdk: invokeOnExitHandler", debugMsg)
        config.onExitHandler?.invoke()
    }

    // -----------------------------------------

    fun goBack(): Boolean
    {
        return config.goBack?.invoke() ?: false
    }

    // -----------------------------------------

    fun getConfig(): WBExchangeConfig
    {
        return config
    }

    // -----------------------------------------
}