# Hướng Dẫn Cài Đặt & Triển Khai Cloudflare Worker Báo Cáo Crash Lên Telegram

Hệ thống Gateway Serverless trên **Cloudflare Workers** nhận các bản tin báo cáo sự cố (Crash Reports) từ ứng dụng Android MKUPortal và tự động gửi thông báo chi tiết qua **Telegram Bot** cho Admin / Nhóm Kỹ Thuật.

---

## 1. Chuẩn Bị Telegram Bot & Chat ID

### Bước 1: Tạo Bot Telegram
1. Mở Telegram, tìm kiếm bot chính thức: `@BotFather`.
2. Gửi lệnh `/newbot`.
3. Nhập tên cho bot (ví dụ: `MKU Portal Crash Alert`) và username (ví dụ: `mku_crash_alert_bot`).
4. `@BotFather` sẽ cấp cho bạn **HTTP API Token**, có dạng:
   ```text
   7123456789:AAFlq0AbCdEfGhIjKlMnOpQrStUvWxYz123
   ```
   👉 Lưu giá trị này lại, đây là `TELEGRAM_BOT_TOKEN`.

### Bước 2: Lấy `TELEGRAM_CHAT_ID`
* **Nếu nhận tin nhắn vào tài khoản cá nhân**:
  1. Nhấn `Start` vào bot vừa tạo.
  2. Tìm kiếm bot `@userinfobot` hoặc `@getmyid_bot` trên Telegram và nhấn `Start`.
  3. Bot sẽ hiển thị `Your user ID: 123456789`. Đây chính là `TELEGRAM_CHAT_ID`.
* **Nếu nhận tin nhắn vào Nhóm / Kênh kỹ thuật**:
  1. Mời bot vừa tạo vào nhóm Telegram của bạn.
  2. Cấp quyền gửi tin nhắn cho bot.
  3. Thêm bot `@RawDataBot` vào nhóm để xem ID của nhóm (chat ID của nhóm thường có dấu `-` phía trước, ví dụ: `-1001987654321`).
  4. Sau khi lấy được ID, có thể kick `@RawDataBot` ra khỏi nhóm.

---

## 2. Triển Khai Lên Cloudflare Workers

Bạn có thể triển khai theo 1 trong 2 cách sau:

### Cách 1: Triển khai qua Cloudflare Dashboard (Giao diện Web - Không cần cài đặt)
1. Đăng nhập vào [Cloudflare Dashboard](https://dash.cloudflare.com/).
2. Vào mục **Workers & Pages** -> Nhấn **Create application** -> **Create Worker**.
3. Đặt tên worker: `mku-crash-reporter` -> Nhấn **Deploy**.
4. Nhấn **Edit code**:
   - Sao chép toàn bộ nội dung trong tệp `worker.js` và dán đè vào trình soạn thảo.
   - Nhấn **Deploy**.
5. Cấu hình biến bí mật (Secrets):
   - Vào tab **Settings** của Worker -> chọn mục **Variables and Secrets**.
   - Thêm 2 Secrets:
     - `TELEGRAM_BOT_TOKEN`: Nhập Token bot đã lấy ở Bước 1.
     - `TELEGRAM_CHAT_ID`: Nhập Chat ID đã lấy ở Bước 2.
     - *(Tùy chọn)* `API_SECRET_KEY`: Khóa bí mật nếu muốn xác thực request từ app.
6. Lấy đường dẫn Worker URL của bạn (Ví dụ: `https://mku-crash-reporter.<your-subdomain>.workers.dev`).

---

### Cách 2: Triển khai bằng dòng lệnh (Cloudflare Wrangler CLI)
Trong thư mục `cloudflare-worker/`:
```bash
# 1. Đăng nhập tài khoản Cloudflare
npx wrangler login

# 2. Cấu hình 2 biến bí mật
npx wrangler secret put TELEGRAM_BOT_TOKEN
# (Nhập token bot của bạn khi được hỏi)

npx wrangler secret put TELEGRAM_CHAT_ID
# (Nhập chat ID của bạn khi được hỏi)

# 3. Triển khai
npx wrangler deploy
```

---

## 3. Kiểm Thử API Bằng cURL

Sau khi triển khai, bạn có thể chạy lệnh test trực tiếp từ Terminal:

```bash
curl -X POST "https://mku-crash-reporter.<your-subdomain>.workers.dev/api/crash-report" \
  -H "Content-Type: application/json" \
  -d '{
    "app_name": "MKUPortal",
    "version_name": "1.0",
    "version_code": 1,
    "build_type": "release",
    "student_id": "2100123",
    "device_manufacturer": "Samsung",
    "device_model": "SM-S911B",
    "android_version": "14",
    "sdk_int": 34,
    "timestamp": 1725900000000,
    "active_screen": "MarksActivity",
    "thread_name": "main",
    "exception_class": "java.lang.NullPointerException",
    "exception_message": "Attempt to invoke virtual method on a null object reference",
    "stack_trace": "java.lang.NullPointerException: Attempt to invoke virtual method on a null object reference\n\tat vn.edu.mku.portal.ui.student.MarksActivity.fetchMarksData(MarksActivity.java:160)\n\tat vn.edu.mku.portal.ui.student.MarksActivity.onCreate(MarksActivity.java:45)",
    "available_ram_mb": 1540,
    "total_ram_mb": 7800,
    "network_type": "WIFI",
    "is_fatal": true
  }'
```

Ngay lập tức bot Telegram sẽ gửi tin nhắn cảnh báo định dạng HTML đẹp mắt đến tài khoản / nhóm của bạn!
