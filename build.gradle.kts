import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
    }
}

allprojects {
    group = "me.dm7.barcodescanner"
    version = "1.9.13"

    repositories {
        google()
        mavenCentral()
    }

    extra["isLibrary"] = false
}

subprojects {
    afterEvaluate {
        if (
            plugins.hasPlugin("com.android.application") ||
            plugins.hasPlugin("com.android.library")
        ) {
            extensions.configure<BaseExtension>("android") {
                compileSdkVersion(libs.versions.compileSdk.get().toInt())

                lintOptions {
                    isAbortOnError = false
                }

                defaultConfig {
                    minSdkVersion(libs.versions.minSdk.get().toInt())
                    targetSdkVersion(libs.versions.targetSdk.get().toInt())
                    versionCode = 1913
                    versionName = "1.9.13"
                }
            }
        }
    }
}
