# ELXVRO Scan

Native Android QR & barcode scanner and QR generator.

## v2.9.1-test — PRO production validation build

This build is intended only for pre-release device testing of PRO features:

- Debuggable builds force the effective entitlement to PRO so premium features can be tested without a Google Play purchase
- Release/non-debuggable builds continue to use the real Google Play Billing entitlement unchanged
- Settings shows “PRO TEST aktif” and hides upgrade/manage/restore billing actions while test mode is active
- Billing code remains present and continues to refresh in the background; the test override only changes the entitlement exposed to feature gates
- PRO test mode must not be used as the final store build; remove/disable the override before publishing the production release
- Version bumped to 2.9.1-test / versionCode 31

## v2.9.0 — Smart result cards

ELXVRO Scan 2.9 makes structured QR and barcode results easier to read while keeping the original raw payload available:

- Wi-Fi results show SSID, security type, hidden-network state and password
- Wi-Fi passwords are hidden by default and require an explicit reveal action
- vCard and MECARD contact results show name, phone and e-mail fields
- Calendar/VEVENT results show event title, start, end and location when available
- mailto results show recipient, subject and message body
- SMS results show phone number and message body
- geo results show validated latitude and longitude
- Product barcodes show the product code and exact barcode format prominently
- Malformed or incomplete structured payloads fall back to the existing raw-result presentation instead of failing
- Raw scanned content remains visible, selectable, copyable and shareable
- Structured cards are additive: unsupported fields and formats continue to use the existing raw-result flow
- Settings/About now reads the installed app version dynamically instead of showing the old hard-coded 2.1.0 value
- All parsing remains local/offline and does not send scanned content to a server
- Added unit coverage for Wi-Fi, vCard, MECARD, VEVENT, mailto, SMS, geo, product and malformed-data fallback parsing
- Version bumped to 2.9.0 / versionCode 30

## v2.8.0 — Local URL safety

ELXVRO Scan 2.8 adds an offline URL safety layer to scanned web links without uploading URLs or scan history:

- Every scanned HTTP/HTTPS link is classified locally as low, medium or high risk
- Normal HTTPS links without structural warning signals are shown as low risk
- Plain HTTP links are marked as medium risk because transport is not encrypted
- Direct IPv4/IPv6 destinations are flagged because the destination is less transparent than a named host
- URLs containing user-info / @ target masking are marked high risk
- Punycode and Unicode IDN hostnames are normalized and flagged for lookalike-domain awareness
- Known URL-shortener hosts are flagged because the final destination is hidden
- Malformed or hostless web URLs are marked high risk
- The scan result sheet shows the resolved host and local risk reasons before manual opening
- Opening a medium/high-risk URL from History requires an explicit local warning confirmation
- Medium/high-risk links use an explicit “Siteyi Yine de Aç” action
- Safe Auto Open now allows only low-risk HTTPS links; HTTP, shorteners, IDN/Punycode, IP hosts and high-risk structures never auto-open
- The existing scanner, batch scan, history, QR creation and PRO flows are unchanged
- Added unit coverage for URL classification, IDN normalization, shorteners, target masking and auto-open policy
- Version bumped to 2.8.0 / versionCode 29

## v2.7.0 — PRO QR Card expansion

ELXVRO Scan 2.7 expands the PRO QR Card editor without changing normal QR generation, scanning, batch scanning or billing behavior:

- Added Business, Promo and Ticket QR Card templates alongside the existing five templates
- Each new template has a distinct visual accent system while preserving safe QR/text separation
- Added card aspect presets: template default, square 1:1, wide 16:9, classic card 1.586:1 and portrait 4:5
- Layout tests cover every template across every supported aspect preset
- Custom/ELXVRO logo composition now uses a larger backing plate with a subtle boundary while retaining the 20% safe QR coverage limit
- QR Card export adds 3072 px and 4096 px premium options
- The 4096 px ceiling is scoped to QR Card export; the normal QR generator remains capped at its existing 2048 px PRO limit
- High-resolution QR Card export renders the card at the selected size while limiting the embedded QR source bitmap to 2048 px for memory efficiency
- Added unit coverage for new template catalog, presentation identity, aspect ratios, logo safety and premium QR Card export sizes
- Version bumped to 2.7.0 / versionCode 28

## v2.6.0 — Scanner ergonomics

ELXVRO Scan 2.6 improves camera scanning ergonomics while preserving single-scan, batch-scan, history and creation flows:

- Added lightweight camera-frame luminance sampling with throttling
- Low-light detection uses hysteresis so the warning does not flicker on borderline frames
- Single and batch scanning show an in-camera low-light hint
- When the device has a flash, the low-light hint can enable the torch directly
- Added an initial center autofocus assist after camera startup
- If no code is detected for a short period, center autofocus retries at a controlled cadence
- Manual tap-to-focus and 1x/2x zoom remain available
- Plain text scan results no longer show a redundant primary Share button because Share already exists in the action list
- Result text preview allows more visible lines before ellipsis
- Added unit coverage for low-light transitions, luma sampling cadence, focus retry timing and result action policy
- Version bumped to 2.6.0 / versionCode 27

## v2.5.0 — Batch scanning

ELXVRO Scan 2.5 adds a dedicated batch scanning workflow without changing the normal single-scan result flow:

- Added Toplu Tarama entry directly from the scanner header
- Continuous live-camera collection of QR codes and supported barcodes
- Multiple gallery images can be processed into the same batch session
- Duplicate value + format pairs are kept only once per batch session
- Same value encoded in a different format remains a distinct result
- Every accepted unique batch result is also stored in the normal local scan history
- Multi-select, select-all, clear-selection and selected-item removal
- Batch selection is session-local; accepted scans remain safely stored in normal local history
- CSV and JSON export uses selected items when a selection exists, otherwise the full batch
- Batch mode intentionally disables single-result auto-open/auto-copy so scanning can continue uninterrupted
- Added unit coverage for batch deduplication, selection, removal and CSV/JSON export
- Version bumped to 2.5.0 / versionCode 26

## v2.4.0 — Barcode creator

ELXVRO Scan 2.4 expands Create with native 1D barcode generation while keeping the existing QR and PRO QR Card flows intact:

- Added dedicated barcode creator entry under Oluştur
- CODE 128 generation for printable text, stock, order and custom identifiers
- EAN-13 generation with 12-digit automatic check-digit completion or 13-digit validation
- UPC-A generation with 11-digit automatic check-digit completion or 12-digit validation
- High-resolution wide PNG barcode rendering with human-readable value below the bars
- Generated barcodes can be saved to the gallery or shared as PNG
- Successful barcode creation is saved to local history as BARCODE with its exact format
- Existing history barcode filter continues to work with generated CODE_128, EAN_13 and UPC_A items
- Added unit coverage for barcode validation, check digits, format mapping and render canvas policy
- Version bumped to 2.4.0 / versionCode 25

## v2.3.0 — QR Card visual quality

ELXVRO Scan 2.3 improves the PRO QR Card creator and keeps the scanner/free QR flow unchanged:

- Five QR Card templates now have distinct visual presentation signatures
- Minimal uses the clean ELXVRO rail treatment
- Corporate uses a stronger header band and framed QR treatment
- Wi-Fi uses a network badge and elevated QR presentation
- Social uses a centered profile-style layout with a highlighted QR ring
- Event uses a dedicated event panel and accent band
- Long titles, subtitles and detail fields wrap safely across multiple lines instead of collapsing into one truncated line
- QR Card preview sanitizes temporary invalid editor state instead of disappearing
- Preview, save and share continue through the same card renderer for consistent output
- Added unit coverage for template presentation, multiline text and safe preview behavior
- Version bumped to 2.3.0 / versionCode 24

## v2.2.1 — QR Card layout fix

ELXVRO Scan 2.2.1 fixes the premium QR Card composition without changing the free scanner or core QR generator:

- Landscape cards keep text and QR in separate left/right regions
- Square Social cards reserve a dedicated text block above the QR
- Portrait Event cards keep event details above the bottom QR area
- Top/center/bottom QR placement now recalculates a safe non-overlapping text region
- Text sizing adapts to the available card region instead of the full card width
- Added layout tests across every built-in QR Card template
- Version bumped to 2.2.1 / versionCode 23

## v2.2.0 — PRO + QR Card hardening

ELXVRO Scan 2.2 tightens subscription and premium QR Card behavior without changing the free scanner/core QR experience:

- Time-bounded offline PRO entitlement cache instead of an indefinitely trusted boolean flag
- Restore Purchases now exposes a checking state while Google Play is queried
- Failed billing-flow launches fall back safely instead of silently leaving stale state
- Monthly/yearly offer mapping prefers the regular base-plan offer when promotional offers coexist
- PRO, Pending, Unknown and billing-error states cannot start duplicate subscription purchases
- QR Card validation rejects non-finite logo scales and unsafe/non-finite card aspect ratios before rendering
- Added unit coverage for entitlement freshness, offer selection, paywall purchase policy and QR Card geometry
- Version bumped to 2.2.0 / versionCode 22

## v2.0.0 — Reference UI redesign

ELXVRO Scan 2.0 rebuilds the interface around one compact, consistent reference-driven design system:

- Single ink / white / electric-blue visual system across every root screen
- Jetpack Compose UI with one fixed Tara / Oluştur / Geçmiş / Ayarlar bottom navigation
- Edge-to-edge layout with status/navigation safe-area handling
- Reference-style camera screen, blue scan frame, 1x/2x zoom, tap-to-focus and pinch zoom
- Flashlight and multiple-image gallery scanning
- White scan-result bottom sheet with smart actions, URL domain preview, copy/share/favorite actions
- Optional automatic copy and HTTP/HTTPS-only safe automatic opening
- Expanded QR creator: URL, text, Wi-Fi, contact, phone, e-mail, SMS, location and calendar
- WPA/WPA2, WEP and open Wi-Fi QR payloads
- QR image color, size and quiet-zone controls plus save/share
- Compact searchable/filterable history and favorites
- Bulk favorite, share and delete actions
- CSV and JSON history export
- Calendar, URL, phone, e-mail, SMS, map, Wi-Fi, contact and product smart actions
- Scan sound, vibration, default flashlight, quick-start and duplicate-scan delay settings
- Existing v1 local history/favorites remain compatible through the original local storage format
- Local/offline core scanning and QR generation; camera frames and history are not uploaded
- Updated ELXVRO Scan launcher mark matching the v2 visual language

## Earlier milestone

v1.0.0 completed the original scanner roadmap: core CameraX/ML Kit scanning, gallery input, history/favorites, QR generation, smart actions, search/filtering, sound/vibration, onboarding, privacy UX and production hardening.
