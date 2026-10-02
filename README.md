# ELXVRO Scan

Native Android QR & barcode scanner and QR generator.

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
