plugins {
    id("com.android.application")
}

@Suppress(names = ["UNCHECKED_CAST"])
val libraries = rootProject.extra["libraries"] as Map<String, Any>

dependencies {
    implementation(project(":zbar"))
    implementation(libraries["support_v4"].toString())
    implementation(libraries["appcompat_v7"].toString())
    implementation(libraries["design_support"].toString())
}

android {
    defaultConfig {
        applicationId = "${project.group}.zbar.sample"
    }

    namespace = "me.dm7.barcodescanner.zbar.sample"
}
