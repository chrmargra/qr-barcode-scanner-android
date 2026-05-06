import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "me.dm7.barcodescanner.zbar.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "me.dm7.barcodescanner.zbar.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 2000
        versionName = "2.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":zbar"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
