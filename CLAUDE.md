# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

StickerCamera is an Android app for taking/picking a photo, cropping it square, applying GPU filters, then overlaying movable/scalable/rotatable stickers and positional tags before saving locally. It started as a 2015-era project but the build toolchain and dependencies have since been **modernized** (AGP 8 / AndroidX / Java 17 — see below). The *app logic* is still legacy in places (e.g. legacy `android.hardware.Camera`, no runtime-permission handling), so watch for old patterns even though the toolchain is current.

## Build & Run

Gradle multi-module project driven by the wrapper. The toolchain is current:

- Android Gradle Plugin **8.11.1**, Gradle **8.13**, `compileSdk 36`, `minSdk 26`, `targetSdk 36`.
- **Java 17** source/target. No Retrolambda (lambdas run natively).
- ButterKnife 10.x's annotation processor needs JDK-internal javac APIs; JDK 16+ encapsulates these, so `app/build.gradle` opens them via `--add-exports`/`--add-opens` on `JavaCompile` tasks. Keep that block if you touch the build script.

```bash
./gradlew assembleDebug                  # build debug APK -> app/build/outputs/apk/
./gradlew installDebug                    # build + install on a connected device/emulator
./gradlew :app:compileDebugJavaWithJavac  # fast Java-only compile check
./gradlew clean
./gradlew lint                            # Android lint
./gradlew connectedAndroidTest            # instrumentation tests (require a device/emulator)
```

There are effectively no real tests — `app/src/androidTest/.../ApplicationTest.java` is the empty Android Studio stub. Verify changes by running the app on a device.

## Modules

`settings.gradle` includes two modules:

- **`:app`** — the application (package `com.github.skykai.stickercamera`; app classes live under `com.stickercamera`, `com.common`, `com.customview`).
- **`:ImageViewTouch`** — vendored zoomable/pannable image view, used as `com.imagezoom.ImageViewTouch`. The sticker overlay extends this. **Kept vendored on purpose**: the app's `MyImageViewDrawableOverlay`/`MyHighlightView` override many of `ImageViewTouchBase`'s protected methods, and this fork is repackaged to `com.imagezoom`, so it cannot be swapped for the upstream `it.sephiroth.android.library.imagezoom` Maven artifact without rewriting the overlay.

GPU filters come from the **Maven dependency** `jp.co.cyberagent.android:gpuimage:2.1.0` (wasabeef fork), not a vendored module. Note its package layout: core classes (`GPUImageView`, `GPUImage`, `GPUImageRenderer`) are in `jp.co.cyberagent.android.gpuimage`, while all filter classes (`GPUImageFilter`, `GPUImageToneCurveFilter`, …) are in the **`jp.co.cyberagent.android.gpuimage.filter`** subpackage.

## Architecture

### Screen flow
`MainActivity` (gallery of saved creations; opens camera automatically when none exist) → `CameraActivity` (capture) or `AlbumActivity` (pick) → `CameraManager.processPhotoItem()` routes by aspect ratio: square images go straight to `PhotoProcessActivity`, others to `CropPhotoActivity` first. `PhotoProcessActivity` is the editor; `EditTextActivity` is a sub-screen for entering tag text.

### Core singletons
- **`App`** (Application, registered in manifest) — initializes Universal Image Loader, caches `DisplayMetrics`, and exposes `dp2px`/`px2dp`, screen size, and `getApp()`. Reach global state through `App.getApp()`.
- **`CameraManager`** — opens the camera and keeps a `Stack<Activity>` of camera-flow activities so the whole flow can be `close()`d at once. `CameraBaseActivity` auto-registers/unregisters with it; editor activities should extend it.
- **`EffectService`** — supplies the ordered list of `FilterEffect`s shown in the filter bar.

### Photo editor (`PhotoProcessActivity`) — three independent overlay systems
1. **Filters**: a `GPUImageView` renders the bitmap. The app uses only the "原始" (NORMAL) pass and tone-curve `.acv` presets in `app/src/main/res/raw/`. `GPUImageFilterTools` is trimmed to exactly those (`FilterType` enum is NORMAL + `ACV_*`; each `ACV_*` maps to an `R.raw.*` curve via `GPUImageToneCurveFilter`). To add a filter: drop the `.acv`, add a `FilterType`, wire it in `GPUImageFilterTools`, and register it in `EffectService.getLocalFilters()`.
2. **Stickers**: `MyImageViewDrawableOverlay` (extends `ImageViewTouch`) hosts `MyHighlightView` handles drawing `StickerDrawable`s. `EffectUtil` adds/clears stickers and holds the static `addonList` (sticker drawables `R.drawable.sticker1..8`) plus the live overlay state. Add stickers by extending `addonList`.
3. **Tags**: `LabelView` (a positioned `TagItem`) placed via `LabelSelector`; tag text is edited in `EditTextActivity`.

### Persistence
No database. Saved creations are a `List<FeedItem>` serialized to JSON with **fastjson** and stored in SharedPreferences under `AppConstants.FEED_INFO` (via `DataUtils`). Output images are written through `FileUtils` (`getPhotoSavedPath()`), which uses **app-specific external storage** (`Context.getExternalFilesDir(...)`) — this works on all API levels without a storage permission, required because direct writes to public external storage fail under scoped storage / `targetSdk 36`. Universal Image Loader's disk cache uses `AppConstants.APP_IMAGE` and falls back to internal cache when external storage is unavailable. `largeHeap` is enabled because full-res bitmaps are processed in memory.

### Cross-cutting conventions
- **AndroidX everywhere** — there are no `android.support.*` imports left.
- **`BaseActivity`** (extends `AppCompatActivity`) is the UI base: edge-to-edge via `WindowInsets` (the old SystemBarTint was removed), optional `CommonTitleBar` wiring, and dialog/toast/progress helpers delegated to `ActivityHelper` → `DialogHelper` (toasts are always posted via `runOnUiThread`). New activities should extend `BaseActivity` (or `CameraBaseActivity` inside the camera flow).
- **ButterKnife 10.x** for view injection — `@BindView`/`@OnClick` + `ButterKnife.bind(this)` (the modern API).
- **EventBus (greenrobot 3.x)** for loosely-coupled events between the editor and the main gallery — `@Subscribe`-annotated handlers (e.g. notifying that a new creation was saved).
- Lists use AndroidX **RecyclerView**; FABs use Material **FloatingActionButton**.
- The camera uses the legacy `android.hardware.Camera` API; `CameraHelper` selects `CameraHelperGB` vs `CameraHelperBase` by SDK level. **There is still no runtime-permission handling** (permissions are manifest-only) — a real gap under `targetSdk 36` for `CAMERA`; storage was sidestepped by writing to app-specific dirs instead of requesting `WRITE_EXTERNAL_STORAGE`.
- Reusable, app-agnostic helpers live in `com.common.util` (vendored Trinea android-common utils, pruned to only what the app uses); custom widgets/drawables live in `com.customview`.
