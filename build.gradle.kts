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
