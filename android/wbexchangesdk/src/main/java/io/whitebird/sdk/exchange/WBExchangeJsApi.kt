package io.whitebird.sdk.exchange

import android.util.Log
import android.webkit.JavascriptInterface
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
        Log.d("-> WB/postMessage: message =", messageJsonStr)

        val messageObj = JSONObject(messageJsonStr)

        // -----------------------------------------

        val type = messageObj.optString("type", "")
        Log.d("-> ... type =", type)

        if (type.isEmpty())
        {
            Log.w("-> ... ERROR: type isEmpty", "!!!")
            return false
        }

        if (!PostMessageType.names.contains(type))
        {
            Log.w("-> ... ERROR: unsupported type", "!!!")
            return false
        }

        // -----------------------------------------

        if (type == PostMessageType.OnChangeTokens.name)
        {
            val accessToken = messageObj.optString("accessToken")
            val isUserVerified = messageObj.optBoolean("isUserVerified")

            Log.d("-> ... accessToken    =", accessToken.takeLast(10))
            Log.d("-> ... isUserVerified =", isUserVerified.toString())

            wbExchangeSdk.invokeOnChangeTokensHandler(accessToken, isUserVerified)
            return true
        }

        // -----------------------------------------

        if (type == PostMessageType.OnBackButton.name)
        {
            wbExchangeSdk.invokeOnExitHandler()
            return true
        }

        // -----------------------------------------

        return true
    }
}