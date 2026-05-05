plugins {
    id("com.android.library")
}

android {
    namespace = "me.dm7.barcodescanner.zbar"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    api(project(":core"))
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
