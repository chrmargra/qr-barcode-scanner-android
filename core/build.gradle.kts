plugins {
    id("com.android.library")
}

extra["isLibrary"] = true
extra["pomPackaging"] = "aar"
extra["pomArtifactId"] = "core"
extra["pomName"] = "Barcode Scanner View"
extra["pomDescription"] = "An android library project which contains the core barcode scanner view"

android {
    namespace = "me.dm7.barcodescanner.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(libs.support.v4)
}
