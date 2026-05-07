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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
    }
}

kotlin {
    jvmToolchain(jdkVersion = 21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":zbar"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
