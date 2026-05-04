plugins {
    id("com.android.library")
}

@Suppress(names = ["UNCHECKED_CAST"])
val libraries = rootProject.extra["libraries"] as Map<String, Any>

extra["isLibrary"] = true
extra["pomPackaging"] = "aar"
extra["pomArtifactId"] = "core"
extra["pomName"] = "Barcode Scanner View"
extra["pomDescription"] = "An android library project which contains the core barcode scanner view"

dependencies {
    implementation(libraries["support_v4"].toString())
}

android {
    namespace = "me.dm7.barcodescanner.core"
}
