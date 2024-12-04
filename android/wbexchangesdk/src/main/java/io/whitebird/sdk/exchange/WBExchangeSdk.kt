package io.whitebird.sdk.exchange

import android.util.Log

class WBExchangeSdk private constructor()
{
    private lateinit var config: WBExchangeConfig

//    private var updateUI: (() -> Unit)? = null
//    private var updateWebViewUrl: (() -> Unit)? = null
//    private var exchangeViewGoBack: (() -> Boolean)? = null

//    private var onChangeTokensHandler: ((accessToken: String, refreshToken: String) -> Unit)? = null
//    private var onExitHandler: (() -> Unit)? = null

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

        // ??
//        loginRequired: Boolean = false,

        // ??
//        showLoaderIfNoToken: Boolean = false,

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

//        if (config.isInitialized)
//        {
//            // TODO: ??
//        }

        Log.d("-> ... mode", mode.toString())
        Log.d("-> ... merchantId", merchantId)

        config.mode = mode
        config.merchantId = merchantId

        // ??
//        config.loginRequired = loginRequired

        // ??
//        config.showLoaderIfNoToken = showLoaderIfNoToken

        if (config.isTokensMode)
        {
            Log.d("-> ... accessToken", accessToken.takeLast(10))
            Log.d("-> ... refreshToken", refreshToken.takeLast(10))
            config.accessToken = accessToken
            config.refreshToken = refreshToken
        }

        if (config.isAuthMode)
        {
            Log.d("-> ... onLogin", if(onLogin != null) "true" else "false")
            config.onLoginHandler = onLogin
        }

        config.showBackButtonOnHomePage = showBackButtonOnHomePage
        if (showBackButtonOnHomePage)
        {
            Log.d("-> ... onExit", if(onExit != null) "true" else "false")
            config.onExitHandler = onExit
        }

        config.updateWebViewUrl?.invoke()
    }

    // -----------------------------------------

//    fun onChangeTokens(fn: ((accessToken: String, refreshToken: String) -> Unit)?)
//    {
//        Log.d("-> WB/sdk: onChangeTokens", "added cb")
//        config.onChangeTokensHandler = fn
//    }

    fun invokeOnChangeTokensHandler(accessToken: String, isUserVerified: Boolean)
    {
        Log.d(
            "-> WB/sdk: invokeOnChangeTokensHandler",
            "accessToken = ${accessToken.takeLast(10)}, isUserVerified = $isUserVerified"
        )
        config.onLoginHandler?.invoke(accessToken, isUserVerified)
    }

    // -----------------------------------------

//    fun onExit(fn: (() -> Unit)?)
//    {
//        Log.d("-> WB/sdk: onExit", "added cb")
//        config.onExitHandler = fn
//    }

    fun invokeOnExitHandler()
    {
        Log.d("-> WB/sdk: invokeOnExitHandler", "")
        config.onExitHandler?.invoke()
    }

    // -----------------------------------------

//    fun setUpdateUI(fn: (() -> Unit)?)
//    {
//        updateUI = fn
//    }

//    fun setUpdateWebViewUrl(fn: (() -> Unit)?)
//    {
//        updateWebViewUrl = fn
//    }

//    fun setGoBack(fn: (() -> Boolean)?)
//    {
//        exchangeViewGoBack = fn
//    }

    fun goBack(): Boolean
    {
        return config.goBack?.invoke() ?: false
    }

    // -----------------------------------------

    fun getConfig(): WBExchangeConfig
    {
        return config
    }

//    fun setShowLoaderIfNoToken(newValue: Boolean)
//    {
//        Log.d("-> WB/sdk: setShowLoaderIfNoToken", newValue.toString())
//        config.showLoaderIfNoToken = newValue
//        config.updateUI?.invoke()
//    }

//    fun setLoginRequired(newValue: Boolean)
//    {
//        Log.d("-> WB/sdk: setLoginRequired", newValue.toString())
//        config.loginRequired = newValue
//        config.updateWebViewUrl?.invoke()
//    }

//    fun setShowBackButtonOnHomePage(newValue: Boolean)
//    {
//        Log.d("-> WB/sdk: setShowBackButtonOnHomePage", newValue.toString())
//        config.showBackButtonOnHomePage = newValue
//        config.updateWebViewUrl?.invoke()
//    }

//    fun setTokens(accessToken: String, refreshToken: String)
//    {
//        if (accessToken.isBlank())
//            Log.d("-> WB/sdk: setToken", "-> EMPTY")
//        else
//            Log.d(
//                "-> WB/sdk: setToken",
//                "${
//                    accessToken.substring(
//                        0,
//                        if (accessToken.length > 20) 20 else accessToken.length
//                    )
//                }..."
//            )
//
//        config.accessToken = accessToken
//        config.refreshToken = refreshToken
//        config.updateWebViewUrl?.invoke()
//    }
}