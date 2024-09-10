package io.whitebird.sdk.example_with_aar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.whitebird.sdk.exchange.WBExchangeSdk

class MainActivity : AppCompatActivity() {

    private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        wbExchangeSdk.setShowLoaderIfNoToken(true) // default = false

        wbExchangeSdk.setTokens(
            accessToken = "accessToken...",
            refreshToken = "refreshToken..."
        )
    }
}