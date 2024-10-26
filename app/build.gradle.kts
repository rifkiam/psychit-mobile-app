plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.psychika"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.psychika"
        minSdk = 24
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
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "BASE_URL_PSYCHIKA", "\"http://34.34.223.17:3000/\"")
        buildConfigField("String", "BASE_URL_CLASSIFICATION", "\"https://psychika.sleepingowl.my.id/\"")
        buildConfigField("String", "BASE_URL_MAPS_NEARBY", "\"https://maps.googleapis.com/maps/api/place/nearbysearch/\"")
        buildConfigField("String", "HOSPITAL_API_KEY", "\"AIzaSyC1fNwFxe1cmbkbOnaFeThzGglQlO7j6KU\"")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Circle Image View
    implementation(libs.circleimageview)

    // Firebase
    implementation(libs.firebase.auth) // Auth
    implementation(libs.play.services.auth) // Google Auth

    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // LiveCycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Glide
    implementation(libs.glide)

    // uCrop
    implementation(libs.ucrop)
    implementation(libs.ucrop.v228native)

    // Camera X
    implementation(libs.androidx.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Maps
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)

    // Dots Progress Bar
    implementation(libs.android.loading.dots)
}