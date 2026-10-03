# ELXVRO Scan

Native Android QR & barcode scanner and QR generator.

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
