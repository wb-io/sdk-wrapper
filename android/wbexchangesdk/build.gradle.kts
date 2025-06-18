plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "io.whitebird.sdk.exchange"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

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
        }
        debug {
            // How to Resolve "Cleartext HTTP traffic not permitted"
            // https://www.repeato.app/how-to-resolve-cleartext-http-traffic-not-permitted-error-in-android-8-and-above/
            // https://developer.android.com/build/manage-manifests#kts
            manifestPlaceholders["usesCleartextTraffic"] = true
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
