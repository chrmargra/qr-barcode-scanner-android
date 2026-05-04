plugins {
    id("com.android.library")
}

@Suppress(names = ["UNCHECKED_CAST"])
val libraries = rootProject.extra["libraries"] as Map<String, Any>

extra["isLibrary"] = true
extra["pomPackaging"] = "aar"
extra["pomArtifactId"] = "zxing"
extra["pomName"] = "ZXing Scanner View"
extra["pomDescription"] = "An android library project which contains the zxing barcode scanner view"

dependencies {
    api(project(":core"))
    api(libraries["zxing_core"].toString())
}

android {
    namespace = "me.dm7.barcodescanner.zxing"
}
