# ELXVRO Scan v2.0 Reference UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rebuild ELXVRO Scan v2.0.0 so the working Android UI closely matches the approved reference artwork while preserving and extending scanning functionality.

**Architecture:** Jetpack Compose hosts the entire visual layer with shared design tokens and a fixed root navigation shell. CameraX/ML Kit remain the scanner engine, while existing domain utilities (history, QR generation, smart actions, duplicate guard) are reused or minimally extended behind composable screens.

**Tech Stack:** Kotlin 1.9.24, AGP 8.5.2, Jetpack Compose, CameraX, ML Kit Barcode Scanning, ZXing, SharedPreferences-backed existing store, JUnit 4.

**Spec:** `docs/superpowers/specs/2026-10-02-v2-reference-ui-design.md`

## Global Constraints
- Reference artwork is the binding visual target.
- One visual theme only: ink / white / electric-blue.
- Bottom navigation is identical across all four root screens.
- Use centralized design tokens; no per-screen random colors/radii/paddings.
- Preserve v1 history/favorites/settings data.
- Version 2.0.0, versionCode 20.
- CI must pass `testDebugUnitTest` and `assembleDebug`.

## Review Focus
- Compact-screen overflow: every root screen scrolls or constrains content safely.
- Safe URL opening: only HTTP/HTTPS may auto-open.
- V1 history/settings remain readable after upgrade.
- Camera permission denial still allows gallery scanning.
- Repeated identical scans inside cooldown are ignored.

---

### Task 1: Behavior contracts for v2 additions
**Files:**
- Create: `app/src/test/java/com/elxvro/scan/V2BehaviorTest.kt`
- Create: `app/src/main/java/com/elxvro/scan/V2Policy.kt`

**Interfaces:**
- Produces `V2Policy.isSafeAutoOpen(raw: String): Boolean`
- Produces `V2Policy.normalizeCooldownMs(seconds: Int): Long`

- [ ] Write failing tests for HTTP/HTTPS allow, custom-scheme deny, and cooldown bounds.
- [ ] Run `gradle testDebugUnitTest --stacktrace` and verify RED.
- [ ] Implement minimal `V2Policy`.
- [ ] Run unit tests and verify GREEN.

### Task 2: Compose build + shared design system
**Files:**
- Modify: `app/build.gradle.kts`
- Create: `app/src/main/java/com/elxvro/scan/ui/theme/ElxvroTheme.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/theme/ElxvroTokens.kt`

**Interfaces:**
- Produces shared colors, dimensions, typography, shapes and `ElxvroScanTheme`.

- [ ] Enable Compose and add Compose dependencies compatible with Kotlin 1.9.24.
- [ ] Encode spec token values centrally.
- [ ] Build and verify compilation.

### Task 3: Replace programmatic View shell with reference-accurate Compose shell
**Files:**
- Replace: `app/src/main/java/com/elxvro/scan/MainActivity.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/AppShell.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/Components.kt`

**Interfaces:**
- Produces fixed four-item bottom navigation, dark header, shared buttons/cards/chips.

- [ ] Implement edge-to-edge host and fixed root navigation.
- [ ] Implement reusable header, bottom nav, cards, segmented chips, switches and buttons from tokens.
- [ ] Verify compile.

### Task 4: Reference scan screen and result sheet
**Files:**
- Create: `app/src/main/java/com/elxvro/scan/ui/ScanScreen.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/ScanResultSheet.kt`
- Modify: `app/src/main/java/com/elxvro/scan/ScannerOverlayView.kt` only if needed for interop.

**Interfaces:**
- Camera preview + gallery + flash + zoom/tap focus.
- Result bottom sheet with semantic primary action, copy/share/favorite/product search.

- [ ] Implement camera surface matching reference proportions.
- [ ] Add 1x/2x zoom controls, gallery/focus/flash controls.
- [ ] Preserve duplicate guard and local feedback.
- [ ] Show results in white bottom sheet instead of AlertDialog.
- [ ] Gate auto-open with `V2Policy`.
- [ ] Verify compile and behavior tests.

### Task 5: Create, History and Settings screens
**Files:**
- Create: `app/src/main/java/com/elxvro/scan/ui/CreateScreen.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/HistoryScreen.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/SettingsScreen.kt`
- Modify: `app/src/main/java/com/elxvro/scan/QrPayloadBuilder.kt`
- Modify: `app/src/test/java/com/elxvro/scan/QrPayloadBuilderTest.kt`

**Interfaces:**
- QR types: URL, text, Wi-Fi, contact, phone, email, SMS, location, calendar.
- History: search/filter/favorites/delete/share.
- Settings: sound, vibration, flash default, quick launch, auto-copy, safe auto-open, cooldown, clear/export/privacy/about.

- [ ] Add failing payload tests for SMS/location/calendar.
- [ ] Verify RED, implement payloads, verify GREEN.
- [ ] Implement Create screen to reference layout.
- [ ] Implement History/Favorites list and compact filter chips.
- [ ] Implement grouped Settings screen in same design system.
- [ ] Verify compile and unit tests.

### Task 6: Final reference pass + release
**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `.github/workflows/android.yml`
- Modify: `README.md`

**Interfaces:**
- Produces final `ELXVRO-Scan-v2.0.0-debug` APK artifact.

- [ ] Set versionCode 20 / versionName 2.0.0.
- [ ] Update artifact name only; keep CI workflow structure stable.
- [ ] Compare all four root screens against the reference checklist: palette, header, bottom nav, card radius, button height, spacing, icon consistency, overflow.
- [ ] Run full `gradle testDebugUnitTest --stacktrace`.
- [ ] Run full `gradle assembleDebug --stacktrace`.
- [ ] Download artifact and verify APK integrity.
