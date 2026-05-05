plugins {
    id("com.android.library")
}

android {
    namespace = "me.dm7.barcodescanner.zxing"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    api(project(":core"))
    api(libs.zxing.core)
}
