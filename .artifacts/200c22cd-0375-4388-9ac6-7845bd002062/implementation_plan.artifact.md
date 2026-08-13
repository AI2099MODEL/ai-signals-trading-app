# Integrate API Secrets and Update Dependencies

This plan outlines the steps to integrate Shoonya and Dhan API secrets, update Jetpack Navigation 3 and Material Adaptive dependencies, and fix existing build/Kotlin errors.

## User Review Required

> [!IMPORTANT]
> To handle API secrets securely, you must add them to your `local.properties` file. I will provide instructions on how to do this. Do NOT commit these secrets to version control.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///D:/Projects/Stockbreak/MyApplication/gradle/libs.versions.toml)
- Update `navigation3` versions to `1.1.6` (or latest stable if higher).
- Update other dependencies to ensure compatibility.

#### [MODIFY] [gradle.properties](file:///D:/Projects/Stockbreak/MyApplication/gradle.properties)
- Increase JVM heap size to `-Xmx4048m` to fix `OutOfMemoryError` during dex merging.

#### [MODIFY] [app/build.gradle.kts](file:///D:/Projects/Stockbreak/MyApplication/app/build.gradle.kts)
- Enable `buildConfig = true` in `buildFeatures`.
- Add logic to load `SHOONYA_SECRET` and `DHAN_SECRET` from `local.properties` or environment variables.
- Add `buildConfigField` for these secrets.

### Application Logic

#### [NEW] [SecretsConfig.kt](file:///D:/Projects/Stockbreak/MyApplication/app/src/main/java/com/example/myapplication/SecretsConfig.kt)
- Create a configuration object to safely access the secrets in the app code.

#### [MODIFY] [MainActivity.kt](file:///D:/Projects/Stockbreak/MyApplication/app/src/main/java/com/example/myapplication/MainActivity.kt)
- Basic verification that secrets are loaded (e.g., logging or showing a "Secrets Loaded" status, without exposing the actual values).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify the project builds with the new memory settings and secrets integration.
- Run unit tests if any are added.

### Manual Verification
- Verify that `BuildConfig.SHOONYA_SECRET` and `BuildConfig.DHAN_SECRET` are accessible in the code.
