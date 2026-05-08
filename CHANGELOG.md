# Change Log

## [2.0.0] - May  9, 2026

This is a major modernization release that updates the archived project to current Android tooling, AndroidX, Kotlin, AGP 9, JDK 21, local modules, Material Components, and refreshed documentation.

### Project maintenance

- Resumed maintenance of the original archived `dm77/barcodescanner` project under this fork.
- Updated the project to build with modern Android Studio, Gradle, Android Gradle Plugin, and JDK versions.
- Raised the library version from `1.9.13` to `2.0.0`.
- Updated sample app `versionCode` to `2000`.
- Updated `compileSdk` and `targetSdk` to API 36.
- Raised the minimum supported Android version to Android 8.0 / API 26.
- Verified that the library and both ZXing/ZBar showcase apps build and run successfully.
- Verified that QR scanning still works after the dependency, AndroidX, Kotlin, and build system migrations.

### Build system modernization

- Migrated Gradle build scripts from Groovy to Kotlin DSL.
- Migrated `settings.gradle` to `settings.gradle.kts`.
- Replaced the legacy `dependencies.gradle` setup with a Gradle version catalog.
- Replaced root `buildscript` classpath configuration with Gradle plugin aliases from the version catalog.
- Centralized plugin repository configuration in `settings.gradle.kts`.
- Updated project repositories to use `google()` and `mavenCentral()`.
- Configured repository content filtering in `settings.gradle.kts`.
- Moved dependency repository configuration to `settings.gradle.kts` and removed duplicated project-level repository declarations.
- Cleaned up Gradle repository declarations to avoid project-level repository duplication.
- Updated the Foojay toolchain resolver convention plugin to `1.0.0`.
- Removed obsolete Bintray and legacy Maven publishing configuration.
- Removed the old JCenter/Bintray-based dependency flow.
- Removed legacy publication metadata from modules that are no longer published to Maven/Bintray.
- Moved Android SDK configuration out of the root Gradle script and into each individual module.
- Added module-specific `compileSdk`, `minSdk`, `targetSdk`, `versionCode`, and `versionName` configuration where appropriate.
- Added explicit module versions for `:core`, `:zxing`, and `:zbar`.
- Removed legacy Lint suppression configuration after confirming the project builds cleanly.
- Updated `.gitignore` Gradle rules to ignore only the root `.gradle/` cache folder while keeping versioned Gradle files tracked.
- Removed unused `.github` stale issue configuration.

### AGP 9, JDK 21, and Gradle properties

- Upgraded the project to AGP 9 and JDK 21.
- Updated all Java modules to use Java 21.
- Updated Kotlin modules to use JVM target 21.
- Enabled AGP 9 built-in Kotlin support.
- Enabled AGP 9 new DSL support.
- Removed legacy `BuildConfig` defaults.
- Removed Jetifier after completing the AndroidX migration.
- Enabled Gradle configuration cache.
- Enabled non-transitive R classes.
- Enabled final resource IDs.
- Enabled unique package names.
- Enabled optimized resource shrinking.
- Enabled strict R8 keep rules.
- Set Kotlin code style to official.
- Updated Gradle JVM arguments.

### AndroidX and dependency migration

- Fully migrated legacy Android Support imports to AndroidX.
- Migrated legacy Android Support XML widgets to AndroidX and Material Components.
- Replaced legacy Android Support dependencies with AndroidX and Material Components dependencies.
- Removed old `com.android.support` libraries from the version catalog.
- Added explicit AndroidX Annotation dependency for annotation imports such as `@ColorInt` and `@NonNull`.
- Added AndroidX AppCompat, AndroidX Core, AndroidX Fragment, and Material Components dependencies for the migrated sample apps.
- Added AndroidX/Jetifier configuration support during the migration, then removed Jetifier once the project fully used AndroidX.
- Updated ZXing core from `3.3.3` to `3.5.4`.

### Local module setup

- Switched ZXing and ZBar sample apps to use local project modules instead of previously published artifacts.
- Replaced previously published scanner artifacts with local `:core`, `:zxing`, and `:zbar` modules.
- Updated module dependencies so `:zxing` and `:zbar` depend on `:core`.
- Removed the old Maven/JCenter/Bintray dependency flow from the sample setup.

### Manifest and namespace cleanup

- Removed obsolete `package` declarations from AndroidManifest files.
- Moved namespace configuration to Gradle.
- Replaced dynamic sample `applicationId` values based on `project.group` with explicit application IDs.
- Moved camera permission and camera feature declarations to the `:core` library manifest so ZXing and ZBar consumers inherit them through manifest merging.
- Removed duplicated camera permission and camera feature declarations from sample app manifests.
- Set the camera hardware feature as required for the scanner library.
- Set `android:screenOrientation="portrait"` for all activities in both ZBar and ZXing sample manifests.

### Launcher icon updates

- Updated launcher icons to adaptive icons and mipmap resources.
- Added adaptive icon XML definitions for API 26+.
- Migrated launcher icons from `drawable` to `mipmap` resources.
- Added vector background and foreground assets for adaptive icons.
- Added round launcher icon support in AndroidManifest files.
- Replaced legacy PNG launcher icons with WebP assets across all densities.

### Sample apps and View Binding

- Enabled View Binding in the ZXing and ZBar sample modules.
- Refactored ZXing and ZBar sample activities and fragments to use View Binding.
- Updated commented-out camera preview code to use View Binding syntax.
- Refactored `BaseScannerActivity` to accept a `Toolbar` parameter to support View Binding.
- Replaced `findViewById` usage in sample screens with View Binding.
- Standardized XML formatting, attribute ordering, and indentation across sample layout files.
- Updated layouts to consistently use `match_parent` instead of deprecated `fill_parent`.
- Updated scanner layout margins and removed redundant layout weights.

### Kotlin migration

- Added Kotlin support to the sample modules.
- Migrated ZXing and ZBar sample app classes from Java to Kotlin while preserving scanner behavior.
- Converted scanner activities, fragments, dialog fragments, camera selector dialogs, format selector dialogs, and shared base activities to Kotlin.
- Replaced Java-style callbacks, anonymous classes, and switch statements with Kotlin lambdas, safe calls, and `when` expressions.
- Avoided Kotlin non-null assertions and `lateinit` where possible by using nullable properties, safe calls, and local non-null variables.
- Updated delayed scanner preview resume logic to use `Handler(Looper.getMainLooper())`.
- Refactored sample activities and fragments with named arguments for clearer Kotlin call sites.
- Improved null safety across migrated Kotlin sample code.
- Converted `DisplayUtils` from Java to Kotlin.
- Converted `Result` from Java to Kotlin in the ZBar module.
- Converted `CameraUtils` from Java to Kotlin.
- Converted `IViewFinder` to Kotlin and renamed it to `ViewFinder`.

### Package and API structure

- Refactored sample package structure for better organization.
- Reorganized sample classes into clearer packages such as `base`, `fullscanner`, `simplescanner`, `dialog`, and `scannerlistener`.
- Updated AndroidManifest files and imports to reflect the new package structure.
- Extracted dialog listener interfaces into a dedicated package.
- Extracted `ResultHandler` from `ZBarScannerView` and `ZXingScannerView` into standalone interfaces.
- Updated sample activities and fragments to use the new `ResultHandler` locations.
- Removed the legacy `m` prefix from member variables for a cleaner and more modern code style.

### Core scanner cleanup

- Refactored core scanner and preview logic for improved readability.
- Replaced magic numbers with descriptive constants for rotation degrees, alpha values, camera rotation counts, image formats, native library names, and layout divisors.
- Renamed variables for clearer camera, rotation, preview size, image rotation, and layout calculations.
- Replaced generic loop variables such as `i`, `x`, and `y` with clearer names such as `cameraId`, `rotationIndex`, `row`, and `column`.
- Simplified boolean logic in `getFlash`.
- Simplified preview layout calculations in `CameraPreview`.
- Improved variable naming and comment formatting in `CameraUtils`.
- Standardized comment style across `ZXingScannerView`, `ZBarScannerView`, and `CameraPreview`.
- Updated `ZXingScannerView` comments for consistency.
- Replaced legacy anonymous inner classes with lambdas in `ZBarScannerView`, `ZXingScannerView`, and `CameraHandlerThread`.
- Removed obsolete Android SDK version checks for API 13 and KitKat.
- Improved code formatting across the core, ZXing, ZBar, and sample modules.
- Standardized ignored exception handling.

### Fragment and UI modernization

- Replaced legacy `<fragment>` tags with `androidx.fragment.app.FragmentContainerView`.
- Refreshed ZXing and ZBar showcase UI with lightweight Material Components styling.
- Updated sample themes from AppCompat to Material Components.
- Replaced standard buttons with `MaterialButton`.
- Added rounded corners to sample buttons for a cleaner look.
- Fixed toolbar overflow menu styling by adding a dedicated popup theme.
- Updated toolbar and app bar overlays to use Material Components theme overlays.
- Verified that showcase screens still render correctly and scanner functionality remains working after the UI refresh.

### README and documentation

- Updated README installation instructions to reflect the local module setup.
- Replaced obsolete JCenter/Bintray installation instructions.
- Documented how to include `:core`, `:zxing`, and `:zbar` as local modules from a cloned copy of the library project.
- Added notes explaining that `projectDir` paths depend on the user’s local folder structure.
- Updated README usage examples from Java to Kotlin.
- Updated advanced usage links to point to the current Kotlin sample classes.
- Updated screenshot references to use local repository paths instead of remote links to the original project.
- Added a table of contents to the README.
- Updated the README title and description to better describe the project as an Android QR/barcode scanner library.
- Preserved original project credits, archive notice, contributor references, and license information.

## [1.9.13] - February  16, 2019
* Update plugin, build tools and library versions
* Min SDK version is now 14
* Upload artifacts to jcenter/bintray instead of Maven central/Sonatype

## [1.9.8] - August 18, 2017
* One more attempt to fix Nexus 5x portrait scanning problems

## [1.9.7] - August 2, 2017
* Fix everything that was broken since 1.9.5 (Relevant issues: #336, #315, #339, #338)

## [1.9.6] - August 1, 2017
* Revert changes from 1.9.5 as it causes more harm than good (See #336)

## [1.9.5] - July 28, 2017
* Fix incorrectly rotated data on Nexus 5x devices. Thanks to @rramprasad for the pull request #315

## [1.9.4] - July 15, 2017
* Add ability to customize aspect ratio tolerance that is used in figuring out the optimal camera preview size. This is just a temporary fix for #249,#287,#293

## [1.9.3] - May 27, 2017
* Add ability to customize view finder with custom attributes. See #285 for more info. Thanks to @albinpoignot for the pull request

## [1.9.2] - May 13, 2017
* Add support for AZTEC codes in ZXing: #299, #288.

## [1.9.1] - April 8, 2017
* Add ability to scan inverted/negative codes with ZXing. Thanks to @manijshrestha for this pull request #265

## [1.9] - July 25, 2016
* Scale camera preview when the view size isn't full screen. Thanks to @xolan for this pull request PR #219
* Fix inverted camera in devices with differently oriented back and forward facing cameras. Thanks to @thadcodes for PR #191
* Add ability switch view finder view to square. Thanks to @squeeish for PR #163

## [1.8.4] - Dec 30, 2015
* Improve performance by opening camera and handling preview frames in a separate HandlerThread (#1, #99)
* Do not automatically stopCamera after a result is found #115
* Update samples to use Material Theme and make sure all samples use the FullScreen theme
* Update gradle wrapper to v2.10, gradle plugin to v1.5.0, buildToolsVersion to v23.0.2 and targetSdkVersion 23

## [1.8.3] - October 3, 2015
* Rebuild ZBar libraries with position independent code (#123,#119,#94).
