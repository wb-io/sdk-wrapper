package io.whitebird.sdk.exchange

import android.util.Log

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
            Log.d("-> WB/sdk: getInstance", "$debugMessage -> $instance")
            return instance ?: synchronized(this) {
                instance ?: WBExchangeSdk().also {
                    instance = it
                    it.config = WBExchangeConfig()
                }
            }
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
        onLogin: ((accessToken: String, isUserVerified: Boolean) -> Unit)? = null,

        showBackButtonOnHomePage: Boolean = false,
        onExit: (() -> Unit)? = null
    )
    {
        Log.d("-> WB/sdk: setup", "")

        Log.d("-> ... mode", mode.toString())
        Log.d("-> ... merchantId", merchantId)

        config.mode = mode
        config.merchantId = merchantId

        if (config.isTokensMode)
        {
            Log.d("-> ... accessToken", accessToken.takeLast(10))
            Log.d("-> ... refreshToken", refreshToken.takeLast(10))
            config.accessToken = accessToken
            config.refreshToken = refreshToken
        }

        if (config.isAuthMode)
        {
            Log.d("-> ... onLogin", if (onLogin != null) "true" else "false")
            config.onLoginHandler = onLogin
        }

        config.showBackButtonOnHomePage = showBackButtonOnHomePage
        if (showBackButtonOnHomePage)
        {
            Log.d("-> ... onExit", if (onExit != null) "true" else "false")
            config.onExitHandler = onExit
        }

        config.updateWebViewUrl?.invoke()
    }

    // -----------------------------------------

    fun invokeOnChangeTokensHandler(accessToken: String, isUserVerified: Boolean)
    {
        Log.d(
            "-> WB/sdk: invokeOnChangeTokensHandler",
            "accessToken = ${accessToken.takeLast(10)}, isUserVerified = $isUserVerified"
        )
        config.onLoginHandler?.invoke(accessToken, isUserVerified)
    }

    // -----------------------------------------

    fun invokeOnExitHandler()
    {
        Log.d("-> WB/sdk: invokeOnExitHandler", "")
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