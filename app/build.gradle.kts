plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {

    namespace = "com.example.skyalert"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.skyalert"
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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    testOptions.unitTests {
        // Always show the result of every unit test when running via command line, even if it passes.
        isIncludeAndroidResources = true

    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.04.01")
    implementation(composeBom)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    // Settings
    implementation(libs.androidx.preference)
    // Navigation Components
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    // Retrofit & Gson dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    //Coil
    implementation("io.coil-kt:coil-compose:2.6.0")
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.activity)
    implementation(libs.play.services.maps)
    kapt("androidx.room:room-compiler:2.6.1")

    // Lottie
    implementation("com.airbnb.android:lottie:6.4.0")
    // Google Play Services
    implementation("com.google.android.gms:play-services-location:21.2.0")
    // swipe refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // Encrypted Shared Preferences
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    // open street map
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    // work manager
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui")
    // Material Design 3
    implementation("androidx.compose.material3:material3")
    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation("androidx.compose.material:material-icons-core")
    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Optional - Add window size utils
    implementation("androidx.compose.material3:material3-window-size-class")
    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.8.2")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")


    /**
     *  Testing dependencies
     * */
    // hamcrest
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    // Junit
    testImplementation("junit:junit:4.13.2")
    // Coroutines test dependencies
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    // Robolectric
    testImplementation("org.robolectric:robolectric:4.12")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // AndroidX Test - Instrumented testing
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}