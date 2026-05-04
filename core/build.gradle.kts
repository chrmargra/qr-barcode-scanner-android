plugins {
    id("com.android.library")
}

extra["isLibrary"] = true
extra["pomPackaging"] = "aar"
extra["pomArtifactId"] = "core"
extra["pomName"] = "Barcode Scanner View"
extra["pomDescription"] = "An android library project which contains the core barcode scanner view"

dependencies {
    implementation(libs.support.v4)
}

android {
    namespace = "me.dm7.barcodescanner.core"
}
