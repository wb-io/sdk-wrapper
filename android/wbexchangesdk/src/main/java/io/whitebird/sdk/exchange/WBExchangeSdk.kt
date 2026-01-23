package io.whitebird.sdk.exchange

import android.util.Log
import java.math.BigDecimal

class WBExchangeSdk private constructor()
{
    private lateinit var config: WBExchangeConfig

    // -----------------------------------------

    companion object
    {
        @Volatile
        private var instance: WBExchangeSdk? = null
        private var logEnabled: Boolean = false

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
            if (logEnabled) Log.d(tag, msg)
        }
        
        fun setLogEnabled(enabled: Boolean) {
            logEnabled = enabled
        }
    }

    // -----------------------------------------

    fun setup(
        mode: WBExchangeSdkMode,
        merchantId: String,
        merchantPass: String? = null,
        environment: WBExchangeEnvironment = WBExchangeEnvironment.DEV,
        logEnabled: Boolean = false,

        // mode = WBExchangeSdkMode.TokensMode
        accessToken: String = "",
        refreshToken: String = "",

        // mode = WBExchangeSdkMode.AuthMode
        onLogin: ((accessToken: String, refreshToken: String, isUserVerified: Boolean) -> Unit)? = null,

        // mode = WBExchangeSdkMode.LoginMode
        onUserData: ((email: String?, accessToken: String, refreshToken: String) -> Unit)? = null,

        showBackButtonOnHomePage: Boolean = false,
        onExit: (() -> Unit)? = null,

        disableAddCard: Boolean = false,

        // Optional parameters
        email: String? = null,
        externalClientId: String? = null,
        currencyAmount: BigDecimal? = null,
        currencyFrom: WBCurrency? = null,
        currencyTo: WBCurrency? = null,
        disableCurrencyFrom: Boolean = false,
        disableCurrencyTo: Boolean = false,
        isAuthAgent: Boolean = false,
        cryptoWallet: String? = null,
        redirectUrl: String? = null,
        refId: String? = null,
        startAppPage: WBStartAppPage = WBStartAppPage.HOMEPAGE,

        onOrderCreated: ((orderId: String, internalCryptoAddress: String?) -> Unit)? = null
    )
    {
        setLogEnabled(logEnabled)
        sdklog("-> WB/sdk: setup", "")

        sdklog("-> ... mode", mode.toString())
        sdklog("-> ... merchantId", merchantId)
        sdklog("-> ... merchantPass", if (!merchantPass.isNullOrEmpty()) "***" else "")
        sdklog("-> ... environment", environment.toString())
        sdklog("-> ... logEnabled", logEnabled.toString())

        config.mode = mode
        config.merchantId = merchantId
        config.merchantPass = merchantPass
        config.environment = environment

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

        if (config.isLoginMode)
        {
            sdklog("-> ... onUserData", if (onUserData != null) "true" else "false")
            config.onUserDataHandler = onUserData
        }

        config.showBackButtonOnHomePage = showBackButtonOnHomePage
//        if (showBackButtonOnHomePage)
//        {
        sdklog("-> ... onExit", if (onExit != null) "true" else "false")
        config.onExitHandler = onExit
//        }

        config.disableAddCard = disableAddCard

        config.email = email
        config.externalClientId = externalClientId
        config.currencyAmount = currencyAmount
        config.currencyFrom = currencyFrom
        config.currencyTo = currencyTo
        config.disableCurrencyFrom = disableCurrencyFrom
        config.disableCurrencyTo = disableCurrencyTo
        config.isAuthAgent = isAuthAgent
        config.cryptoWallet = cryptoWallet
        config.redirectUrl = redirectUrl
        config.refId = refId
        config.startAppPage = startAppPage

        sdklog("-> ... onOrderCreated", if (onOrderCreated != null) "true" else "false")
        config.onOrderCreatedHandler = onOrderCreated

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

    fun invokeOnUserDataHandler(email: String?, accessToken: String, refreshToken: String)
    {
        sdklog(
            "-> WB/sdk: invokeOnUserDataHandler",
            "email = ${email ?: ""}, accessToken = ${accessToken.takeLast(10)}, refreshToken = ${refreshToken.takeLast(10)}"
        )
        config.onUserDataHandler?.invoke(email, accessToken, refreshToken)
    }

    // -----------------------------------------

    fun invokeOnOrderCreatedHandler(orderId: String, internalCryptoAddress: String?)
    {
        sdklog(
            "-> WB/sdk: invokeOnOrderCreatedHandler",
            "orderId = $orderId, internalCryptoAddress = ${internalCryptoAddress ?: ""}"
        )
        config.onOrderCreatedHandler?.invoke(orderId, internalCryptoAddress)
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
