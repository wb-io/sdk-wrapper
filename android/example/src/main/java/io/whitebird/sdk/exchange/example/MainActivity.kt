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

        wbExchangeSdk.setup(
            merchantId = "merchantId_TEST",     // required

            // LoginMode
//            mode = WBExchangeSdkMode.LoginMode, // required

            // TokensMode
//            mode = WBExchangeSdkMode.TokensMode,
//            accessToken = "",                     // default = ""
//            refreshToken = "",                    // default = ""

            // ??
//            loginRequired = true,                   // default = false

            // ??
//            showLoaderIfNoToken = true,           // default = false

            // AuthMode
            mode = WBExchangeSdkMode.AuthMode,
            onLogin = { accessToken, isUserVerified ->
                Log.d(
                    "-> MAIN_APP: onLogin",
                    "accessToken = ${accessToken.takeLast(10)}, isUserVerified = $isUserVerified"
                )
            },

            showBackButtonOnHomePage = true,        // default = false
            onExit = {
                Log.d("-> MAIN_APP: onExit", "")
                runOnUiThread { // <- !!!
                    exitSkd()
                }
            }
        )

        // -----------------------------------------
        // WBExchangeSKD:
        // подписываемся на получение токенов из WebView

//        wbExchangeSdk.onChangeTokens { accessToken, refreshToken ->
//            Log.d(
//                "-> MAIN_APP: onChangeTokens",
//                "${accessToken.takeLast(10)}, ${refreshToken.takeLast(10)}"
//            )
//        }

        // подписываемся на событие выхода с Home Page
//        wbExchangeSdk.onExit {
//            Log.d("-> MAIN_APP: onExit", "")
//            runOnUiThread { exitSkd() }
//        }

        // --------------
//        wbExchangeSdk.setShowBackButtonOnHomePage(true) // default = false

        // --------------
        // work with loginRequired
        // если выставить loginRequired лоадер игнорируется
//        wbExchangeSdk.setLoginRequired(true) // default = false

        // --------------
        // work with tokens
        // нужен ли лоадер если еще не передан токен
//        wbExchangeSdk.setShowLoaderIfNoToken(true) // default = false
//        wbExchangeSdk.setLoginRequired(false)
//        setActualTokens()

        // в примере ниже управляем лоадером при помощи переключателя
        // -----------------------------------------

//        val switchLoader = findViewById<SwitchCompat>(R.id.loader)
//        switchLoader.setOnCheckedChangeListener { _, isChecked ->
//            wbExchangeSdk.setShowLoaderIfNoToken(isChecked)
//        }

//        val loginRequired = findViewById<SwitchCompat>(R.id.loginRequired)
//        loginRequired.setOnCheckedChangeListener { _, isChecked ->
//            wbExchangeSdk.setLoginRequired(isChecked)
//        }

//        wbExchangeSdk.setShowLoaderIfNoToken(switchLoader.isChecked) // default = false
//        wbExchangeSdk.setLoginRequired(loginRequired.isChecked) // default = false

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

        // -----------------------------------------

        // кнопками ADD и DEL -> добавляем/удаляем токен из SDK
//        val btnDel = findViewById<Button>(R.id.btnDel)
//        val btnAdd = findViewById<Button>(R.id.btnAdd)

        // уже есть токен - передаем его в wbExchange
//        setActualTokens()
//        btnDel.isEnabled = true
//        btnAdd.isEnabled = false

        // токен выставляем позже - в примере по кнопке ADD
//        btnDel.isEnabled = false
//        btnAdd.isEnabled = true
//
//        btnDel.setOnClickListener {
//            Log.d("-> MAIN_APP: btnDel", "")
//            wbExchangeSdk.setTokens(
//                accessToken = "",
//                refreshToken = ""
//            )
//            btnDel.isEnabled = false
//            btnAdd.isEnabled = true
//        }
//        btnAdd.setOnClickListener {
//            Log.d("-> MAIN_APP: btnAdd", "")
//            setActualTokens()
//            btnDel.isEnabled = true
//            btnAdd.isEnabled = false
//        }
    }

//    private fun setActualTokens()
//    {
//        wbExchangeSdk.setTokens(
//            // new user -> NO offer
////            accessToken = "",
////            refreshToken = ""
//
//            // with offer -> NOT verified
////            accessToken = "",
////            refreshToken = ""
//
//            // verified -> NO test
////            accessToken = "",
////            refreshToken = ""
//
//            // verified -> AND test
//            accessToken = "",
//            refreshToken = ""
//        )
//    }

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
        if (!wbExchangeSdk.goBack())
        {
            Log.d("-> MAIN_APP: NO HISTORY onBACK", "")
            exitSkd()
        }
    }
    // -----------------------------------------

    private fun exitSkd()
    {
        Log.d("-> MAIN_APP: exitSkd", "")

//            val btnDel = findViewById<Button>(R.id.btnDel)
//            val btnAdd = findViewById<Button>(R.id.btnAdd)
//            btnDel.isEnabled = false
//            btnAdd.isEnabled = true

//            val loginRequired = findViewById<SwitchCompat>(R.id.loginRequired)
//            loginRequired.isChecked = false

//            val loginRequired = findViewById<SwitchCompat>(R.id.loginRequired)
//            loginRequired.isChecked = false

        val btnShowWebView = findViewById<Button>(R.id.btnAddWebView)
//            Log.d("-> MAIN_APP: btnShowWebView", "$btnShowWebView")

        // android.view.ViewRootImpl$CalledFromWrongThreadException:
        // Only the original thread that created a view hierarchy can touch its views.
        // Expected: main Calling: JavaBridge
        btnShowWebView.isEnabled = true

        val btnHideWebView = findViewById<Button>(R.id.btnRemoveWebView)
        btnHideWebView.isEnabled = false

        val wbWebViewWrapper = findViewById<LinearLayout>(R.id.wbWebViewWrapper)
//            wbWebViewWrapper.visibility = View.GONE
//            val wbWebView = findViewById<View>(R.id.wbWebView)
//            wbWebView.visibility = View.GONE
//            wbWebViewWrapper.removeView(wbWebView)
        wbWebViewWrapper.removeAllViews()
    }

    // -----------------------------------------
}