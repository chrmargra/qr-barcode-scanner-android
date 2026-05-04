plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(
   "core",
    "zxing",
    "zxing-sample",
    "zbar",
    "zbar-sample"
)
