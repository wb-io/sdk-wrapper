# sdk-wrapper

1. [Android Guide](#white-bird-exchange-sdk-for-android)
2. [iOS Guide](#white-bird-exchange-sdk-for-ios)

# White Bird Exchange SDK for Android

This document provides instructions for integrating the White Bird Exchange SDK into your Android application.

## Project Structure

The project contains several modules:

- **wbexchangesdk** - The SDK module
- **example** - Application for SDK development and testing
- **example_with_aar** - Example application using the SDK as an AAR file

You can run any module using Android Studio, for example, the `example` module.

## Building the AAR Module

You can build the AAR module in two ways:

1. **From Android Studio menu**:
    - The AAR file will be created at `build/outputs/aar/`
    - Note: This method may only create the debug version

2. **From command line** (recommended):
   ```
   ./gradlew build -p wbexchangesdk
   ```
    - This creates both debug and release versions

**Debug version** - allows loading from local server via HTTP  
**Release version** - only allows HTTPS connections

After building, rename the file to match your version, e.g., `wbexchangesdk-0.1.0.aar`

## Integration Using AAR

1. Place the `wbexchangesdk-0.1.0.aar` file in your app's `libs` folder

2. Modify the `build.gradle.kts` file for the module containing the AAR:
   ```kotlin
   dependencies {
       implementation(files("libs/wbexchangesdk-0.1.0.aar"))
       
       // Required transitive dependencies
       implementation("androidx.databinding:viewbinding:8.6.0")
       implementation("androidx.constraintlayout:constraintlayout:2.1.4")
   }
   ```

3. Add `WBExchangeView` to your layout XML file:
   ```xml
   <io.whitebird.sdk.exchange.WBExchangeView
       android:layout_width="match_parent"
       android:layout_height="match_parent" />
   ```

4. Initialize the SDK in your activity:
   ```kotlin
   class SomeActivity : AppCompatActivity() {
       private val wbExchangeSdk by lazy { WBExchangeSdk.getInstance() }
   
       override fun onCreate() {
   
           wbExchangeSdk.setup(
               merchantId = "merchantId_TEST",      // * required
               merchantPass = "your_merchant_pass",  // optional
               environment = WBExchangeEnvironment.DEV, // DEV, QA, or PROD
               logEnabled = true,
               
               // Choose one mode:
               
               // LoginMode
               mode = WBExchangeSdkMode.LoginMode,
               
               // OR TokensMode
               // mode = WBExchangeSdkMode.TokensMode,
               // accessToken = "...",                 // default = ""
               // refreshToken = "...",                // default = ""
               
               // OR AuthMode
               // mode = WBExchangeSdkMode.AuthMode,
               onLogin = { accessToken, refreshToken, isUserVerified ->
                   // App code to handle login
               },
               
               showBackButtonOnHomePage = true,     // default = false
               onExit = {
                   runOnUiThread { // <- IMPORTANT!
                   // код основного приложения при выходе из SDK
                   // вызывается при выходе с главной страницы SDK по нажатию на кнопку Back,
                   // либо если в коде вызван метод wbExchangeSdk.goBack() и если SDK был на главной странице
                   // - вызывается данный onExit
                   }
               },
               
               disableAddCard = true,
           )
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
    // а также сработает onExit описанный выше
    // -----------------------------------------
    @Deprecated(
       "Deprecated in Java",
       ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
       wbExchangeSdk.goBack()
    }
   }
   ```
   See full example: `android/example/src/main/java/io/whitebird/sdk/exchange/example/MainActivity.kt`

### Minimum Platform Version

The minimum Android platform version for the SDK is API 24 (Android 7.0).

For more information on Android platform versions, see:
[https://developer.android.com/tools/releases/platforms](https://developer.android.com/tools/releases/platforms)

# White Bird Exchange SDK for iOS

[iOS doc](https://docs.google.com/document/d/1btj8dgdH5SBS8iWQnp9IW_iYoMiD2YVvG8TM5lxpzuc)
