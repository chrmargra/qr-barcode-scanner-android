plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

allprojects {
    group = "me.dm7.barcodescanner"
    version = "2.0.0"
    extra["isLibrary"] = false
}
