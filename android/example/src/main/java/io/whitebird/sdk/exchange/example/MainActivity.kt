package io.whitebird.sdk.exchange.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.whitebird.sdk.exchange.WBExchangeSdk
import io.whitebird.sdk.exchange.WBExchangeSdkMode
import io.whitebird.sdk.exchange.WBExchangeView

class MainActivity : AppCompatActivity()
{
    // -----------------------------------------
    // WBExchangeSKD:
    private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance("from MainActivity") }
    // -----------------------------------------

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        Log.d("-> MAIN_APP: onCreate", "")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // -----------------------------------------
        // WBExchangeSKD:
        // -----------------------------------------
        wbExchangeSdk.setup(
            merchantId = "merchantId_TEST",      // * required
//            mode = WBExchangeSdkMode.[выбрать_нужный_режим], // * required

            // LoginMode
            mode = WBExchangeSdkMode.LoginMode,

            // TokensMode
//            mode = WBExchangeSdkMode.TokensMode,
//            accessToken = "...",                 // default = ""
//            refreshToken = "...",                // default = ""

            // AuthMode
//            mode = WBExchangeSdkMode.AuthMode,
            onLogin = { accessToken, isUserVerified ->
                Log.d(
                    "-> MAIN_APP: onLogin",
                    "accessToken = ${accessToken.takeLast(10)}, isUserVerified = $isUserVerified"
                )
            },

            showBackButtonOnHomePage = true,     // default = false
            onExit = {
                Log.d("-> MAIN_APP: onExit", "")

                // android.view.ViewRootImpl$CalledFromWrongThreadException:
                // Only the original thread that created a view hierarchy can touch its views.
                // Expected: main Calling: JavaBridge
                runOnUiThread { // <- !!!
                    exitSkd()
                }
            }
        )

        // -----------------------------------------

        // DEMO - удаление/добавление WBExchangeView
        // при удалении удаляем, а НЕ скрываем view - что бы ->
        // при добавлении создать новый View -> test for webview init
        val wbWebViewWrapper = findViewById<LinearLayout>(R.id.wbWebViewWrapper)

        val btnAddWebView = findViewById<Button>(R.id.btnAddWebView)
        val btnRemoveWebView = findViewById<Button>(R.id.btnRemoveWebView)

        btnAddWebView.setOnClickListener {
            Log.d("-> MAIN_APP: btnShowWebView", "")
            btnAddWebView.isEnabled = false
            btnRemoveWebView.isEnabled = true

            val wbExchangeView = WBExchangeView(context = applicationContext)
            wbExchangeView.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)

            wbWebViewWrapper.addView(wbExchangeView)
        }

        btnRemoveWebView.setOnClickListener {
            Log.d("-> MAIN_APP: btnHideWebView", "")
            btnRemoveWebView.isEnabled = false
            btnAddWebView.isEnabled = true

            wbWebViewWrapper.removeAllViews()
        }
    }

    // -----------------------------------------
    // WBExchangeSKD:
    // управление кнопкой BACK в webview нашего SDK
    // надо производить из главного Activity в методе onBackPressed
    //
    // если SDK обработал BACK (вернулся на страницу назад)
    // -> wbExchangeSdk.goBack() - возвращает true
    //
    // если хотим выйти из SDK (кнопка/жест BACK нажата на HOME-page в webview)
    // -> wbExchangeSdk.goBack() - возвращает false
    // -----------------------------------------
    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed()
    {
//        super.onBackPressed()
        Log.d("-> MAIN_APP: onBackPressed", "")
        wbExchangeSdk.goBack()

//        if (!wbExchangeSdk.goBack())
//        {
//            Log.d("-> MAIN_APP: NO HISTORY onBACK", "")
//            exitSkd()
//        }
    }
    // -----------------------------------------

    private fun exitSkd()
    {
        Log.d("-> MAIN_APP: exitSkd", "")

        val btnShowWebView = findViewById<Button>(R.id.btnAddWebView)
        btnShowWebView.isEnabled = true

        val btnHideWebView = findViewById<Button>(R.id.btnRemoveWebView)
        btnHideWebView.isEnabled = false

        val wbWebViewWrapper = findViewById<LinearLayout>(R.id.wbWebViewWrapper)
        wbWebViewWrapper.removeAllViews()
    }

    // -----------------------------------------
}