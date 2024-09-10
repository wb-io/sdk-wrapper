package io.whitebird.sdk.exchange

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import io.whitebird.sdk.exchange.databinding.WbexchangeLayoutBinding

@SuppressLint("SetJavaScriptEnabled")
class WBExchangeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance() }

    private val binding: WbexchangeLayoutBinding

    private var isTokenApplied: Boolean = false

    private var isNetError: Boolean = false
    private var errorMessage: String = ""

    // -----------------------------------------

    companion object {
        private const val URL_ABOUT_BLANK = "about:blank"
        private const val URL_EXCHANGE_SERVER = BuildConfig.URL_EXCHANGE_SERVER

        private const val ERROR_MESSAGE =
            "Сервис временно недоступен,\nобратитесь в службу поддержки."
    }

    // -----------------------------------------

    init {
        Log.d("-> WB/view: init", "")

        inflate(context, R.layout.wbexchange_layout, this)

        binding = WbexchangeLayoutBinding.bind(this)

        wbExchangeSdk.setUpdateUI(::updateUI)
        wbExchangeSdk.setApplyToken(::applyToken)
        wbExchangeSdk.setGoBack(::goBack)

        initWebView()
        updateUI()
    }

    // TODO: !? onDestroy
//        wbExchangeSdk.setUpdateUI(null)
//        wbExchangeSdk.setApplyToken(null)
//        wbExchangeSdk.setGoBack(null)

    // -----------------------------------------

    private fun initWebView() {
        Log.d("-> WB/view: initWebView", "")
        with(binding.wbWebView) {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }

            webChromeClient = object : WebChromeClient() {}

            webViewClient = object : WebViewClient() {

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.e(
                        "-> WB/view: onReceivedError",
                        "errorCode = ${error?.errorCode.toString()} => ${error?.description.toString()}"
                    )
                    binding.wbWebView.loadUrl(URL_ABOUT_BLANK)

//                    binding.wbWebView.visibility = WebView.INVISIBLE

//                    val toast = Toast.makeText(context, "Ups! some NET Error", Toast.LENGTH_SHORT)
//                    toast.show()

                    isNetError = true
                    errorMessage = ERROR_MESSAGE
//                    errorMessage = error?.description.toString()

                    // no server
//                  // errorCode = -6 => net::ERR_CONNECTION_REFUSED

                    // no wifi / mobile inet
                    // errorCode = -2 => net::ERR_ADDRESS_UNREACHABLE

                    updateUI()
                }
            }
        }
    }

    // -----------------------------------------

    private fun applyToken() {

        Log.d("-> WB/view: applyToken", "")

        val config = wbExchangeSdk.config()

        if (config.loginRequired) {
            isTokenApplied = false
            isNetError = false
            val url = "${URL_EXCHANGE_SERVER}?login_required=true"
            binding.wbWebView.loadUrl(url)
            return
        }

        if (config.accessToken.isBlank()) {
            Log.e("-> WB/view: applyToken", "token is EMPTY")
            isTokenApplied = false
            isNetError = false
            binding.wbWebView.loadUrl(URL_ABOUT_BLANK)
            updateUI()
            return
        }

        if (isTokenApplied) {
            Log.w("-> WB/view: applyToken", "TOKEN already applied")
            return
        }

        Log.d("-> WB/view: applyToken", "OK")

        isTokenApplied = true
        isNetError = false

        val url =
            "${URL_EXCHANGE_SERVER}?access_token=${config.accessToken}&refresh_token=${config.refreshToken}"
        binding.wbWebView.loadUrl(url)
        updateUI()
    }

    // -----------------------------------------

    private fun goBack(): Boolean {
        if (binding.wbWebView.canGoBack()) {
            Log.d("-> WB/view: goBack", "TRUE")
            binding.wbWebView.goBack()
            return true
        } else {
            Log.d("-> WB/view: goBack", "FALSE")
            return false
        }
    }

    // -----------------------------------------

    private fun updateUI() {

        val config = wbExchangeSdk.config()

        if (isNetError) {
            Log.d("-> WB/view: checkUIVisibility", "isNetError = true")
            binding.wbLoader.visibility = ProgressBar.INVISIBLE
            binding.wbWebView.visibility = WebView.VISIBLE

            binding.wbErrorText.text = errorMessage
            binding.wbErrorText.visibility = WebView.VISIBLE
            return
        } else {
            binding.wbErrorText.visibility = ProgressBar.INVISIBLE
        }

        if (isTokenApplied) {
            Log.d("-> WB/view: checkUIVisibility", "isTokenApplied = true")
            binding.wbLoader.visibility = ProgressBar.INVISIBLE
            binding.wbWebView.visibility = WebView.VISIBLE
        } else { // !isTokenApplied
            Log.d("-> WB/view: checkUIVisibility", "isTokenApplied = false")
            if (!config.loginRequired) {
                binding.wbLoader.visibility = ProgressBar.VISIBLE
                binding.wbWebView.visibility = WebView.INVISIBLE
            }
        }

        if (!config.showLoaderIfNoToken || config.loginRequired)
            binding.wbLoader.visibility = ProgressBar.INVISIBLE
    }
}
