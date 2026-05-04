plugins {
    id("com.android.application")
}

dependencies {
    implementation(project(":zbar"))
    implementation(libs.support.v4)
    implementation(libs.appcompat.v7)
    implementation(libs.design.support)
}

android {
    defaultConfig {
        applicationId = "${project.group}.zbar.sample"
    }

    namespace = "me.dm7.barcodescanner.zbar.sample"
}
