plugins {
    id("com.android.library")
}

extra["isLibrary"] = true
extra["pomPackaging"] = "aar"
extra["pomArtifactId"] = "zxing"
extra["pomName"] = "ZXing Scanner View"
extra["pomDescription"] = "An android library project which contains the zxing barcode scanner view"

dependencies {
    api(project(":core"))
    api(libs.zxing.core)

}

android {
    namespace = "me.dm7.barcodescanner.zxing"
}
