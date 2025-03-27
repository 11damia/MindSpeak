plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "cat.dam.mindspeak"
    compileSdk = 35

    defaultConfig {
        applicationId = "cat.dam.mindspeak"
        minSdk = 28
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
    implementation (libs.patrykandpatrick.compose)
    implementation (libs.compose.m3)
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.storage)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Firebase
    implementation (libs.firebase.auth)
    implementation (libs.firebase.auth.ktx)
    implementation (libs.firebase.firestore.ktx)
    implementation (libs.firebase.database.ktx)

    // Supabase
    implementation (libs.io.github.jan.tennert.supabase.postgrest.kt)
    implementation (libs.storage.kt)
    implementation (libs.supabase.kt)
    implementation (libs.kotlinx.coroutines.android)

    // Coroutines support for Firebase tasks
    implementation (libs.jetbrains.kotlinx.coroutines.play.services)

    // Coil per carregar imatges de manera asíncrona
    implementation(libs.coil.compose)


    // Images icons externals
    implementation (libs.androidx.material.icons.extended)

    // Ktor http
    implementation (libs.ktor.client.android)  
    implementation (libs.ktor.client.content.negotiation)
    implementation (libs.ktor.serialization.kotlinx.json)

    // Accompanist
    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.28.0")

    // ExoPlayer

    implementation ("androidx.media3:media3-exoplayer:1.6.0")
    implementation ("androidx.media3:media3-ui:1.6.0")

    // Glide for image loading
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}