apply(from = "dependencies.gradle")

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
        val pluginContainer = plugins

        if (
            pluginContainer.hasPlugin("com.android.application") ||
            pluginContainer.hasPlugin("com.android.library")
        ) {
            extensions.configure<com.android.build.gradle.BaseExtension>("android") {
                compileSdkVersion((rootProject.extra["versions"] as Map<*, *>)["compile_sdk"] as Int)

                lintOptions {
                    isAbortOnError = false
                }

                defaultConfig {
                    minSdkVersion((rootProject.extra["versions"] as Map<*, *>)["min_sdk"] as Int)
                    targetSdkVersion((rootProject.extra["versions"] as Map<*, *>)["target_sdk"] as Int)
                    versionCode = 1913
                    versionName = "1.9.13"
                }
            }
        }
    }
}
