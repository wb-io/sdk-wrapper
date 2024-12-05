plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "io.whitebird.sdk.exchange.example"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.whitebird.sdk.exchange.example"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(project(":wbexchangesdk"))

    // ??
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.activity)
//    implementation(libs.androidx.appcompat)

    // ??
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
}