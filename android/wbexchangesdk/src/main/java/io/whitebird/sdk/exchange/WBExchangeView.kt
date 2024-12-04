package io.whitebird.sdk.exchange

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import io.whitebird.sdk.exchange.databinding.WbexchangeLayoutBinding

@SuppressLint("SetJavaScriptEnabled")
class WBExchangeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes)
{
    private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance("from WBExchangeView") }

    private val binding: WbexchangeLayoutBinding

    private var isNetError: Boolean = false
    private var errorMessage: String = ""

    // -----------------------------------------

    companion object
    {
        private const val URL_ABOUT_BLANK = "about:blank"

        private const val ERROR_MESSAGE =
            "Сервис временно недоступен,\nобратитесь в службу поддержки."
    }

    // -----------------------------------------

    init
    {
        Log.d("-> WB/view: -------------------", "")
        Log.d("-> WB/view: init", "")

        inflate(context, R.layout.wbexchange_layout, this)

        binding = WbexchangeLayoutBinding.bind(this)

        val config = wbExchangeSdk.getConfig()

        config.updateWebViewUrl = ::updateWebViewUrl
        config.updateUI = ::updateUI
        config.goBack = ::goBack

        initWebView()
        updateWebViewUrl()
    }

    // -----------------------------------------

    private fun initWebView()
    {
        Log.d("-> WB/view: initWebView", "")
        Log.d("-> ... binding.wbWebView", "${binding.wbWebView}")

        with(binding.wbWebView) {
            Log.d("-> ... with(binding.wbWebView)", "")

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }

            // binding.wbWebView.
            addJavascriptInterface(WBExchangeJsApi(), WBExchangeJsApi.API_NAME)

            webChromeClient = object : WebChromeClient()
            {}

            webViewClient = object : WebViewClient()
            {

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                )
                {
                    super.onReceivedError(view, request, error)

                    Log.e(
                        "-> WB/view: onReceivedError",
                        "errorCode = ${error?.errorCode.toString()} => ${error?.description.toString()}"
                    )
                    Log.e(
                        "-> ... request",
                        "for url = ${request?.url}"
                    )
//                    binding.wbWebView.loadUrl(URL_ABOUT_BLANK)

//                    binding.wbWebView.visibility = WebView.INVISIBLE

//                    val toast = Toast.makeText(context, "Ups! some NET Error", Toast.LENGTH_SHORT)
//                    toast.show()

//                    if (error?.errorCode == ERROR_CONNECT) // -6
//                    {
//                    }

                    if (error?.errorCode == ERROR_HOST_LOOKUP) // -2
                    {
                        isNetError = true
                        errorMessage = ERROR_MESSAGE
//                    errorMessage = error?.description.toString()
                        updateUI()
                    }

                    // no server / url
                    // errorCode = -6 => net::ERR_CONNECTION_REFUSED

                    // no wifi / mobile inet
                    // errorCode = -2 => net::ERR_ADDRESS_UNREACHABLE
                }
            }
        }
    }

    // -----------------------------------------

    private fun updateWebViewUrl()
    {
        Log.d("-> WB/view: updateWebViewUrl", "")
        val currentUrl = binding.wbWebView.url
        Log.d("-> ... currentUrl from =", "$currentUrl")

        val config = wbExchangeSdk.getConfig()

//        if (config.isModeNotSelected)
//        {
//            // TODO !!
//        }

//        if (config.isLoginMode)
//        {
//            // TODO !!
//        }

//        if (config.isAuthMode)
//        {
//            // TODO !!
//        }
//        if (config.loginRequired)
//        {
//            isTokenApplied = false
//            isNetError = false
//            // TODO: ?? login_required=true
//            val url = config.getUrl()
//            Log.d("-> ... currentUrl to   =", url)
//            binding.wbWebView.clearHistory()
//            binding.wbWebView.clearCache(false)
//            binding.wbWebView.loadUrl(url)
//            updateUI()
//            return
//        }

//        if (config.isTokensMode)
//        {
//            // TODO !!
//        }
//        if (config.accessToken.isBlank())
//        {
//            Log.e("-> ...", "token is EMPTY")
//            isTokenApplied = false
//            isNetError = false
//            // ??
////            binding.wbWebView.loadUrl(URL_ABOUT_BLANK)
//            updateUI()
//            return
//        }

//        if (isTokenApplied)
//        {
//            Log.w("->...", "TOKEN already applied")
//            return
//        }

        Log.d("-> ... OK", "")

//        isTokenApplied = true
        isNetError = false

        val url = config.getUrl()
        Log.d("-> ... currentUrl to   =", url)
        binding.wbWebView.clearHistory()
        binding.wbWebView.clearCache(false)
        binding.wbWebView.loadUrl(url)
        updateUI()
    }

    // -----------------------------------------

    private fun goBack(): Boolean
    {
        Log.d("-> WB/view: goBack", "")
        val currentUrl = binding.wbWebView.url
        Log.d("-> ... currentUrl", "$currentUrl")

        if (binding.wbWebView.canGoBack())
        {
            Log.d("-> ...", "TRUE")
            binding.wbWebView.goBack()
            return true
        }
        else
        {
            Log.d("-> ...", "FALSE")
            return false
        }
    }

    // -----------------------------------------

    private fun updateUI()
    {
        Log.d("-> WB/view: updateUI", "")
        val config = wbExchangeSdk.getConfig()

        if (config.isModeNotSelected)
        {
            Log.d("-> ... isModeNotSelected", config.isModeNotSelected.toString())
            binding.wbWebView.visibility = View.INVISIBLE
//            binding.wbLoader.visibility = View.GONE
            binding.wbErrorText.visibility = View.GONE
            return
        }

        if (isNetError)
        {
            Log.d("-> ... isNetError", "true")
            binding.wbWebView.visibility = View.INVISIBLE
//            binding.wbLoader.visibility = View.GONE

            binding.wbErrorText.text = errorMessage
            binding.wbErrorText.visibility = View.VISIBLE
            return
        }
        else
        {
            binding.wbErrorText.visibility = View.GONE
        }

//        if (config.loginRequired)
//        {
//            Log.d("-> ... loginRequired", "true")
//            binding.wbWebView.visibility = View.VISIBLE
//        }
//        else
//        {

        val url = config.getUrl()

//            Log.d("-> ... loginRequired", "false")
        if (url.isNotBlank())
        {
            Log.d("-> ... show WebView", "true")
            binding.wbWebView.visibility = View.VISIBLE
//                binding.wbLoader.visibility = View.GONE
        }
        else
        { // !isTokenApplied
            Log.d("-> ... show WebView", "false")
            binding.wbWebView.visibility = View.INVISIBLE
//                binding.wbLoader.visibility = View.VISIBLE
//            binding.wbWebView.goBack() // ??
        }
//        }

//        val isShowProgress = !config.loginRequired && !isTokenApplied && config.showLoaderIfNoToken
//        binding.wbLoader.visibility = if (isShowProgress) View.VISIBLE else View.GONE
    }
}
