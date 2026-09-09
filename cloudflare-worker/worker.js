/**
 * MKUPortal - Cloudflare Worker Crash Reporter Gateway
 * 
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 * 
 * Automatically receives crash reports from the MKUPortal Android App
 * and forwards formatted alert messages to an Admin Telegram Bot/Group.
 */

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);

    // 1. Handle CORS Preflight
    if (request.method === "OPTIONS") {
      return handleCors();
    }

    // 2. Health check route
    if (request.method === "GET" && (url.pathname === "/" || url.pathname === "/health")) {
      return jsonResponse({
        service: "MKUPortal Crash Reporter Gateway",
        status: "healthy",
        timestamp: new Date().toISOString()
      });
    }

    // 3. Crash report intake route
    if (
      request.method === "POST" &&
      (url.pathname === "/api/crash" ||
        url.pathname === "/api/crash-report" ||
        url.pathname === "/crash" ||
        url.pathname === "/crash-report" ||
        url.pathname === "/")
    ) {
      return handleCrashReport(request, env);
    }

    return jsonResponse({ error: "Endpoint not found" }, 404);
  }
};

/**
 * Handles incoming crash report and sends to Telegram
 */
async function handleCrashReport(request, env) {
  try {
    // Optional Bearer token validation if configured
    if (env.API_SECRET_KEY) {
      const authHeader = request.headers.get("Authorization") || "";
      const expected = `Bearer ${env.API_SECRET_KEY}`;
      if (authHeader !== expected) {
        return jsonResponse({ error: "Unauthorized: Invalid API secret key" }, 401);
      }
    }

    let payload;
    try {
      payload = await request.json();
    } catch (e) {
      return jsonResponse({ error: "Invalid JSON payload" }, 400);
    }

    // Support both snake_case and camelCase formats
    const excClass = payload.exception_class || payload.exceptionClass;
    const stackTrace = payload.stack_trace || payload.stackTrace;
    const excMessage = payload.exception_message || payload.exceptionMessage;

    // Validate required fields
    if (!excClass && !stackTrace && !excMessage) {
      return jsonResponse({ error: "Missing required crash information" }, 400);
    }

    // Normalize payload to snake_case for formatter
    payload.app_name = payload.app_name || payload.appName;
    payload.version_name = payload.version_name || payload.versionName;
    payload.version_code = payload.version_code || payload.versionCode;
    payload.build_type = payload.build_type || payload.buildType;
    payload.student_id = payload.student_id || payload.studentId;
    payload.device_manufacturer = payload.device_manufacturer || payload.deviceManufacturer;
    payload.device_model = payload.device_model || payload.deviceModel;
    payload.android_version = payload.android_version || payload.androidVersion;
    payload.sdk_int = payload.sdk_int || payload.sdkInt;
    payload.active_screen = payload.active_screen || payload.activeScreen;
    payload.thread_name = payload.thread_name || payload.threadName;
    payload.exception_class = excClass;
    payload.exception_message = excMessage;
    payload.stack_trace = stackTrace;
    payload.available_ram_mb = payload.available_ram_mb || payload.availableRamMb;
    payload.total_ram_mb = payload.total_ram_mb || payload.totalRamMb;
    payload.network_type = payload.network_type || payload.networkType;
    if (payload.is_fatal === undefined && payload.isFatal !== undefined) {
      payload.is_fatal = payload.isFatal;
    }

    // Format HTML alert message for Telegram
    const message = formatTelegramMessage(payload);

    // Send to Telegram
    const telegramResult = await sendToTelegram(message, env);

    if (telegramResult.ok) {
      return jsonResponse({
        status: "success",
        message: "Crash report sent to Telegram successfully"
      });
    } else {
      return jsonResponse({
        status: "partial_error",
        message: "Failed to forward to Telegram",
        details: telegramResult.description
      }, 502);
    }
  } catch (error) {
    return jsonResponse({
      error: "Internal Worker Error",
      details: error.message
    }, 500);
  }
}

/**
 * Format crash details into clean HTML for Telegram Bot API
 */
function formatTelegramMessage(p) {
  const isFatal = p.is_fatal !== false;
  const alertIcon = isFatal ? "🚨" : "⚠️";
  const severity = isFatal ? "🔴 <b>CRITICAL (FATAL CRASH)</b>" : "🟡 <b>WARNING (NON-FATAL)</b>";

  const appName = escapeHtml(p.app_name || "MKUPortal");
  const versionName = escapeHtml(p.version_name || "1.0");
  const versionCode = p.version_code || 1;
  const buildType = escapeHtml(p.build_type || "release");

  const studentId = escapeHtml(p.student_id || "Guest / Not Authenticated");
  const screen = escapeHtml(p.active_screen || "Unknown Screen");

  const device = escapeHtml(`${p.device_manufacturer || ""} ${p.device_model || "Unknown Device"}`.trim());
  const osInfo = escapeHtml(`Android ${p.android_version || "?"} (API ${p.sdk_int || "?"})`);

  const network = escapeHtml(p.network_type || "Unknown");
  const ramInfo = (p.available_ram_mb && p.total_ram_mb)
    ? `${p.available_ram_mb} MB / ${p.total_ram_mb} MB`
    : "N/A";

  const timeStr = p.timestamp
    ? new Date(p.timestamp).toLocaleString("en-US", { timeZone: "Asia/Ho_Chi_Minh" })
    : new Date().toLocaleString("en-US", { timeZone: "Asia/Ho_Chi_Minh" });

  const excClass = escapeHtml(p.exception_class || "Unknown Exception");
  const excMessage = escapeHtml(p.exception_message || "No error message");

  // Truncate stacktrace to keep under Telegram 4096 character limit
  const cleanStack = truncateStackTrace(p.stack_trace || "No stacktrace provided", 2500);

  return `
${alertIcon} <b>[${appName}] APPLICATION CRASH REPORT</b> ${alertIcon}
━━━━━━━━━━━━━━━━━━━━━━━━━━
⚙️ <b>Severity</b>: ${severity}
👤 <b>Student ID</b>: <code>${studentId}</code>
📍 <b>Screen</b>: <code>${screen}</code>
📱 <b>Device</b>: <b>${device}</b> (${osInfo})
📶 <b>Network</b>: <code>${network}</code> | 🧠 <b>Free RAM</b>: <code>${ramInfo}</code>
🏷️ <b>Version</b>: <code>v${versionName} (Build ${versionCode}) - ${buildType}</code>
⏰ <b>Time</b>: <code>${timeStr} (ICT)</code>
━━━━━━━━━━━━━━━━━━━━━━━━━━
❌ <b>Exception</b>: <code>${excClass}</code>
💬 <b>Message</b>: <i>${excMessage}</i>

📜 <b>Stack Trace</b>:
<pre><code class="language-java">${cleanStack}</code></pre>
  `.trim();
}

/**
 * Truncate long stacktraces preserving head and relevant tail frames
 */
function truncateStackTrace(stack, maxLen) {
  if (stack.length <= maxLen) {
    return escapeHtml(stack);
  }
  const headLen = Math.floor(maxLen * 0.7);
  const tailLen = Math.floor(maxLen * 0.25);
  const head = stack.substring(0, headLen);
  const tail = stack.substring(stack.length - tailLen);
  return escapeHtml(`${head}\n\n... [Trimmed ${stack.length - maxLen} characters of stacktrace] ...\n\n${tail}`);
}

/**
 * Dispatch message to Telegram Bot API
 */
async function sendToTelegram(text, env) {
  const token = env.TELEGRAM_BOT_TOKEN;
  const chatId = env.TELEGRAM_CHAT_ID;

  if (!token || !chatId) {
    return {
      ok: false,
      description: "Missing TELEGRAM_BOT_TOKEN or TELEGRAM_CHAT_ID in Worker environment secrets"
    };
  }

  const tgUrl = `https://api.telegram.org/bot${token}/sendMessage`;
  const response = await fetch(tgUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      chat_id: chatId,
      text: text,
      parse_mode: "HTML",
      disable_web_page_preview: true
    })
  });

  return await response.json();
}

function escapeHtml(text) {
  if (!text) return "";
  return String(text)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function handleCors() {
  return new Response(null, {
    headers: {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization"
    }
  });
}

function jsonResponse(body, status = 200) {
  return new Response(JSON.stringify(body, null, 2), {
    status: status,
    headers: {
      "Content-Type": "application/json; charset=UTF-8",
      "Access-Control-Allow-Origin": "*"
    }
  });
}
