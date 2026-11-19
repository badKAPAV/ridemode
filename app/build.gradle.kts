plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.hilt)
}

val localApiKeys = rootProject.file(".env").takeIf { it.isFile }?.readLines()
    ?.mapNotNull { line ->
        val entry = line.trim()
        if (entry.isEmpty() || entry.startsWith("#") || !entry.contains('=')) null
        else entry.substringBefore('=').trim() to entry.substringAfter('=').trim()
    }?.toMap().orEmpty()

fun quotedBuildValue(value: String): String = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

// Navigation SDK includes the Maps classes; Places transitively requests the standalone artifact.
configurations.configureEach {
    exclude(group = "com.google.android.gms", module = "play-services-maps")
}

android {
    namespace = "com.kapav.ridemode"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.kapav.ridemode"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["NAVIGATION_API_KEY"] = localApiKeys["NAVIGATION_API_KEY"].orEmpty()
        buildConfigField("String", "PLACES_API_KEY", quotedBuildValue(localApiKeys["PLACES_API_KEY"].orEmpty()))
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.activity)
    implementation(libs.compose.material3)
    implementation(libs.phosphor.icons)
    implementation(libs.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.datastore.preferences)
    implementation(libs.hilt.android)
    implementation(libs.google.navigation.sdk)
    implementation(libs.google.places.sdk)
    coreLibraryDesugaring(libs.desugar.nio)
    kapt(libs.hilt.compiler)
    debugImplementation(libs.compose.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    arguments { arg("room.schemaLocation", "$projectDir/schemas") }
}

hilt {
    enableAggregatingTask = false
}
