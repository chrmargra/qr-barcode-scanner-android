# Change Log

## [3.0.0] - Unreleased

This is a major Kotlin migration and API modernization release. It completes the migration of the library modules from Java to Kotlin, reorganizes the public core package structure, updates the Android build environment, introduces QR code bitmap generation, and adds comprehensive multi-module API documentation.

This release preserves the existing scanner behavior, but contains breaking package and Java interoperability changes that may require source updates.

### Breaking changes

#### Core package reorganization

Several core classes have moved into dedicated packages:

| Version 2.0.0 | Version 3.0.0 |
| --- | --- |
| `me.dm7.barcodescanner.core.CameraHandlerThread` | `me.dm7.barcodescanner.core.camera.CameraHandlerThread` |
| `me.dm7.barcodescanner.core.CameraPreview` | `me.dm7.barcodescanner.core.camera.CameraPreview` |
| `me.dm7.barcodescanner.core.CameraUtils` | `me.dm7.barcodescanner.core.camera.CameraUtils` |
| `me.dm7.barcodescanner.core.CameraWrapper` | `me.dm7.barcodescanner.core.camera.CameraWrapper` |
| `me.dm7.barcodescanner.core.DisplayUtils` | `me.dm7.barcodescanner.core.util.DisplayUtils` |
| `me.dm7.barcodescanner.core.ViewFinder` | `me.dm7.barcodescanner.core.viewfinder.ViewFinder` |
| `me.dm7.barcodescanner.core.ViewFinderView` | `me.dm7.barcodescanner.core.viewfinder.ViewFinderView` |

Kotlin and Java consumers that reference these classes directly must update their imports. The package names of `BarcodeScannerView`, `ZXingScannerView`, `ZBarScannerView`, their result handlers, and ZBar result classes remain unchanged.

#### Java interoperability

The migration to Kotlin removes unnecessary `@JvmField` and `@JvmStatic` annotations. Kotlin call sites remain concise, but Java consumers must update direct static and field access where applicable.

Examples include:

| Previous Java access | Version 3.0.0 Java access |
| --- | --- |
| `BarcodeFormat.QRCODE` | `BarcodeFormat.Companion.getQRCODE()` |
| `BarcodeFormat.getFormatById(id)` | `BarcodeFormat.Companion.getFormatById(id)` |
| `ZXingScannerView.ALL_FORMATS` | `ZXingScannerView.Companion.getALL_FORMATS()` |
| `CameraWrapper.getWrapper(camera, cameraId)` | `CameraWrapper.Companion.getWrapper(camera, cameraId)` |
| `CameraUtils.getCameraInstance()` | `CameraUtils.INSTANCE.getCameraInstance()` |
| `DisplayUtils.getScreenOrientation(context)` | `DisplayUtils.INSTANCE.getScreenOrientation(context)` |
| `cameraWrapper.camera` | `cameraWrapper.getCamera()` |
| `cameraWrapper.cameraId` | `cameraWrapper.getCameraId()` |

The protected `laserPaint`, `finderMaskPaint`, and `borderPaint` members in `ViewFinderView` are now Kotlin properties. Java subclasses must access them through their protected getters and setters.

`CameraHandlerThread`, scanner views, `CameraPreview`, `ViewFinderView`, and `BarcodeFormat` remain extensible.

Nullability is now explicitly represented by the Kotlin API. Kotlin consumers may therefore receive stricter compile-time null-safety checks than with the previous Java platform types.

The `ViewFinder.setLaserEnabled` parameter has been renamed from `isLaserEnabled` to `isEnabled`. Kotlin callers using a named argument must update the parameter name.

#### Version catalogs for local modules

Applications that include `:core`, `:zxing`, or `:zbar` using `projectDir` must expose the version-catalog aliases referenced by those module build scripts.

The `:core` module now uses `androidx-core-ktx` instead of `androidx-annotation`.

See the README section on version catalogs for the complete list of required aliases and generated accessors.

### Kotlin migration

- Completed the migration of all remaining production library sources from Java to Kotlin.
- Migrated `CameraHandlerThread`, `CameraWrapper`, `CameraPreview`, `BarcodeScannerView`, and `ViewFinderView` in the core module.
- Migrated `BarcodeFormat` and `ZBarScannerView` in the ZBar module.
- Migrated `ZXingScannerView` in the ZXing module.
- Preserved the existing camera lifecycle, preview handling, barcode decoding, result delivery, rotation, autofocus, flash, and viewfinder behavior.
- Preserved the original implementation comments where they remain relevant.
- Improved null safety without introducing `lateinit` properties or non-null assertion operators.
- Retained synchronization for framing-rectangle calculations that may be accessed across camera and UI threads.
- Replaced Java utility calls with Kotlin equivalents where they preserve the same semantics.
- Improved parameter names, local variable names, named arguments, expression bodies, and constant naming throughout the migrated code.

### QR code generation

- Added `QRCodeEncoder` to the `:zxing` module.
- Added support for generating QR codes as Android `Bitmap` instances without requiring an Android `Context`.
- Added configurable resolution, foreground color, and background color.
- Added a default resolution of `500 × 500` pixels with black modules and a white background.
- Generated bitmaps use `Bitmap.Config.RGB_565`.
- Added validation for empty values and non-positive resolutions.
- Exposed invalid input and encoding failures through `IllegalArgumentException` and ZXing's `WriterException`, respectively.
- Added KDoc describing parameters, return values, bitmap configuration, validation, and failure behavior.

### Core scanner improvements

- Reorganized camera, display utility, viewfinder, and logging classes into dedicated packages.
- Added `QRBarcodeLogger` as a shared logging utility for the scanner modules.
- Restricted library logging to debug builds and removed direct production logging from ZXing and ZBar scanner views.
- Enabled `BuildConfig` generation in the core module to support debug-only logging.
- Replaced camera rotation magic numbers with descriptive constants.
- Extracted constants for the camera handler thread name, scanner animation values, image formats, and rotation states.
- Improved camera preview sizing and orientation calculations while preserving their existing behavior.
- Improved naming around camera state, preview data, dimensions, rotation, autofocus, flash, and viewfinder drawing.
- Ensured the configured border alpha is applied when a viewfinder is created or recreated.
- Retained the required `@JvmField` declarations for protected `ViewFinderView` properties whose generated setters would otherwise clash with existing JVM method signatures.
- Removed the unused AndroidX Annotation dependency.

### Build system and dependency updates

- Raised the library version from `2.0.0` to `3.0.0`.
- Updated sample application `versionCode` from `2000` to `2001`.
- Updated `compileSdk` from API 36 to API 37.
- Updated the Gradle wrapper from `9.5.0` to `9.6.1`.
- Updated Android Gradle Plugin from `9.2.1` to `9.3.1`.
- Updated Kotlin from `2.3.21` to `2.4.10`.
- Updated Material Components from `1.13.0` to `1.14.0`.
- Added AndroidX Core KTX `1.19.0`.
- Added Dokka `2.2.0`.
- Replaced expanded version-catalog dependency declarations with compact `module = "group:artifact"` notation.
- Updated the version-catalog documentation for applications that consume the library as local modules.

### Sample applications

- Added `QRCodeGeneratorActivity` to the ZXing sample application.
- Added a QR code generator screen with text input, bitmap generation, validation handling, and preview output.
- Added a QR code generator entry point to the ZXing sample main screen.
- Updated the sample manifest, layouts, strings, and screenshots for the new generator.
- Refreshed the ZXing and ZBar screenshots.
- Refactored both sample applications with clearer named arguments, expression bodies, collection handling, parameter names, and loop variable names.
- Improved sample activity, fragment, dialog, and scanner setup readability without changing their behavior.

### Documentation

- Added comprehensive KDoc to the public and protected APIs in `:core`, `:zxing`, and `:zbar`.
- Documented scanner lifecycle, result delivery, supported formats, camera preview behavior, viewfinder customization, utility functions, and QR code generation.
- Added Dokka configuration for generating combined HTML documentation for all three library modules.
- Added generated multi-module API documentation under `docs/` for future publication through GitHub Pages.
- Added the API documentation link to the README.
- Expanded the README with version `3.0.0` migration information.
- Added detailed version-catalog guidance for local module consumers.
- Added QR code generation examples, customization options, error-handling guidance, and a link to the sample implementation.
- Clarified camera permission, runtime permission, and optional camera hardware feature behavior.
- Updated README screenshots and links to reflect the current sample applications.
- Centralized Dokka configuration in the library root project so applications consuming the local modules do not require the Dokka plugin.

## [2.0.0] - May 10, 2026

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
- Added camera permission and camera feature declarations to the `:core` library manifest so consumers inherit them through manifest merging.
- Marked the camera hardware feature as optional with `android:required="false"`.
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

## [1.9.13] - February 16, 2019
- Update plugin, build tools and library versions
- Min SDK version is now 14
- Upload artifacts to jcenter/bintray instead of Maven central/Sonatype

## [1.9.8] - August 18, 2017
- One more attempt to fix Nexus 5x portrait scanning problems

## [1.9.7] - August 2, 2017
- Fix everything that was broken since 1.9.5 (Relevant issues: #336, #315, #339, #338)

## [1.9.6] - August 1, 2017
- Revert changes from 1.9.5 as it causes more harm than good (See #336)

## [1.9.5] - July 28, 2017
- Fix incorrectly rotated data on Nexus 5x devices. Thanks to @rramprasad for the pull request #315

## [1.9.4] - July 15, 2017
- Add ability to customize aspect ratio tolerance that is used in figuring out the optimal camera preview size. This is just a temporary fix for #249,#287,#293

## [1.9.3] - May 27, 2017
- Add ability to customize view finder with custom attributes. See #285 for more info. Thanks to @albinpoignot for the pull request

## [1.9.2] - May 13, 2017
- Add support for AZTEC codes in ZXing: #299, #288.

## [1.9.1] - April 8, 2017
- Add ability to scan inverted/negative codes with ZXing. Thanks to @manijshrestha for this pull request #265

## [1.9] - July 25, 2016
- Scale camera preview when the view size isn't full screen. Thanks to @xolan for this pull request PR #219
- Fix inverted camera in devices with differently oriented back and forward facing cameras. Thanks to @thadcodes for PR #191
- Add ability switch view finder view to square. Thanks to @squeeish for PR #163

## [1.8.4] - December 30, 2015

- Improve performance by opening the camera and handling preview frames in a separate `HandlerThread` (#1, #99).
- Do not automatically stop the camera after a result is found (#115).
- Update samples to use Material Theme and make sure all samples use the FullScreen theme.
- Update Gradle wrapper to 2.10, Gradle plugin to 1.5.0, Build Tools to 23.0.2, and `targetSdkVersion` to 23.

### Migration note

After a successful scan, only the camera preview is stopped. The camera itself is not automatically released.

Applications that previously called `startCamera()` inside `handleResult()` should use `resumeCameraPreview()` instead:

```java
mScannerView.resumeCameraPreview(this);
```

## [1.8.3] - October 3, 2015
- Rebuild ZBar libraries with position independent code (#123,#119,#94).
