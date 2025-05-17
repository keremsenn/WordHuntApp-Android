plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.keremsen.wordmaster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.keremsen.wordmaster"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.firebase.firestore.ktx)
    val nav_version = "2.8.9"
    //flowrom
    implementation (libs.androidx.foundation)
    //gson
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json.v163)
    //firebase
    implementation (libs.firebase.auth.ktx)
    implementation (libs.play.services.auth)
    implementation (libs.play.services.location)
    //firestore
    implementation (libs.firebase.firestore.ktx.v2490)
    // LiveData için
    implementation (libs.androidx.lifecycle.livedata.ktx)
    // LiveData'nın Compose ile entegrasyonu için
    implementation (libs.androidx.runtime.livedata)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.analytics)

    //navigation
    implementation(libs.androidx.navigation.compose)

    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation(libs.kotlinx.serialization.json)
    //view model
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    //animation
    implementation (libs.lottie.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}