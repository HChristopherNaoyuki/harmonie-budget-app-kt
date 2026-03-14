// Gradle Scripts/build.gradle.kts (Module :app)
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.harmonie_budget_app_kt"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.harmonie_budget_app_kt"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    // Core AndroidX libraries (already present)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Added for JSON persistence (Gson) - required by JsonHelper.kt
    implementation("com.google.code.gson:gson:2.10.1")

    // Added for RecyclerView in CategoryActivity.kt and ExpenseListActivity.kt
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Test dependencies (unchanged)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}