plugins {
    id("com.android.application")
}

android {
    namespace = "me.dm7.barcodescanner.zbar.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "${project.group}.zbar.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 2000
        versionName = "2.0.0"
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(project(":zbar"))
    implementation(libs.support.v4)
    implementation(libs.appcompat.v7)
    implementation(libs.design.support)
}
