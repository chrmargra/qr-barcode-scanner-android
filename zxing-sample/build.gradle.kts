plugins {
    id("com.android.application")
}

dependencies {
    implementation(project(":zxing"))
    implementation(libs.support.v4)
    implementation(libs.appcompat.v7)
    implementation(libs.design.support)
}

android {
    defaultConfig {
        applicationId = "${project.group}.zxing.sample"
    }

    namespace = "me.dm7.barcodescanner.zxing.sample"
}
