package io.whitebird.sdk.exchange.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.whitebird.sdk.exchange.WBExchangeSdk

class MainActivity : AppCompatActivity() {

    // -----------------------------------------
    // WBExchangeSKD:
    private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance() }
    // -----------------------------------------

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("-> MAIN: onCreate", "")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // -----------------------------------------
        // WBExchangeSKD:

        // нужен ли лоадер если еще не передан токен
//        wbExchangeSdk.setShowLoaderIfNoToken(true) // default = false

        // если выставить loginRequired лоадер игнорируется
//        wbExchangeSdk.setLoginRequired(true) // default = false

        // в примере ниже управляем лоадером при помощи переключателя
        // -----------------------------------------

        val switchLoader = findViewById<SwitchCompat>(R.id.loader)
        val loginRequired = findViewById<SwitchCompat>(R.id.loginRequired)

        switchLoader.setOnCheckedChangeListener { _, isChecked ->
            wbExchangeSdk.setShowLoaderIfNoToken(isChecked)
        }

        loginRequired.setOnCheckedChangeListener { _, isChecked ->
            wbExchangeSdk.setLoginRequired(isChecked)
        }

        wbExchangeSdk.setShowLoaderIfNoToken(switchLoader.isChecked) // default = false
        wbExchangeSdk.setLoginRequired(loginRequired.isChecked) // default = false

        // -----------------------------------------

        // кнопками ADD и DEL -> добавляем/удаляем токен из SDK
        val btnDel = findViewById<Button>(R.id.btnDel)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        // уже есть токен - передаем его в wbExchange
//        setActualTokens()
//        btnDel.isEnabled = true
//        btnAdd.isEnabled = false

        // токен выставляем позже - в примере по кнопке ADD
        btnDel.isEnabled = false
        btnAdd.isEnabled = true

        btnDel.setOnClickListener {
            Log.d("-> MAIN: btnDel", "")
            wbExchangeSdk.setTokens(
                accessToken = "",
                refreshToken = ""
            )
            btnDel.isEnabled = false
            btnAdd.isEnabled = true
        }
        btnAdd.setOnClickListener {
            Log.d("-> MAIN: btnAdd", "")
            setActualTokens()
            btnDel.isEnabled = true
            btnAdd.isEnabled = false
        }
    }

    private fun setActualTokens() {
        wbExchangeSdk.setTokens(
            accessToken = "accessToken...",
            refreshToken = "refreshToken..."
        )
    }

    // -----------------------------------------
    // WBExchangeSKD:
    // если необходимо управлять кнопкой BACK в webview нашего SDK
    // то это делается из главного Activity в методе onBackPressed
    // если SDK обработал BACK -> wbExchange.goBack() - возвращает true
    // -----------------------------------------
//    @Deprecated(
//        "Deprecated in Java",
//        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
//    )
//    override fun onBackPressed() {
//        if (!wbExchangeSdk.goBack()) super.onBackPressed()
//    }
    // -----------------------------------------
}