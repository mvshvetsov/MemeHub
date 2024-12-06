plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.jetbrains.kotlin.plugin.parcelize)
}

android {
    namespace = "ru.shvetsov.memehub"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.shvetsov.memehub"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.media3.ui)
    //retrofit
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3)
    //room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    //hilt
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    //glide
    implementation(libs.glide)
    ksp(libs.glide.compiler)
    //lottie
    implementation(libs.lottie)
    //compressor (photo)
    implementation(libs.id.zelory.compressor)
    //exoPlayer
    implementation(libs.androidx.media3.exoplayer)
}