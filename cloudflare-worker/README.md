# MKUPortal Crash Reporter Gateway ⚡
### Serverless Cloudflare Worker for Automated Android Crash Telemetry & Telegram Alerts

[![Cloudflare Workers](https://img.shields.io/badge/Platform-Cloudflare%20Workers-F38020?logo=cloudflare&logoColor=white)](https://workers.cloudflare.com/)
[![Runtime](https://img.shields.io/badge/Runtime-V8%20%2F%20ES%20Modules-brightgreen)](https://developers.cloudflare.com/workers/)
[![Telegram Bot API](https://img.shields.io/badge/Alerts-Telegram%20Bot%20API-26A5E4?logo=telegram&logoColor=white)](https://core.telegram.org/bots/api)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](../LICENSE)

The **MKUPortal Crash Reporter Gateway** is an ultra-fast, serverless edge microservice built on **Cloudflare Workers**. It acts as a dedicated diagnostic gateway that receives crash telemetry payloads from the **MKUPortal Android application** and immediately forwards formatted, rich HTML diagnostic alerts directly to the administrator's Telegram bot or engineering group.

---

## 🏗️ Architecture & Benefits

```
┌──────────────────────────┐      HTTP POST /api/crash      ┌──────────────────────────┐
│   MKUPortal Android      ├───────────────────────────────►│  Cloudflare Edge Worker   │
│ (UncaughtExceptionHandler│   (Device specs, stacktrace)   │  (mku-crash-reporter)    │
└──────────────────────────┘                                └────────────┬─────────────┘
                                                                         │
                                                                   Telegram Bot API
                                                              (POST /bot<token>/sendMessage)
                                                                         │
                                                                         ▼
                                                            ┌──────────────────────────┐
                                                            │   Admin Telegram Chat    │
                                                            │ (Instant Alert Delivery) │
                                                            └──────────────────────────┘
```

* **Zero University Server Overhead**: Crash telemetry is handled 100% serverless at the Cloudflare edge, completely independent of Mekong University's academic servers.
* **Token Security**: Your Telegram Bot API Token and Admin Chat ID are securely encrypted in Cloudflare Worker Secrets—never bundled into or exposed inside the Android APK.
* **Smart Stacktrace Truncation**: Automatically trims excessive stacktrace frames to fit within Telegram's strict 4,096-character limit while preserving the head and tail frames.
* **CORS & Preflight Ready**: Full preflight `OPTIONS` handling with appropriate HTTP headers.

---

## 📋 Prerequisites: Telegram Bot & Chat ID Setup

### 1. Create a Telegram Bot
1. Open Telegram and search for the official bot: [`@BotFather`](https://t.me/BotFather).
2. Send the command: `/newbot`.
3. Enter a display name (e.g., `MKUPortal Crash Alert`) and a username (e.g., `mku_portal_alert_bot`).
4. `@BotFather` will generate your **HTTP API Bot Token**:
   ```text
   8910374316:AAEzqNERF-uv16M06TFBZRbRM5Sc6XuqRqU
   ```
   Save this token securely. This is your `TELEGRAM_BOT_TOKEN`.

### 2. Obtain Your `TELEGRAM_CHAT_ID`
* **For Personal Direct Alerts**:
  1. Open a conversation with your newly created bot and press **Start** (`/start`).
  2. Search for `@userinfobot` or `@getmyid_bot` on Telegram and send `/start`.
  3. The bot will reply with your numeric User ID (e.g., `1244823958`). This is your `TELEGRAM_CHAT_ID`.
* **For Team / Channel Alerts**:
  1. Add your bot to your technical team group or channel with message sending permissions.
  2. Send a test message into the group.
  3. Open `https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getUpdates` in your browser to inspect the group's `chat.id` (group IDs typically begin with `-100`, e.g., `-1001987654321`).

---

## 🚀 Deployment Guide

### Option 1: Deploy via Cloudflare Wrangler CLI (Recommended)

From within the `cloudflare-worker/` directory:

1. **Authenticate with Cloudflare**:
   ```bash
   npx wrangler login
   ```
   *(Or pass `CLOUDFLARE_API_TOKEN` and `CLOUDFLARE_ACCOUNT_ID` in your CI/CD environment).*

2. **Configure Encrypted Secrets**:
   ```bash
   npx wrangler secret put TELEGRAM_BOT_TOKEN
   # Paste your Telegram Bot Token when prompted

   npx wrangler secret put TELEGRAM_CHAT_ID
   # Paste your Telegram Chat ID when prompted
   ```

3. **Deploy the Worker**:
   ```bash
   npx wrangler deploy
   ```
   Upon completion, Wrangler will output your live gateway URL:
   ```text
   https://mku-crash-reporter.dev2k6.workers.dev
   ```

---

### Option 2: Deploy via Cloudflare Web Dashboard

1. Sign in to the [Cloudflare Dashboard](https://dash.cloudflare.com/).
2. Navigate to **Compute (Workers & Pages)** ➡️ Click **Create Application** ➡️ **Create Worker**.
3. Name your worker (e.g., `mku-crash-reporter`) and click **Deploy**.
4. Click **Edit Code**:
   - Replace the default template with the entire contents of [`worker.js`](worker.js).
   - Click **Save and Deploy**.
5. Configure Secrets:
   - Navigate to **Settings** ➡️ **Variables and Secrets**.
   - Add the following encrypted secrets:
     - `TELEGRAM_BOT_TOKEN`: Your bot token from `@BotFather`.
     - `TELEGRAM_CHAT_ID`: Your chat ID from step 2.
     - *(Optional)* `API_SECRET_KEY`: A shared secret if you wish to enforce Bearer token verification.

---

## 📡 API Endpoints

### 1. Health Check
* **Endpoint**: `GET /health` or `GET /`
* **Response**:
  ```json
  {
    "service": "MKUPortal Crash Reporter Gateway",
    "status": "healthy",
    "timestamp": "2026-09-09T16:00:00.000Z"
  }
  ```

### 2. Ingest Crash Report
* **Endpoint**: `POST /api/crash` (also accepts `/api/crash-report`)
* **Headers**: `Content-Type: application/json`
* **Payload Format (JSON)**:
  ```json
  {
    "app_name": "MKUPortal",
    "version_name": "1.0.0",
    "version_code": 1,
    "build_type": "release",
    "student_id": "2100123",
    "device_manufacturer": "Google",
    "device_model": "Pixel 8 Pro",
    "android_version": "14",
    "sdk_int": 34,
    "available_ram_mb": 4096,
    "total_ram_mb": 12288,
    "network_type": "WiFi",
    "active_screen": "vn.edu.mku.portal.ui.student.MarksActivity",
    "thread_name": "main",
    "is_fatal": true,
    "exception_class": "java.lang.NullPointerException",
    "exception_message": "Attempt to invoke virtual method on a null object reference",
    "stack_trace": "java.lang.NullPointerException: ...\n\tat vn.edu.mku.portal.ui.student.MarksActivity.onCreate(MarksActivity.java:42)",
    "timestamp": 1725900000000
  }
  ```
* **Success Response (HTTP 200)**:
  ```json
  {
    "status": "success",
    "message": "Crash report sent to Telegram successfully"
  }
  ```

---

## 🧪 Testing with cURL

Verify your deployed worker with a test telemetry dispatch:

```bash
curl -X POST "https://mku-crash-reporter.dev2k6.workers.dev/api/crash" \
  -H "Content-Type: application/json" \
  -d '{
    "app_name": "MKUPortal",
    "version_name": "1.0.0",
    "version_code": 1,
    "build_type": "release",
    "student_id": "2100123",
    "device_manufacturer": "Google",
    "device_model": "Pixel 8 Pro",
    "android_version": "14",
    "sdk_int": 34,
    "available_ram_mb": 4096,
    "total_ram_mb": 12288,
    "network_type": "WiFi",
    "active_screen": "vn.edu.mku.portal.ui.student.MarksActivity",
    "thread_name": "main",
    "is_fatal": true,
    "exception_class": "java.lang.NullPointerException",
    "exception_message": "Test crash telemetry verification",
    "stack_trace": "java.lang.NullPointerException: Test verification\n\tat vn.edu.mku.portal.ui.student.MarksActivity.onCreate(MarksActivity.java:42)"
  }'
```

An immediate HTML-formatted notification will be delivered to your Telegram chat.

---

## 👨‍💻 Developer Information

* **Author**: **Thái Nguyên** (`dev2k6`)
* **Email**: [thainguyen.junior@gmail.com](mailto:thainguyen.junior@gmail.com)
* **Phone / Zalo / Telegram**:
  * `03333 499 48`
  * `07777 63 858`
* **Repository**: [https://github.com/dev2k6/MKUPortal](https://github.com/dev2k6/MKUPortal)

---

## 📄 License

This microservice is open source and available under the [MIT License](../LICENSE).
