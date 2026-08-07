import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.dokka)
}

private val documentedModules = mapOf(
    ":core" to "Core",
    ":zxing" to "ZXing",
    ":zbar" to "ZBar"
)

documentedModules.forEach { (projectPath, documentationName) ->
    project(projectPath).run {
        pluginManager.withPlugin("com.android.library") {
            pluginManager.apply("org.jetbrains.dokka")

            extensions.configure<DokkaExtension> {
                dokkaPublications.named("html") {
                    moduleName.set(documentationName)
                    moduleVersion.set(providers.provider { version.toString() })
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
        }
    }
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
