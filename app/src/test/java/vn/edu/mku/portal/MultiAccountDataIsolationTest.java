/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MultiAccountDataIsolationTest {

    private String buildStudentKey(String studentId, String suffix) {
        String id = (studentId != null && !studentId.trim().isEmpty()) ? studentId.trim() : "guest";
        return id + "_" + suffix;
    }

    private String buildDraftKey(String studentId, String formKey, String fieldKey) {
        String sId = (studentId != null && !studentId.trim().isEmpty()) ? studentId.trim() : "guest";
        return sId + "_" + formKey + "_" + fieldKey;
    }

    private String buildDeclarationPrefName(String studentId) {
        String sId = (studentId != null && !studentId.trim().isEmpty()) ? studentId.trim() : "guest";
        return "InvoiceDeclarationPref_" + sId;
    }

    private String buildGradeSnapshotKey(String studentId) {
        return "grade_snapshot_" + (studentId != null ? studentId.trim() : "guest");
    }

    @Test
    public void testCacheKeysAreIsolatedBetweenStudents() {
        String studentA = "2100123";
        String studentB = "2100456";

        String keyMarksA = buildStudentKey(studentA, "marks_01_SV");
        String keyMarksB = buildStudentKey(studentB, "marks_01_SV");

        assertNotEquals(keyMarksA, keyMarksB);
        assertEquals("2100123_marks_01_SV", keyMarksA);
        assertEquals("2100456_marks_01_SV", keyMarksB);

        String keyScheduleA = buildStudentKey(studentA, "drawing_schedules_2024-2025_HK1_1");
        String keyScheduleB = buildStudentKey(studentB, "drawing_schedules_2024-2025_HK1_1");
        assertNotEquals(keyScheduleA, keyScheduleB);
    }

    @Test
    public void testDraftKeysAreIsolatedBetweenStudents() {
        String studentA = "SV001";
        String studentB = "SV002";

        String draftSubjectA = buildDraftKey(studentA, "form_contact", "field_subject");
        String draftSubjectB = buildDraftKey(studentB, "form_contact", "field_subject");

        assertNotEquals(draftSubjectA, draftSubjectB);
        assertEquals("SV001_form_contact_field_subject", draftSubjectA);
        assertEquals("SV002_form_contact_field_subject", draftSubjectB);
    }

    @Test
    public void testInvoiceDeclarationPrefsIsolated() {
        String studentA = "2100123";
        String studentB = "2100456";

        String prefA = buildDeclarationPrefName(studentA);
        String prefB = buildDeclarationPrefName(studentB);

        assertNotEquals(prefA, prefB);
        assertEquals("InvoiceDeclarationPref_2100123", prefA);
        assertEquals("InvoiceDeclarationPref_2100456", prefB);
    }

    @Test
    public void testGradeSnapshotKeyIsolated() {
        String studentA = "SV_A";
        String studentB = "SV_B";

        String snapA = buildGradeSnapshotKey(studentA);
        String snapB = buildGradeSnapshotKey(studentB);

        assertNotEquals(snapA, snapB);
        assertEquals("grade_snapshot_SV_A", snapA);
        assertEquals("grade_snapshot_SV_B", snapB);
    }

    @Test
    public void testGradeDiffingDetection() {
        Map<String, String> oldSnapshot = new HashMap<>();
        oldSnapshot.put("IT101", "8.5|A");
        oldSnapshot.put("IT102", "7.0|B");

        Map<String, String> newSnapshot = new HashMap<>();
        newSnapshot.put("IT101", "8.5|A"); // unchanged
        newSnapshot.put("IT102", "8.0|B+"); // updated score
        newSnapshot.put("IT103", "9.0|A+"); // new course grade

        List<String> changedOrNew = new ArrayList<>();
        for (Map.Entry<String, String> entry : newSnapshot.entrySet()) {
            String course = entry.getKey();
            String val = entry.getValue();
            if (!oldSnapshot.containsKey(course) || !val.equals(oldSnapshot.get(course))) {
                changedOrNew.add(course);
            }
        }

        assertEquals(2, changedOrNew.size());
        assertTrue(changedOrNew.contains("IT102"));
        assertTrue(changedOrNew.contains("IT103"));
        assertFalse(changedOrNew.contains("IT101"));
    }

    @Test
    public void testFallbackWhenStudentIdEmpty() {
        String key = buildStudentKey("", "info");
        assertEquals("guest_info", key);

        String draft = buildDraftKey(null, "form_contact", "field_content");
        assertEquals("guest_form_contact_field_content", draft);
    }
}
