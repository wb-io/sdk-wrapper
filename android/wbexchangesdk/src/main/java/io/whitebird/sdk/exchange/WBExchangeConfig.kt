package io.whitebird.sdk.exchange

import android.util.Log
import io.whitebird.sdk.exchange.BuildConfig.URL_EXCHANGE_SERVER

enum class WBExchangeSdkMode
{
    AuthMode,
    LoginMode,
    TokensMode;
}

class WBExchangeConfig
{
    init
    {
        Log.d("-> WB/config: INIT", "")
    }

    // -----------------------------------------

    var mode: WBExchangeSdkMode? = null

    val isAuthMode: Boolean get() = mode == WBExchangeSdkMode.AuthMode
    val isTokensMode: Boolean get() = mode == WBExchangeSdkMode.TokensMode
    val isLoginMode: Boolean get() = mode == WBExchangeSdkMode.LoginMode
    val isModeNotSelected: Boolean get() = mode == null

    // -----------------------------------------

    var merchantId: String = ""

    var showBackButtonOnHomePage: Boolean = false // <- MobileType
    var onExitHandler: (() -> Unit)? = null

    // -----------------------------------------

    // TokensMode
    var accessToken: String = ""
    var refreshToken: String = ""

    // -----------------------------------------

    // AuthMode
    var onLoginHandler: ((accessToken: String, isUserVerified: Boolean) -> Unit)? = null

    // -----------------------------------------

    var goBack: (() -> Boolean)? = null

    var updateWebViewUrl: (() -> Unit)? = null
    var updateUI: (() -> Unit)? = null

    // -----------------------------------------

    fun getUrl(): String
    {
        if (isModeNotSelected)
        {
            Log.d("-> getUrl", "isModeNotSelected")
            return "" // TODO: ?? "about:blank"
        }

        val server = URL_EXCHANGE_SERVER

        val modeStr = when (mode)
        {
            WBExchangeSdkMode.LoginMode -> "LoginMode"
            WBExchangeSdkMode.AuthMode -> "AuthMode"
            WBExchangeSdkMode.TokensMode -> "TokensMode"
            null -> "null"
        }
        val modeQuery = "?mode=${modeStr}" // required

        val merchantIdQuery = "&merchantId=${merchantId}" // required

        var showBackButton = false
        if (showBackButtonOnHomePage && onExitHandler != null)
        {
            showBackButton = true
        }
        val showBackButtonQuery =
            "&showBackButtonOnHomePage=${if (showBackButton) "true" else "false"}"

        var tokensQuery = ""
        if (isTokensMode && accessToken.isNotBlank() && refreshToken.isNotBlank())
        {
            tokensQuery = "&access_token=${accessToken}&refresh_token=${refreshToken}"
        }

        return "${server}${modeQuery}${merchantIdQuery}${showBackButtonQuery}${tokensQuery}"
    }
}
