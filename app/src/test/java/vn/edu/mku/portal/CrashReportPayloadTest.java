/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import vn.edu.mku.portal.data.crash.CrashReportPayload;

import static org.junit.Assert.*;

public class CrashReportPayloadTest {

    private final Gson gson = new Gson();

    @Test
    public void testPayloadSerializationContainsExpectedKeys() {
        CrashReportPayload payload = new CrashReportPayload();
        payload.setAppName("MKUPortal");
        payload.setVersionName("1.0");
        payload.setVersionCode(1);
        payload.setBuildType("release");
        payload.setStudentId("2100123");
        payload.setDeviceManufacturer("Google");
        payload.setDeviceModel("Pixel 8");
        payload.setAndroidVersion("14");
        payload.setSdkInt(34);
        payload.setTimestamp(1725900000000L);
        payload.setActiveScreen("MarksActivity");
        payload.setThreadName("main");
        payload.setExceptionClass("java.lang.NullPointerException");
        payload.setExceptionMessage("Test null pointer");
        payload.setStackTrace("java.lang.NullPointerException: Test null pointer\n\tat vn.edu.mku.portal...");
        payload.setAvailableRamMb(2048);
        payload.setTotalRamMb(8192);
        payload.setNetworkType("WIFI");
        payload.setFatal(true);

        String json = gson.toJson(payload);

        assertTrue(json.contains("\"app_name\":\"MKUPortal\""));
        assertTrue(json.contains("\"student_id\":\"2100123\""));
        assertTrue(json.contains("\"active_screen\":\"MarksActivity\""));
        assertTrue(json.contains("\"exception_class\":\"java.lang.NullPointerException\""));
        assertTrue(json.contains("\"is_fatal\":true"));
        assertTrue(json.contains("\"device_model\":\"Pixel 8\""));
    }

    @Test
    public void testOfflineQueueSerializationAndDeserialization() {
        List<CrashReportPayload> queue = new ArrayList<>();

        CrashReportPayload p1 = new CrashReportPayload();
        p1.setTimestamp(1001L);
        p1.setExceptionClass("java.lang.IllegalStateException");
        p1.setExceptionMessage("Error 1");
        queue.add(p1);

        CrashReportPayload p2 = new CrashReportPayload();
        p2.setTimestamp(1002L);
        p2.setExceptionClass("java.lang.IndexOutOfBoundsException");
        p2.setExceptionMessage("Error 2");
        queue.add(p2);

        String json = gson.toJson(queue);

        Type type = new TypeToken<List<CrashReportPayload>>() {}.getType();
        List<CrashReportPayload> restored = gson.fromJson(json, type);

        assertNotNull(restored);
        assertEquals(2, restored.size());
        assertEquals(1001L, restored.get(0).getTimestamp());
        assertEquals("java.lang.IllegalStateException", restored.get(0).getExceptionClass());
        assertEquals(1002L, restored.get(1).getTimestamp());
        assertEquals("java.lang.IndexOutOfBoundsException", restored.get(1).getExceptionClass());

        // Test removing sent crash from queue
        restored.removeIf(item -> item.getTimestamp() == 1001L);
        assertEquals(1, restored.size());
        assertEquals(1002L, restored.get(0).getTimestamp());
    }
}
