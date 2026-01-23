package io.whitebird.sdk.exchange

import java.math.BigDecimal
import io.whitebird.sdk.exchange.WBExchangeSdk.Companion.sdklog
import java.net.URLEncoder

enum class WBExchangeSdkMode
{
    AuthMode,
    LoginMode,
    TokensMode;
}

enum class WBExchangeEnvironment {
    DEV,
    QA,
    PROD;

    fun getBaseUrl(): String {
        return when(this) {
            DEV -> "https://sdk.dev.wbdevel.net/v2.0/"
            QA -> "https://sdk.qa.wbdevel.net/v2.0/"
            PROD -> "https://sdk.whitebird.io/v2.0/"
        }
    }
}

// Currency options for pre-filling. Display names for reference:
enum class WBCurrency(val displayName: String) {
    BYN("BYN"),
    USD("USD"),
    EUR("EUR"),
    RUB("RUB"),
    BTC("BTC"),
    ETH("ETH"),
    USDT("USDT (ERC-20)"),
    TRX("TRX"),
    USDC("USDC (ERC-20)"),
    USDT_TRC("USDT (TRC-20)"),
    TON("TON"),
    USDT_TON("USDT (TON)"),
    WBP("WBP (TRC-20)");
}

enum class WBStartAppPage(val urlPath: String?) {
    HOMEPAGE(null),
    PAYMENTS("/account/payments"),
    OPERATIONS("/user-operations");
}

class WBExchangeConfig
{
    init
    {
        sdklog("-> WB/config: INIT")
    }

    // -----------------------------------------

    var mode: WBExchangeSdkMode? = null
    var environment: WBExchangeEnvironment = WBExchangeEnvironment.DEV

    val isAuthMode: Boolean get() = mode == WBExchangeSdkMode.AuthMode
    val isTokensMode: Boolean get() = mode == WBExchangeSdkMode.TokensMode
    val isLoginMode: Boolean get() = mode == WBExchangeSdkMode.LoginMode
    val isModeNotSelected: Boolean get() = mode == null

    // -----------------------------------------

    var merchantId: String = ""
    var merchantPass: String? = null

    var showBackButtonOnHomePage: Boolean = false // <- MobileType
    var onExitHandler: (() -> Unit)? = null

    var disableAddCard: Boolean = false

    var email: String? = null
    var externalClientId: String? = null
    var currencyAmount: BigDecimal? = null
    var currencyFrom: WBCurrency? = null
    var currencyTo: WBCurrency? = null
    var disableCurrencyFrom: Boolean = false
    var disableCurrencyTo: Boolean = false
    var isAuthAgent: Boolean = false
    var cryptoWallet: String? = null
    var redirectUrl: String? = null
    var refId: String? = null
    var startAppPage: WBStartAppPage = WBStartAppPage.HOMEPAGE

    // -----------------------------------------

    // TokensMode
    var accessToken: String = ""
    var refreshToken: String = ""

    // -----------------------------------------

    // AuthMode
    var onLoginHandler: ((accessToken: String, refreshToken: String, isUserVerified: Boolean) -> Unit)? = null

    // LoginMode
    var onUserDataHandler: ((email: String?, accessToken: String, refreshToken: String) -> Unit)? = null

    // Not tied to specific mode
    var onOrderCreatedHandler: ((orderId: String, internalCryptoAddress: String?) -> Unit)? = null

    // -----------------------------------------

    var goBack: (() -> Boolean)? = null

    var updateWebViewUrl: (() -> Unit)? = null
    var updateUI: (() -> Unit)? = null

    // -----------------------------------------

    fun getUrl(): String
    {
        if (isModeNotSelected)
        {
            sdklog("-> getUrl", "isModeNotSelected")
            return "" // TODO: ?? "about:blank"
        }

        val server = environment.getBaseUrl()

        val modeStr = when (mode)
        {
            WBExchangeSdkMode.LoginMode -> "LoginMode"
            WBExchangeSdkMode.AuthMode -> "AuthMode"
            WBExchangeSdkMode.TokensMode -> "TokensMode"
            null -> "null"
        }
        val modeQuery = "?mode=${modeStr}" // required

        val merchantIdQuery = "&merchantId=${merchantId}" // required
        val merchantPassQuery = if (merchantPass != null) "&merchantPass=${merchantPass}" else ""

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

        val disableAddCardQuery =
            "&disableAddCard=${if (disableAddCard) "true" else "false"}"

        val emailQuery = if (!email.isNullOrBlank()) "&email=${email}" else ""
        val externalClientIdQuery = if (!externalClientId.isNullOrBlank()) "&externalClientId=${externalClientId}" else ""
        val currencyAmountQuery = if (currencyAmount != null) "&currencyAmount=${currencyAmount!!.toPlainString()}" else ""
        val currencyFromQuery = if (currencyFrom != null) "&currencyFrom=${currencyFrom}" else ""
        val currencyToQuery = if (currencyTo != null) "&currencyTo=${currencyTo}" else ""
        val disableCurrencyFromQuery = if (disableCurrencyFrom) "&disableCurrencyFrom=true" else ""
        val disableCurrencyToQuery = if (disableCurrencyTo) "&disableCurrencyTo=true" else ""
        val isAuthAgentQuery = if (isAuthAgent) "&isAuthAgent=true" else ""
        val cryptoWalletQuery = if (!cryptoWallet.isNullOrBlank()) "&cryptoWallet=${cryptoWallet}" else ""
        val redirectUrlQuery = if (!redirectUrl.isNullOrBlank()) "&redirectUrl=${URLEncoder.encode(redirectUrl!!, Charsets.UTF_8.name())}" else ""
        val refIdQuery = if (!refId.isNullOrBlank()) "&refId=${refId}" else ""
        val startAppPageQuery = startAppPage.urlPath?.let { "&startAppPage=$it" } ?: ""

        return "${server}${modeQuery}${merchantIdQuery}${merchantPassQuery}${showBackButtonQuery}${tokensQuery}${disableAddCardQuery}${emailQuery}${externalClientIdQuery}${currencyAmountQuery}${currencyFromQuery}${currencyToQuery}${disableCurrencyFromQuery}${disableCurrencyToQuery}${isAuthAgentQuery}${cryptoWalletQuery}${redirectUrlQuery}${refIdQuery}${startAppPageQuery}"
    }
}
