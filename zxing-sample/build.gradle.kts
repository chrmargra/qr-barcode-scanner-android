plugins {
    id("com.android.application")
}

android {
    namespace = "me.dm7.barcodescanner.zxing.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "me.dm7.barcodescanner.zxing.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 2000
        versionName = "2.0.0"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":zxing"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
