import com.android.build.gradle.BaseExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

allprojects {
    group = "me.dm7.barcodescanner"
    version = "2.0.0"
    extra["isLibrary"] = false
}

subprojects {
    afterEvaluate {
        if (
            plugins.hasPlugin("com.android.application") ||
            plugins.hasPlugin("com.android.library")
        ) {
            extensions.configure<BaseExtension>("android") {
                compileSdkVersion(apiLevel = 36)

                lintOptions {
                    isAbortOnError = false
                }

                defaultConfig {
                    minSdkVersion(26)
                    targetSdkVersion(36)
                    versionCode = 2000
                    versionName = "2.0.0"
                }
            }
        }
    }
}
