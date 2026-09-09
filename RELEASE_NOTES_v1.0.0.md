# MKUPortal v1.0.0 — Official Community Release 🚀

We are thrilled to announce the initial official release of **MKUPortal (v1.0.0)** — an open-source, modern, and high-performance Android application crafted for students, faculty, and the academic community of **Mekong University (Trường Đại Học Cửu Long - MKU)**.

---

## 🌟 What's New in v1.0.0

### ⚡ Performance & Core Experience
- **Zero-Copy Disk & Memory Caching**: Instantaneous startup and seamless screen transitions with zero UI stutter.
- **Full Offline Capabilities**: View cached academic schedules, grade transcripts, exam timetables, and tuition history even with intermittent or no campus internet connectivity.
- **Modern Material 3 Architecture**: Clean, responsive, and ergonomic interface built with edge-to-edge support and refined typography.

### 🔔 Smart Background Engine
- **Autonomous Grade Checker (`GradeCheckWorker`)**: Background synchronization notifies students immediately when new semester grades or GPA updates are published.
- **Smart Notification Dispatch**: Timely reminders for upcoming classes, exam room assignments, and academic deadline alerts.

### 🛡️ Resilience & Security
- **Cloudflare Crash Telemetry**: Lightweight, non-intrusive crash reporting worker keeping user privacy paramount while ensuring rapid defect resolution.
- **Encrypted Local Storage**: Sensitive session tokens and student credentials protected using Android Keystore and biometric-compatible encryption layers.

### 🔄 In-App Updates & GitHub Integration
- **Direct GitHub Releases Sync**: Built-in `AppUpdateManager` automatically checks GitHub releases and notifies users of new enhancements without requiring third-party app stores.
- **Lightweight Package**: Minified and optimized with R8/Proguard — ultra-compact footprint (~3.8 MB).

---

## 📦 Download & Verification

| Asset | Type | Size | Description |
| :--- | :--- | :--- | :--- |
| `MKUPortal-v1.0.0.apk` | Release APK | ~3.8 MB | Production release package (sideloadable) |

### 🔒 Checksum Verification
Verify APK integrity after downloading:

- **File**: `MKUPortal-v1.0.0.apk`
- **SHA-256**: `280051d1573586e03c00fa9c6a529466f439efede4194a705ec447e57e541762`

```bash
# macOS / Linux
sha256sum MKUPortal-v1.0.0.apk

# Windows PowerShell
Get-FileHash -Algorithm SHA256 MKUPortal-v1.0.0.apk
```

---

## 📱 System Requirements
- **Operating System**: Android 8.0 (Oreo / API Level 26) or higher
- **Target OS**: Android 14 / 15 (API Level 34/35)
- **Permissions Required**: Internet Access, Network State, Post Notifications (Android 13+)

---

## 🤝 Community & Feedback
- **Issue Tracker**: [GitHub Issues](https://github.com/dev2k6/MKUPortal/issues)
- **Discussions & Feedback**: [GitHub Discussions](https://github.com/dev2k6/MKUPortal/discussions)
- **Project Lead**: Thái Nguyên ([@dev2k6](https://github.com/dev2k6))

*Thank you to all students, contributors, and the Mekong University community for supporting this project!*
