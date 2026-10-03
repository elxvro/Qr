# ELXVRO Scan PRO Subscription + QR Card Design

## Goal
Add a production-ready Google Play subscription entitlement layer and a PRO-only QR Card creator to ELXVRO Scan without changing the approved v2 visual language.

## Product Model
### Free
- QR and barcode scanning.
- Gallery scanning.
- Standard QR creation.
- Default ELXVRO logo included in generated QR output.
- Basic QR color choices.
- Standard export sizes.

### PRO
- User can replace the ELXVRO logo with their own logo.
- User can remove the logo entirely.
- Logo size controls.
- Advanced QR color/background controls.
- High-resolution export sizes.
- QR Card creator.
- QR Card templates: Minimal, Kurumsal, Wi-Fi, Sosyal Medya, Etkinlik.
- Card customization: logo, title, subtitle, contact fields, colors, QR placement, card ratio.
- Save and share card as image.

## Subscription Model
- Google Play subscription product id: `elxvro_scan_pro`.
- Base plans: monthly and yearly.
- Prices, currency, trial/introductory offers, taxes and localized display strings are always read from Google Play ProductDetails; no hardcoded store price text.
- Google Play Billing Library: 9.1.0.
- Entitlement name in app: `PRO`.
- A user is PRO only when Google Play reports an active, acknowledged subscription entitlement for the configured product.
- Cancellation does not immediately remove PRO; access remains until the paid entitlement expires.
- Restore purchases action queries Play and refreshes entitlement.
- Pending purchases are never treated as active PRO.
- Purchase acknowledgements are handled before entitlement is persisted as active.

## Architecture
### Billing
Create a dedicated billing package:
- `billing/BillingManager.kt`: owns BillingClient lifecycle, product queries, purchase flow, acknowledgement and purchase refresh.
- `billing/ProEntitlement.kt`: sealed entitlement state (`Unknown`, `Free`, `Pro`, `Pending`, `Error`).
- `billing/BillingRepository.kt`: exposes observable subscription/product details and entitlement state to Compose.
- `billing/BillingProducts.kt`: constants for product id and expected monthly/yearly base-plan tags/ids.

Billing must not be implemented directly in composables. Compose screens consume state and dispatch intents to the repository/manager.

### PRO Gate
Create one reusable gate policy:
- Free users may open the QR Card section and view locked previews/templates.
- Attempting a PRO-only edit/export action opens the paywall.
- Existing free scanning features must never be blocked by billing state or Play availability.
- If Google Play billing is unavailable, the app remains fully usable in Free mode.

### QR Card Domain
Create a focused card package:
- `qrcard/QrCardModel.kt`: content and appearance model.
- `qrcard/QrCardTemplate.kt`: supported card template enum/config.
- `qrcard/QrCardRenderer.kt`: renders a Bitmap from model + generated QR bitmap.
- `qrcard/QrCardValidator.kt`: input validation and readable-QR constraints.
- `qrcard/QrCardExport.kt`: save/share card bitmap using scoped storage/FileProvider patterns already used by QR image export.

The renderer must keep QR quiet zone and contrast sufficient for reliable scanning. Custom logos must be bounded so they cannot obscure too much of the QR.

## UI / Visual Design
Keep the exact v2 design language already approved:
- `ScanTokens` remains the source of truth for palette, spacing, radii and sizing.
- No second visual theme.
- Bottom navigation remains Tara / Oluştur / Geçmiş / Ayarlar.
- PRO affordances use the existing dark navy / electric blue system plus restrained gold crown badges.

### Entry Point
Add a compact `QR Kart` card/entry in the Create screen below standard QR creation options.
- Free users see a crown/PRO badge.
- PRO users enter directly.

### QR Card Editor
Four compact tabs within one screen:
1. Tasarım
2. İçerik
3. Stil
4. Gelişmiş

Editor supports:
- Logo picker.
- Card title and subtitle.
- Template selection.
- Card color/accent selection.
- QR position: top / center / bottom where supported by template.
- Contact fields relevant to selected card type.
- QR payload source.
- Live preview.

### Templates
1. Minimal: white/light card, compact brand + QR.
2. Kurumsal: dark/brand-colored professional card with business contact lines.
3. Wi-Fi: SSID and connection prompt with QR.
4. Sosyal Medya: profile/brand name plus social account labels and QR.
5. Etkinlik: event title/date/location plus QR.

### Paywall
Use one full-screen/bottom-sheet style consistent with v2.
Show:
- ELXVRO Scan PRO title.
- Short value statement.
- Key benefits: own logo, QR Cards, high-resolution export, advanced styles.
- Monthly option card populated from Play price.
- Yearly option card populated from Play price.
- Primary subscribe button.
- Restore purchases action.
- Manage subscription link when appropriate.
- Terms and privacy links.

Do not fabricate discounts. If yearly savings are shown, calculate only from actual Play prices when both price values are safely comparable.

## Settings
Add a PRO section in Settings:
- Current plan/status.
- Upgrade to PRO when Free.
- Manage subscription when PRO.
- Restore purchases.
- Billing error state with retry.

## Security / Reliability
- UI flags alone must not grant PRO.
- Local entitlement cache is only for continuity while offline; it must originate from a previously verified Play purchase state and be refreshed when Play reconnects.
- Never store raw payment credentials.
- Never log purchase tokens in production logs.
- Unknown/error billing state should fail closed for PRO-only actions while leaving Free functionality available.

## Testing
Add unit tests for:
- Product/base-plan selection logic.
- Entitlement mapping for purchased, pending, cancelled/expired and error states.
- Price selection/display model without hardcoded amounts.
- PRO gate policy.
- QR Card validation, especially logo size and QR contrast/quiet zone constraints.
- Template model defaults.
- Free mode remains usable when billing is unavailable.

Add Android/Compose tests for:
- Free user tapping own-logo action opens paywall.
- PRO user can enter QR Card editor.
- Monthly/yearly offers render from supplied ProductDetails state.
- Restore purchase control is reachable.

## Store Configuration Required After App Build
The app can compile before the Play Console product exists, but real purchase testing requires Play Console configuration:
- Subscription product: `elxvro_scan_pro`.
- Monthly base plan.
- Yearly base plan.
- Active offers/prices for the target countries.
- App uploaded to a Play testing track and installed through Google Play for end-to-end billing tests.

## Versioning
- Target app version: 2.1.0.
- Increment versionCode from 20 to 21.

## Completion Criteria
- Existing scan/create/history/settings behavior remains functional.
- Free/PRO feature boundaries behave consistently.
- QR Card output is scannable after export for every template.
- Billing unavailability cannot crash or block Free features.
- Unit tests and debug APK build pass in CI.
- Final UI side-by-side check confirms no theme drift from v2 reference design.
