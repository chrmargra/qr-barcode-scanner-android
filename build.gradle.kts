plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(project(":core"))
    dokka(project(":zxing"))
    dokka(project(":zbar"))
}

dokka {
    dokkaPublications.html {
        moduleName.set("QR Barcode Scanner Android")
        moduleVersion.set("3.0.0")
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }
}
