import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
}

version = "3.0.0"

android {
    namespace = "me.dm7.barcodescanner.zxing"
    compileSdk {
        version = release(version = 37)
    }

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(jdkVersion = 21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    api(project(":core"))
    api(libs.zxing.core)
}

dokka {
    dokkaPublications.html {
        moduleName.set("ZXing")
        moduleVersion.set(project.version.toString())
    }

    dokkaSourceSets.configureEach {
        documentedVisibilities.set(
            setOf(
                VisibilityModifier.Public,
                VisibilityModifier.Protected
            )
        )
    }
}
