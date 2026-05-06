plugins {
    id("com.android.library")
}

android {
    namespace = "me.dm7.barcodescanner.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    implementation(libs.androidx.annotation)
}
