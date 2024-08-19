plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.example"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}
dependencies {
    // Firebase BoM (Bill of Materials) for version management
    implementation(platform(libs.firebase.bom))
    // Firebase Authentication for managing authentication
    implementation(libs.firebase.auth.ktx)
    // Google Play Services Auth for Google authentication
    implementation ("com.google.firebase:firebase-auth:21.0.5")
    implementation ("com.google.android.gms:play-services-auth:20.4.0")
    implementation(libs.androidx.core.ktx)
    // AppCompat library for backward compatibility
    implementation(libs.androidx.appcompat)
    implementation (libs.google.firebase.auth.ktx)
    // Google Sign-In
    implementation (libs.play.services.auth.v2120)
    // Material Design Components
    implementation(libs.material)

    // AndroidX Activity library
    implementation(libs.androidx.activity)

    // ConstraintLayout for flexible layouts
    implementation(libs.androidx.constraintlayout)

    // LiveData and ViewModel libraries
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Navigation Component libraries
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.legacy.support.v4)

    // Unit testing library
    testImplementation(libs.junit)

    // AndroidX JUnit and Espresso libraries for testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

