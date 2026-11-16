import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "me.dm7.barcodescanner.zxing.sample"
    compileSdk {
        version = release(version = 37)
    }

    defaultConfig {
        applicationId = "me.dm7.barcodescanner.zxing.sample"
        minSdk = 26
        targetSdk = 37
        versionCode = 2002
        versionName = "4.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(jdkVersion = 21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":zxing"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
