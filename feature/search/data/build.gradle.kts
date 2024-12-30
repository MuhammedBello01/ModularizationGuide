plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)

    //alias(libs.plugins.dagger)
    //id("kotlin-kapt")

    alias(libs.plugins.hilt) // Hilt Plugin
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.search.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    implementation(platform(libs.androidx.compose.bom))

    implementation(project(":feature:search:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(project(":feature:search:domain"))
    implementation(project(":feature:search:domain"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.convertor)

    //implementation(libs.dagger.hilt)
    //kapt(libs.dagger.kapt)
    //ksp(libs.dagger.kapt)
    implementation(libs.hilt.android) // Hilt Android library
    ksp(libs.hilt.compiler)
    implementation(libs.compose.runtime)

}