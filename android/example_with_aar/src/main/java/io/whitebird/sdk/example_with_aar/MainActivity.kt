package io.whitebird.sdk.example_with_aar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.whitebird.sdk.exchange.WBExchangeSdk
import io.whitebird.sdk.exchange.WBExchangeSdkMode

class MainActivity : AppCompatActivity()
{
    private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        wbExchangeSdk.setup(
//            merchantId = "merchantId_TEST",      // * required
            merchantId = "4f19017b-0793-4591-94ff-610bb3c4665b",      // * required
//            mode = WBExchangeSdkMode.[!!_select_mode_!!], // * required

            // LoginMode
//            mode = WBExchangeSdkMode.LoginMode,

            // TokensMode
//            mode = WBExchangeSdkMode.TokensMode,
//            accessToken = "...",                 // default = ""
//            refreshToken = "...",                // default = ""

            // AuthMode
            mode = WBExchangeSdkMode.AuthMode,
            onLogin = { accessToken, refreshToken, isUserVerified ->
                Log.d(
                    "-> MAIN_APP: onLogin",
                    "accessToken = ${accessToken.takeLast(10)}, refreshToken = ${refreshToken.takeLast(10)}, isUserVerified = $isUserVerified"
                )
                Toast.makeText(applicationContext, "MAIN_APP: onLogin", Toast.LENGTH_SHORT).show()
            },

            showBackButtonOnHomePage = true,     // default = false
            onExit = {
                Log.d("-> MAIN_APP: onExit", "")

                // android.view.ViewRootImpl$CalledFromWrongThreadException:
                // Only the original thread that created a view hierarchy can touch its views.
                // Expected: main Calling: JavaBridge
                runOnUiThread { // <- !!!
                    // TODO: onExit - back from SDK page
                    Toast.makeText(applicationContext, "TODO: onExit", Toast.LENGTH_SHORT).show()
                }
            },

            disableAddCard = true,
        )
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed()
    {
        Log.d("-> MAIN_APP: onBackPressed", "")

        // wbExchangeSdk.goBack()

        // or
        if (!wbExchangeSdk.goBack())
        {
            Log.d("-> MAIN_APP: NO HISTORY onBACK", "")

            // TODO: back from SDK page

            // or exit from APP
            super.onBackPressed()
        }
    }
}