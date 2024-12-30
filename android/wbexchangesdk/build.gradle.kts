plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "io.whitebird.sdk.exchange"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["usesCleartextTraffic"] = false
            buildConfigField("String", "URL_EXCHANGE_SERVER", "\"https://sdk.qa.wbdevel.net/v2.0/\"")
            buildConfigField("Boolean", "SDKLOG_ENABLED", "false")
        }
        debug {
            // How to Resolve "Cleartext HTTP traffic not permitted"
            // https://www.repeato.app/how-to-resolve-cleartext-http-traffic-not-permitted-error-in-android-8-and-above/
            // https://developer.android.com/build/manage-manifests#kts
            manifestPlaceholders["usesCleartextTraffic"] = true
//            buildConfigField("String", "URL_EXCHANGE_SERVER", "\"http://192.168.100.95:3004/v2.0/\"")
//            buildConfigField("String", "URL_EXCHANGE_SERVER", "\"http://192.168.100.95:3004/\"")
             buildConfigField("String", "URL_EXCHANGE_SERVER", "\"https://sdk.qa.wbdevel.net/v2.0/\"")
            buildConfigField("Boolean", "SDKLOG_ENABLED", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // !!!
    // https://developer.android.com/topic/libraries/view-binding
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
//    implementation(libs.androidx.viewbinding)
//    implementation(libs.androidx.constraintlayout)
    implementation("androidx.databinding:viewbinding:8.6.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ??
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)

    // ??
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
}
