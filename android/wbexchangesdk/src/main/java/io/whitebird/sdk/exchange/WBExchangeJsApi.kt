package io.whitebird.sdk.exchange

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import io.whitebird.sdk.exchange.WBExchangeSdk.Companion.sdklog
import org.json.JSONObject

// -----------------------------------------

enum class PostMessageType()
{
    OnChangeTokens,
    OnBackButton,
    OnOpenLink,
    OnUserData,
    OnOrderCreated;

    companion object
    {
        val names by lazy { entries.map { it.name } }
    }
}

// -----------------------------------------

class WBExchangeJsApi(private val context: Context)
{
    private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance("from WBExchangeJsApi") }

    companion object
    {
        public const val API_NAME = "WBSdkJsApi"
    }

    private fun openExternalUrl(url: String)
    {
        try
        {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (context !is Activity)
            {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            sdklog("-> WB/jsApi: openExternalUrl", url)
        }
        catch (e: ActivityNotFoundException)
        {
            sdklog("-> WB/jsApi: openExternalUrl failed", e.message ?: "ActivityNotFoundException")
        }
        catch (e: Exception)
        {
            sdklog("-> WB/jsApi: openExternalUrl failed", e.message ?: "Unknown error")
        }
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
            val email = messageObj.optString("email")
            val accessToken = messageObj.optString("accessToken")
            val refreshToken = messageObj.optString("refreshToken")
            val isUserVerified = messageObj.optBoolean("isUserVerified")

            sdklog("-> ... email          =", email)
            sdklog("-> ... accessToken    =", accessToken.takeLast(10))
            sdklog("-> ... refreshToken   =", refreshToken.takeLast(10))
            sdklog("-> ... isUserVerified =", isUserVerified.toString())

            if (wbExchangeSdk.getConfig().isAuthMode)
            {
                wbExchangeSdk.invokeOnChangeTokensHandler(accessToken, refreshToken, isUserVerified)
            }
            return true
        }

        // -----------------------------------------

        if (type == PostMessageType.OnOpenLink.name)
        {
            val link = messageObj.optString("link")
            sdklog("-> ... link =", link)

            if (link.isNotBlank())
            {
                openExternalUrl(link)
            }
            else
            {
                sdklog("-> ... ERROR: link isBlank", "!!!")
            }
            return true
        }

        // -----------------------------------------

        if (type == PostMessageType.OnUserData.name)
        {
            val email = messageObj.optString("email")
            val accessToken = messageObj.optString("accessToken")
            val refreshToken = messageObj.optString("refreshToken")

            sdklog("-> ... email        =", email)
            sdklog("-> ... accessToken  =", accessToken.takeLast(10))
            sdklog("-> ... refreshToken =", refreshToken.takeLast(10))

            if (wbExchangeSdk.getConfig().isLoginMode)
            {
                wbExchangeSdk.invokeOnUserDataHandler(email, accessToken, refreshToken)
            }
            return true
        }

        // -----------------------------------------

        if (type == PostMessageType.OnOrderCreated.name)
        {
            val orderId = messageObj.optString("orderId")
            val internalCryptoAddress = messageObj.optString("internalCryptoAddress")

            sdklog("-> ... orderId               =", orderId)
            sdklog("-> ... internalCryptoAddress =", internalCryptoAddress)

            wbExchangeSdk.invokeOnOrderCreatedHandler(
                orderId,
                internalCryptoAddress.ifBlank { null }
            )
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