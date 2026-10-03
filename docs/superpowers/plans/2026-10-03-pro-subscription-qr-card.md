# ELXVRO Scan PRO Subscription + QR Card Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Google Play monthly/yearly PRO subscriptions and a PRO-only QR Card creator while preserving the approved v2 visual language and all free scanning functionality.

**Architecture:** Billing is isolated under `billing/`, QR Card generation under `qrcard/`, and Compose screens consume state rather than owning payment logic. Free scanning/create/history/settings remain operational even if Play Billing is unavailable. PRO entitlement gates premium editor/export actions but never gates core scanner functionality.

**Tech Stack:** Kotlin, Jetpack Compose, Google Play Billing 9.1.0, CameraX, ML Kit, ZXing, JUnit 4, existing GitHub Actions pipeline.

**Spec:** `docs/superpowers/specs/2026-10-03-pro-subscription-qr-card-design.md`

## Global Constraints

- Product id: `elxvro_scan_pro`.
- Base plan ids: `monthly` and `yearly`.
- Prices and localized billing text come only from Google Play ProductDetails.
- Pending purchases do not grant PRO.
- Acknowledgement is required before persisting active entitlement.
- Free features stay usable when billing is unavailable.
- No raw payment credentials or purchase tokens are logged.
- Existing v2 `ScanTokens` design system remains the only visual language.
- Root bottom navigation remains Tara / Oluştur / Geçmiş / Ayarlar.
- Target version: 2.1.0, versionCode 21.

## Review Focus

- Play Billing unavailable/offline: app must fall back to Free mode without blocking scanner or QR generation.
- ProductDetails missing one base plan: paywall must render the available plan only and never invent a price.
- Pending/cancelled purchase: PRO must remain locked unless an acknowledged active entitlement is returned.
- Oversized or low-contrast custom logo/card: validator must reject or clamp unsafe output so exported QR remains scannable.
- Existing users upgrading from v2.0.0: history, settings, and scanner behavior must remain intact after 2.1.0 update.

---

### Task 1: Billing product model and entitlement mapping

**Files:**
- Create: `app/src/main/java/com/elxvro/scan/billing/BillingProducts.kt`
- Create: `app/src/main/java/com/elxvro/scan/billing/ProEntitlement.kt`
- Create: `app/src/main/java/com/elxvro/scan/billing/BillingOfferMapper.kt`
- Test: `app/src/test/java/com/elxvro/scan/billing/BillingOfferMapperTest.kt`

**Interfaces:**
- Produces: `BillingProducts.PRODUCT_ID`, `MONTHLY_BASE_PLAN_ID`, `YEARLY_BASE_PLAN_ID`.
- Produces: `sealed interface ProEntitlement` with `Unknown`, `Free`, `Pending`, `Pro`, `Error`.
- Produces: `BillingOfferMapper.map(...)` returning normalized monthly/yearly offer models with Play-provided formatted price strings.

- [ ] **Step 1: Write failing tests** for monthly/yearly selection, missing-plan handling, pending not granting PRO, and acknowledged purchased state granting PRO.
- [ ] **Step 2: Run** `gradle testDebugUnitTest --tests '*BillingOfferMapperTest*'` and verify RED due to missing production types.
- [ ] **Step 3: Implement** constants, entitlement types and offer mapping with no hardcoded prices.
- [ ] **Step 4: Re-run targeted tests** and verify PASS.
- [ ] **Step 5: Commit** `feat: add billing offer and entitlement models`.

### Task 2: Google Play Billing manager and repository

**Files:**
- Create: `app/src/main/java/com/elxvro/scan/billing/BillingManager.kt`
- Create: `app/src/main/java/com/elxvro/scan/billing/BillingRepository.kt`
- Create: `app/src/main/java/com/elxvro/scan/billing/EntitlementCache.kt`
- Modify: `app/build.gradle.kts`
- Test: `app/src/test/java/com/elxvro/scan/billing/EntitlementPolicyTest.kt`

**Interfaces:**
- Produces: observable `StateFlow<ProEntitlement>` and `StateFlow<List<SubscriptionOffer>>`.
- Produces: `refreshPurchases()`, `launchPurchase(activity, offer)`, `restorePurchases()`, `close()`.
- Entitlement cache may preserve only a previously verified Play-derived PRO state and must be refreshable.

- [ ] **Step 1: Write failing policy tests** for active/acknowledged, pending, cancelled/expired, error, and offline-cache fallback behavior.
- [ ] **Step 2: Run targeted tests** and verify RED.
- [ ] **Step 3: Add** `com.android.billingclient:billing-ktx:9.1.0` and implement BillingClient connection, ProductDetails query, purchase acknowledgement, purchase update handling and restore.
- [ ] **Step 4: Ensure** purchase tokens are never written to logs and billing failure maps to Free/Error without crashing.
- [ ] **Step 5: Run targeted tests and `testDebugUnitTest`**; verify PASS.
- [ ] **Step 6: Commit** `feat: add Google Play PRO billing repository`.

### Task 3: PRO gate and paywall UI

**Files:**
- Create: `app/src/main/java/com/elxvro/scan/pro/ProGate.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/screens/ProPaywallScreen.kt`
- Modify: `app/src/main/java/com/elxvro/scan/ui/AppShell.kt`
- Test: `app/src/test/java/com/elxvro/scan/pro/ProGateTest.kt`

**Interfaces:**
- Produces: `ProGate.canUse(feature, entitlement): Boolean`.
- Produces: paywall callbacks for monthly purchase, yearly purchase, restore and manage subscription.

- [ ] **Step 1: Write failing tests** proving free scanner/core QR remain allowed while own-logo, QR Card, high-resolution export and premium styles require PRO.
- [ ] **Step 2: Run targeted tests** and verify RED.
- [ ] **Step 3: Implement gate policy** and Compose paywall using existing `ScanTokens`, gold crown badge and Play-provided prices only.
- [ ] **Step 4: Wire billing state at app-shell level** so screens receive entitlement and paywall launcher without direct BillingClient access.
- [ ] **Step 5: Run unit tests and compileDebugKotlin**; verify PASS.
- [ ] **Step 6: Commit** `feat: add PRO gate and subscription paywall`.

### Task 4: QR Card domain model, validator and renderer

**Files:**
- Create: `app/src/main/java/com/elxvro/scan/qrcard/QrCardModel.kt`
- Create: `app/src/main/java/com/elxvro/scan/qrcard/QrCardTemplate.kt`
- Create: `app/src/main/java/com/elxvro/scan/qrcard/QrCardValidator.kt`
- Create: `app/src/main/java/com/elxvro/scan/qrcard/QrCardRenderer.kt`
- Create: `app/src/main/java/com/elxvro/scan/qrcard/QrCardExport.kt`
- Test: `app/src/test/java/com/elxvro/scan/qrcard/QrCardValidatorTest.kt`
- Test: `app/src/test/java/com/elxvro/scan/qrcard/QrCardTemplateTest.kt`

**Interfaces:**
- Templates: `MINIMAL`, `CORPORATE`, `WIFI`, `SOCIAL`, `EVENT`.
- Produces: `QrCardValidator.validate(model): ValidationResult`.
- Produces: `QrCardRenderer.render(model, qrBitmap, logoBitmap?): Bitmap`.
- Produces: save/share helpers matching existing scoped-storage/FileProvider behavior.

- [ ] **Step 1: Write failing tests** for template defaults, maximum logo coverage, QR contrast, quiet-zone minimum and required template fields.
- [ ] **Step 2: Run targeted tests** and verify RED.
- [ ] **Step 3: Implement models and validation** with safe logo bounds and readable QR constraints.
- [ ] **Step 4: Implement bitmap renderer** for all five templates using fixed design tokens and responsive card ratios.
- [ ] **Step 5: Implement export** to PNG/share intent.
- [ ] **Step 6: Run tests and compile**; verify PASS.
- [ ] **Step 7: Commit** `feat: add PRO QR Card rendering engine`.

### Task 5: QR Card editor and Free/PRO logo behavior

**Files:**
- Create: `app/src/main/java/com/elxvro/scan/ui/screens/QrCardEditorScreen.kt`
- Create: `app/src/main/java/com/elxvro/scan/ui/components/ProBadge.kt`
- Modify: `app/src/main/java/com/elxvro/scan/ui/screens/CreateScreen.kt`
- Modify: `app/src/main/java/com/elxvro/scan/ui/AppShell.kt`

**Interfaces:**
- Free standard QR uses built-in ELXVRO logo.
- PRO can select custom logo, remove logo, adjust logo size and open QR Card editor.
- QR Card editor tabs: Tasarım / İçerik / Stil / Gelişmiş.

- [ ] **Step 1: Add UI-state tests or pure-state tests** for Free/PRO logo defaults and editor access behavior.
- [ ] **Step 2: Verify RED** before production changes.
- [ ] **Step 3: Add compact `QR Kart` PRO entry** to Create screen without changing root navigation.
- [ ] **Step 4: Implement editor** with five templates, logo picker, title/subtitle/contact fields, colors, QR placement and live preview.
- [ ] **Step 5: Gate premium actions** through `ProGate`; locked actions open paywall.
- [ ] **Step 6: Run tests and `assembleDebug`**; verify PASS.
- [ ] **Step 7: Commit** `feat: add QR Card editor and premium logo controls`.

### Task 6: Settings subscription management and versioning

**Files:**
- Modify: `app/src/main/java/com/elxvro/scan/ui/screens/SettingsScreen.kt`
- Modify: `app/build.gradle.kts`
- Modify: `.github/workflows/android.yml`

**Interfaces:**
- Settings shows current Free/PRO status.
- Free: Upgrade to PRO + Restore Purchases.
- PRO: Manage Subscription + Restore Purchases.
- Billing error: Retry without blocking other settings.

- [ ] **Step 1: Add state tests** for Free, PRO and Billing Error settings presentation.
- [ ] **Step 2: Verify RED**.
- [ ] **Step 3: Implement PRO settings section** matching v2 design language.
- [ ] **Step 4: Set** `versionCode = 21`, `versionName = "2.1.0"`, and workflow artifact name `ELXVRO-Scan-v2.1.0-debug`.
- [ ] **Step 5: Run `testDebugUnitTest` and `assembleDebug`**; verify PASS.
- [ ] **Step 6: Commit** `release: prepare ELXVRO Scan 2.1.0`.

### Task 7: Final regression, visual review and APK verification

**Files:**
- Modify only files required by defects found during verification.

**Interfaces:**
- Final artifact: `ELXVRO-Scan-v2.1.0-debug.apk`.

- [ ] **Step 1: Run full unit test suite** `gradle testDebugUnitTest --stacktrace` and require success.
- [ ] **Step 2: Run debug APK build** `gradle assembleDebug --stacktrace` and require success.
- [ ] **Step 3: Verify CI artifact upload** on GitHub Actions.
- [ ] **Step 4: Side-by-side visual checklist** against v2 reference: header, bottom navigation, palette, card radius, spacing, paywall, QR Card editor, settings continuity.
- [ ] **Step 5: Verify Free-mode regression** with billing unavailable: scanner, gallery scan, standard QR, history and settings still operate.
- [ ] **Step 6: Verify premium boundaries**: own logo, QR Card, high-res/premium export remain locked without PRO.
- [ ] **Step 7: Download artifact and verify APK ZIP integrity/file type** before delivery.
