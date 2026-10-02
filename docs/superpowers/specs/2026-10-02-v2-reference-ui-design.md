# ELXVRO Scan v2.0 Reference UI Design

## Goal
Rebuild the ELXVRO Scan Android UI as the working mobile equivalent of the approved reference artwork `ELXVRO Scan Uygulama Tanıtım Panosu.png`, prioritizing visual fidelity, cross-screen consistency, usability, responsive behavior, and performance in that order.

## Binding Reference
The reference artwork is a direct design target, not inspiration. The application must preserve its overall dark camera/header framing, white content surfaces, electric-blue accent, compact cards, fixed four-item bottom navigation, typography hierarchy, rounded geometry, and spacing rhythm. Do not invent a second theme or unrelated visual language.

## Design Tokens
### Colors
- `ink`: #090E15 — main camera/background/header/bottom-nav surface
- `inkRaised`: #111821 — secondary dark controls
- `blue`: #0068F8 — active navigation, primary CTA, scan frame and selected chips
- `blueBright`: #0070F8 — focus/pressed/highlight variant
- `paper`: #F7F8F9 — light screen/content background
- `card`: #FFFFFF — result sheets and content cards
- `text`: #111820 — primary text on light surfaces
- `textOnDark`: #F8FAFC — primary text on dark surfaces
- `muted`: #8E96A3 — secondary text
- `divider`: #E7E9ED — borders/dividers
- Semantic icon colors only: success #16C865, warning #FFB000, danger #FF3B30.

### Geometry
- Grid: 4dp base unit
- Screen horizontal padding: 16dp
- Standard component gap: 8dp
- Section gap: 12dp / 16dp
- Header height: 56dp plus status-bar inset
- Bottom navigation: 68dp plus navigation-bar inset
- Primary button height: 50dp
- Circular scan-side controls: 52dp
- Center scan action: 72dp
- Standard card radius: 14dp
- Bottom sheet / hero card radius: 22dp top corners
- Button radius: 12dp
- Chip radius: 18dp
- Border: 1dp divider color where needed
- Elevation: minimal (0–4dp); no heavy shadows

### Typography
Use Android sans-serif/Roboto family.
- Screen title: 20sp / Semibold
- Result title: 18sp / Semibold
- Card title: 15sp / Semibold
- Body: 14sp / Regular
- Supporting: 12sp / Regular
- Bottom nav: 11sp / Medium
- Primary CTA: 14sp / Semibold

## System Bars / Responsive Rules
- Edge-to-edge layout with safe-area handling.
- Status and navigation bars visually merge into `ink`.
- No clipped content on compact phones; scroll content screens where required.
- Use width constraints rather than fixed pixel dimensions. Maintain the reference proportions on common Android portrait widths.
- Bottom navigation remains identical in height, placement, icon treatment and active state on all four root screens.

## Root Navigation
Fixed tabs:
1. Tara
2. Oluştur
3. Geçmiş
4. Ayarlar

No root screen may use an alternate bottom navigation.

## Scan Screen
- Dark full-height camera surface.
- Header: menu icon, ELXVRO Scan mark/title, compact right-side utility icon.
- Large blue corner scan frame centered in the upper-middle camera area.
- Small zoom pill (1x / 2x) beneath frame.
- Three circular actions above bottom navigation: Galeri, prominent center scan/focus control, Fener.
- Camera controls remain legible over varying live backgrounds using controlled dark translucent surfaces, not arbitrary colors.
- Tap-to-focus and pinch-to-zoom supported.
- Flash control reflects real flash availability/state.
- Duplicate scan guard retained.

## Scan Result Sheet
- Results appear as a white modal bottom sheet with 22dp top radius, matching the reference.
- Show semantic title, raw value/URL, format and domain/type metadata.
- Primary smart action is blue.
- Secondary action rows use consistent line icons: copy, share, save/favorite/history, product lookup as applicable.
- URL results show the host/domain before opening.
- Unknown/custom URI schemes are never auto-opened.
- Optional auto-copy is allowed; optional auto-open applies only to HTTP/HTTPS URLs.

## Create Screen
- Dark header, compact segmented type selector, light content card.
- Types: URL, Metin, Wi-Fi, Kişi, Telefon, E-posta, SMS, Konum, Takvim.
- Generated QR displayed centrally on white.
- Consistent blue primary `QR Kod Oluştur` CTA.
- Save and Share secondary actions below.
- QR generation supports size and quiet-zone/margin options without cluttering the default layout.
- Wi-Fi supports WPA/WPA2, WEP and open-network payloads.

## History / Favorites
- Dark fixed header and the same root bottom navigation.
- Compact filter chips: Tümü, QR Kod, Barkod, Favoriler.
- Search field follows reference geometry.
- White compact list surface with semantic line icons and star favorite action.
- Preserve v1 search, filter, sort, favorite and delete behavior.
- Add multi-select for bulk favorite/delete/share.
- Add JSON/CSV export of history.

## Settings
- Dark header, light grouped settings cards.
- Keep one ELXVRO visual theme only; do not expose a second light/dark theme selector.
- Controls: Tarama Sesi, Titreşim, Fener Varsayılanı, Hızlı Açılış, Otomatik Kopyala, Güvenli Otomatik URL Açma, duplicate-scan wait setting, Geçmişi Temizle, Dışa Aktar, Gizlilik, Kullanım Koşulları/About.
- Switches are custom-colored with reference blue and compact row heights.

## Recognized Content / Smart Actions
Maintain and polish support for URL, phone, e-mail, Wi-Fi, text, contact/vCard, location, SMS, calendar and product barcode actions. Semantic icons may use restrained category colors shown in the reference, while structural UI remains blue/ink/white.

## Data / Migration
- Existing v1 SharedPreferences history/favorites must remain readable and must not be destroyed by the UI migration.
- Settings use a single preference source and sane defaults.
- No cloud upload of camera frames or history.

## Architecture
- Migrate UI from the oversized programmatic-View `MainActivity` to Jetpack Compose.
- `MainActivity` becomes the edge-to-edge host.
- Centralize visual tokens in a `ui/theme` package.
- Split root screens into focused composables.
- Isolate CameraX/ML Kit lifecycle in a scanner controller.
- Reuse existing scan store, deduplicator, QR generation and smart-action domain logic where correct.
- New behavior logic stays unit-testable outside composables.

## Testing / Completion Criteria
- Existing unit tests remain green.
- Add tests for expanded QR payload types, safe-auto-open policy, history bulk selection/export formatting, and v1 preference compatibility.
- CI must pass `testDebugUnitTest` and `assembleDebug`.
- Visual completion requires a manual side-by-side checklist against the reference for: palette, header, bottom nav, card radius, button height, icon consistency, spacing, overflow, and root-screen continuity.
- Version: 2.0.0, versionCode 20.
