# ELXVRO Scan v2.0 Reference UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship ELXVRO Scan 2.0.0 with a reference-accurate Compose UI, consistent design system, improved scanner ergonomics, expanded QR generation, safer result actions, bulk history tools, and preserved v1 data.

**Architecture:** Keep CameraX, ML Kit, ZXing and existing domain/storage logic where valid; replace the monolithic programmatic View UI with a Compose host and focused screens. Put all visual constants in a shared design-system package and isolate camera lifecycle behind `ScannerController` so screen composables remain testable and visually consistent.

**Tech Stack:** Kotlin 1.9.24, Android Gradle Plugin 8.5.2, Jetpack Compose, CameraX 1.3.4, ML Kit Barcode Scanning 17.3.0, ZXing 3.5.3, SharedPreferences compatibility layer, JUnit 4.

**Spec:** `docs/superpowers/specs/2026-10-02-v2-reference-ui-design.md`

## Global Constraints
- Version must be `2.0.0`, versionCode `20`.
- Reference fidelity outranks convenience or default Material styling.
- Use one visual theme only: ink/white/electric-blue reference system.
- Root bottom navigation must remain identical across Tara/Oluştur/Geçmiş/Ayarlar.
- Preserve v1 history/favorites and core offline scanning/QR generation.
- Do not upload camera frames or history.
- Keep current GitHub Actions structure; only version/artifact naming may change.

## Review Focus
- Small-screen overflow: all root screens remain usable at compact portrait widths.
- Camera lifecycle: navigation away from Tara stops analysis; returning restarts cleanly.
- Auto-open safety: only HTTP/HTTPS may auto-open and only when enabled.
- Existing v1 data: old JSON history remains visible/favorited after upgrade.
- Unknown QR payloads: never crash; present as text/share.

---

### Task 1: Compose Foundation + Reference Design System

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/java/com/elxvro/scan/MainActivity.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/theme/ScanTokens.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/theme/ElxvroScanTheme.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/AppShell.kt`

**Interfaces:**
- Produces `ElxvroScanTheme {}` and `ScanTokens` for every screen.
- Produces root `AppTab` navigation and fixed `ElxvroBottomBar`.

- [ ] Add Compose build feature/compiler and Compose dependencies.
- [ ] Implement exact token values from the spec.
- [ ] Convert `MainActivity` to edge-to-edge Compose host.
- [ ] Implement fixed four-tab app shell with dark header/bottom bar and safe-area handling.
- [ ] Run `gradle testDebugUnitTest assembleDebug` and verify success.
- [ ] Commit `feat: add v2 compose design system shell`.

### Task 2: Scanner Controller + Reference Scan Screen

**Files:**
- Create: `app/src/main/java/com/elxvro/scan/scanner/ScannerController.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/screens/ScanScreen.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/components/ScanOverlay.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/components/ResultSheet.kt`
- Create: `app/src/main/java/com/elxvro/scan/SafeActionPolicy.kt`
- Test: `app/src/test/java/com/elxvro/scan/SafeActionPolicyTest.kt`

**Interfaces:**
- `ScannerController.start(lifecycleOwner, previewView, onResult)` / `stop()` / `setTorch()` / `setZoomRatio()` / `close()`.
- `SafeActionPolicy.mayAutoOpen(action: SmartAction): Boolean`.

- [ ] Write failing tests that HTTP/HTTPS can auto-open but custom schemes, phone, SMS, Wi-Fi and text cannot.
- [ ] Implement `SafeActionPolicy` and make tests pass.
- [ ] Move CameraX/ML Kit lifecycle out of Activity.
- [ ] Implement reference camera layout, zoom pill, gallery, center scan control and flashlight.
- [ ] Add tap-to-focus and pinch-to-zoom.
- [ ] Implement white reference-style result bottom sheet with smart actions/domain preview.
- [ ] Add optional auto-copy and safe auto-open settings.
- [ ] Run full unit/build verification.
- [ ] Commit `feat: rebuild reference scanner experience`.

### Task 3: Reference QR Creator + Expanded Payloads

**Files:**
- Modify: `app/src/main/java/com/elxvro/scan/QrPayloadBuilder.kt`
- Modify: `app/src/main/java/com/elxvro/scan/QrCodeUtil.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/screens/CreateScreen.kt`
- Test: `app/src/test/java/com/elxvro/scan/QrPayloadBuilderV2Test.kt`

**Interfaces:**
- Extend `QrPayloadBuilder.build(type, primary, extra)` compatibility while adding structured helpers for SMS, GEO, calendar and Wi-Fi security.
- Add QR generation size/margin options with current defaults preserved.

- [ ] Write failing tests for SMS, geo, calendar, WPA/WEP/open Wi-Fi escaping.
- [ ] Implement minimal payload extensions until tests pass.
- [ ] Build reference segmented selector + white QR card + blue CTA + save/share actions.
- [ ] Add optional QR size/margin controls without increasing default visual density.
- [ ] Run unit/build verification.
- [ ] Commit `feat: expand v2 qr creator`.

### Task 4: Reference History + Bulk Tools

**Files:**
- Modify: `app/src/main/java/com/elxvro/scan/HistoryLogic.kt`
- Create: `app/src/main/java/com/elxvro/scan/HistoryExport.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/screens/HistoryScreen.kt`
- Test: `app/src/test/java/com/elxvro/scan/HistoryExportTest.kt`

**Interfaces:**
- Preserve `HistoryLogic.searchAndFilter`.
- `HistoryExport.toJson(items)` and `HistoryExport.toCsv(items)`.

- [ ] Write failing export/escaping tests.
- [ ] Implement deterministic JSON/CSV export.
- [ ] Implement reference dark header/filter chips + white compact list.
- [ ] Add multi-select, bulk favorite/delete/share.
- [ ] Preserve existing favorites/search/sort/delete behavior and v1 data.
- [ ] Run unit/build verification.
- [ ] Commit `feat: add reference history and bulk tools`.

### Task 5: Settings + Single Theme + Branding

**Files:**
- Create: `app/src/main/java/com/elxvro/scan/AppPrefs.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/screens/SettingsScreen.kt`
- Modify: `app/src/main/res/drawable/ic_launcher.xml`
- Modify: `app/src/main/AndroidManifest.xml`

**Interfaces:**
- `AppPrefs` exposes sound, vibration, defaultTorch, quickStart, autoCopy, safeAutoOpen, duplicateDelay.

- [ ] Centralize settings keys/defaults.
- [ ] Implement reference grouped white settings cards and custom blue switches.
- [ ] Remove runtime dark/light theme switching.
- [ ] Rebuild launcher mark as blue rounded scanner/barcode icon with red scan line.
- [ ] Add privacy/about text aligned with local-processing behavior.
- [ ] Run unit/build verification.
- [ ] Commit `feat: finalize v2 settings and branding`.

### Task 6: Versioning, CI, Regression + Visual Consistency Gate

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `.github/workflows/android.yml`
- Modify: `README.md`

**Interfaces:**
- Final artifact name `ELXVRO-Scan-v2.0.0-debug`.

- [ ] Set versionCode 20/versionName 2.0.0.
- [ ] Keep workflow steps unchanged and update artifact label only.
- [ ] Run all unit tests and `assembleDebug` in CI.
- [ ] Review root screens against the reference checklist: palette, headers, bottom nav, card radius, button height, icon language, spacing, overflow and single-theme continuity.
- [ ] Fix any mismatches found by the checklist and rerun CI.
- [ ] Download artifact, verify APK archive integrity, and deliver final APK.
- [ ] Commit `release: ELXVRO Scan 2.0.0`.
