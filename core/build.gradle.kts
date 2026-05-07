plugins {
    alias(libs.plugins.android.library)
}

version = "2.0.0"

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
