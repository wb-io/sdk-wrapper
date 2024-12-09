package io.whitebird.sdk.exchange

import android.webkit.JavascriptInterface
import io.whitebird.sdk.exchange.WBExchangeSdk.Companion.sdklog
import org.json.JSONObject

// -----------------------------------------

enum class PostMessageType()
{
    OnChangeTokens,
    OnBackButton;

    companion object
    {
        val names by lazy { entries.map { it.name } }
    }
}

// -----------------------------------------

class WBExchangeJsApi
{
    private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance("from WBExchangeJsApi") }

    companion object
    {
        public const val API_NAME = "WBSdkJsApi"
    }

    @JavascriptInterface
    fun postMessage(messageJsonStr: String, targetOrigin: String): Boolean
    {
        sdklog("-> WB/postMessage: message =", messageJsonStr)

        val messageObj = JSONObject(messageJsonStr)

        // -----------------------------------------

        val type = messageObj.optString("type", "")
        sdklog("-> ... type =", type)

        if (type.isEmpty())
        {
            sdklog("-> ... ERROR: type isEmpty", "!!!")
            return false
        }

        if (!PostMessageType.names.contains(type))
        {
            sdklog("-> ... ERROR: unsupported type", "!!!")
            return false
        }

        // -----------------------------------------

        if (type == PostMessageType.OnChangeTokens.name)
        {
            val accessToken = messageObj.optString("accessToken")
            val isUserVerified = messageObj.optBoolean("isUserVerified")

            sdklog("-> ... accessToken    =", accessToken.takeLast(10))
            sdklog("-> ... isUserVerified =", isUserVerified.toString())

            wbExchangeSdk.invokeOnChangeTokensHandler(accessToken, isUserVerified)
            return true
        }

        // -----------------------------------------

        if (type == PostMessageType.OnBackButton.name)
        {
            wbExchangeSdk.invokeOnExitHandler("from postMessage -> OnBackButton")
            return true
        }

        // -----------------------------------------

        return true
    }
}