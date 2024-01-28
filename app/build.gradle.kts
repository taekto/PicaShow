import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "io.b101.picashow"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.b101.picashow"
        minSdk = 26
        targetSdk = 33
        versionCode = 9
        versionName = "1.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Composable 내에서 ViewModel 인스턴스를 가져오기
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0-alpha03")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // ViewModel, LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.4")

    // Room 사용을 위해
    val roomVersion = "2.4.3"
    val activityVersion = "1.3.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.activity:activity-ktx:$activityVersion")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.google.accompanist:accompanist-pager:0.18.0")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //Gson converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    //coil
    implementation ("io.coil-kt:coil-compose:1.4.0")
    // workManager
    implementation("androidx.work:work-runtime-ktx:2.7.0")
    // navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")
    // fragment
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.ui:ui-tooling:1.5.4")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // java.time.YearMonth - API 26부터, 그 미만은 ThreeTenABP 라이브러리 사용
//    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation-layout-android:1.5.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}