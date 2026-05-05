plugins {
    id("com.android.library")
}

extra["isLibrary"] = true
extra["pomPackaging"] = "aar"
extra["pomArtifactId"] = "zbar"
extra["pomName"] = "ZBar Scanner View"
extra["pomDescription"] = "An android library project which contains the zbar barcode scanner view"

android {
    namespace = "me.dm7.barcodescanner.zbar"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    api(project(":core"))
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
