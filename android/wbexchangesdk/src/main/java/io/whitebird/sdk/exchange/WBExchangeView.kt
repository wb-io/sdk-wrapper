package io.whitebird.sdk.exchange

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import io.whitebird.sdk.exchange.WBExchangeSdk.Companion.sdklog
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

    // -----------------------------------------

    companion object
    {
        // TODO: ??
//        private const val URL_ABOUT_BLANK = "about:blank"
    }

    // -----------------------------------------

    init
    {
        sdklog("-> WB/view: -------------------", "")
        sdklog("-> WB/view: init", "")

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
        sdklog("-> WB/view: initWebView", "")
        sdklog("-> ... binding.wbWebView", "${binding.wbWebView}")

        with(binding.wbWebView) {
            sdklog("-> ... with(binding.wbWebView)", "")

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }

            // binding.wbWebView.
            addJavascriptInterface(WBExchangeJsApi(), WBExchangeJsApi.API_NAME)

            // До завершения загрузки страницы работает WebViewClient,
            // а после — WebChromeClient.

            // WebViewClient требуется для того,
            // чтобы контролировать процесс загрузки страницы,
            // ---
            // !!! и чтобы переход по внешним ссылкам оставался в webview
            // а не открывался браузер
            webViewClient = object : WebViewClient()
            {}

            // WebChromeClient — чтобы взаимодействовать с этой страницей после того,
            // как она успешно загружена.
            webChromeClient = object : WebChromeClient()
            {}
        }
    }

    // -----------------------------------------

    private fun updateWebViewUrl()
    {
        sdklog("-> WB/view: updateWebViewUrl", "")
        val currentUrl = binding.wbWebView.url
        sdklog("-> ... currentUrl from =", "$currentUrl")

        val config = wbExchangeSdk.getConfig()

        sdklog("-> ... OK", "")

        val url = config.getUrl()
        sdklog("-> ... currentUrl to   =", url)

        binding.wbWebView.clearHistory()
        binding.wbWebView.clearCache(false)
        binding.wbWebView.loadUrl(url)
        updateUI()
    }

    // -----------------------------------------

    private fun goBack(): Boolean
    {
        sdklog("-> WB/view: goBack", "")
        val currentUrl = binding.wbWebView.url
        sdklog("-> ... currentUrl", "$currentUrl")

        if (binding.wbWebView.canGoBack())
        {
            sdklog("-> ...", "TRUE")
            binding.wbWebView.goBack()
            return true
        }
        else
        {
            sdklog("-> ...", "FALSE")
            wbExchangeSdk.invokeOnExitHandler("from onBack")
            return false
        }
    }

    // -----------------------------------------

    private fun updateUI()
    {
        sdklog("-> WB/view: updateUI", "")
        val config = wbExchangeSdk.getConfig()

        if (config.isModeNotSelected)
        {
            sdklog("-> ... isModeNotSelected", config.isModeNotSelected.toString())
            binding.wbWebView.visibility = View.INVISIBLE
            return
        }

        val url = config.getUrl()

        if (url.isNotBlank())
        {
            sdklog("-> ... show WebView", "true")
            binding.wbWebView.visibility = View.VISIBLE
        }
        else
        {
            sdklog("-> ... show WebView", "false")
            binding.wbWebView.visibility = View.INVISIBLE
//            binding.wbWebView.goBack() // ??
        }
    }
}
