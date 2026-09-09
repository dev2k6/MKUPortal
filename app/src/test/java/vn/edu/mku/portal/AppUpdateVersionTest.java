/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import org.junit.Test;

import vn.edu.mku.portal.data.update.AppUpdateConfig;
import vn.edu.mku.portal.data.update.AppUpdateManager;
import vn.edu.mku.portal.data.update.GitHubReleaseModel;

public class AppUpdateVersionTest {

    @Test
    public void testGitHubConfigValues() {
        assertEquals("dev2k6", AppUpdateConfig.GITHUB_OWNER);
        assertEquals("MKUPortal", AppUpdateConfig.GITHUB_REPO);
        assertEquals("https://api.github.com/repos/dev2k6/MKUPortal/releases/latest", 
                AppUpdateConfig.getLatestReleaseApiUrl());
        assertEquals("https://github.com/dev2k6/MKUPortal/releases/latest", 
                AppUpdateConfig.getReleasesWebUrl());
    }

    @Test
    public void testVersionComparison_NewerPatch() {
        assertTrue(AppUpdateManager.isNewerVersion("1.0", "1.0.1"));
        assertTrue(AppUpdateManager.isNewerVersion("1.0.0", "1.0.1"));
        assertTrue(AppUpdateManager.isNewerVersion("v1.0", "v1.0.1"));
        assertTrue(AppUpdateManager.isNewerVersion("1.0", "v1.0.1"));
    }

    @Test
    public void testVersionComparison_NewerMinorOrMajor() {
        assertTrue(AppUpdateManager.isNewerVersion("1.0.9", "1.1.0"));
        assertTrue(AppUpdateManager.isNewerVersion("1.9.9", "2.0.0"));
        assertTrue(AppUpdateManager.isNewerVersion("1.0", "2.0"));
    }

    @Test
    public void testVersionComparison_EqualVersions() {
        assertFalse(AppUpdateManager.isNewerVersion("1.0", "1.0"));
        assertFalse(AppUpdateManager.isNewerVersion("1.0.0", "1.0"));
        assertFalse(AppUpdateManager.isNewerVersion("v1.0.1", "1.0.1"));
        assertFalse(AppUpdateManager.isNewerVersion("1.0.1", "v1.0.1"));
    }

    @Test
    public void testVersionComparison_OlderVersions() {
        assertFalse(AppUpdateManager.isNewerVersion("1.1", "1.0"));
        assertFalse(AppUpdateManager.isNewerVersion("2.0.0", "1.9.9"));
        assertFalse(AppUpdateManager.isNewerVersion("1.0.5", "1.0.4"));
    }

    @Test
    public void testVersionComparison_SuffixHandling() {
        // 1.0.1-beta vs 1.0 -> 1.0.1 > 1.0 -> true
        assertTrue(AppUpdateManager.isNewerVersion("1.0", "1.0.1-beta"));
        // 1.0 vs 1.0-beta -> false
        assertFalse(AppUpdateManager.isNewerVersion("1.0", "1.0-beta"));
    }

    @Test
    public void testReleaseModelJsonDeserialization() {
        String json = "{\n" +
                "  \"tag_name\": \"v1.0.1\",\n" +
                "  \"name\": \"MKUPortal v1.0.1 - Cập nhật tối ưu\",\n" +
                "  \"body\": \"- Sửa lỗi hiển thị logo\\n- Nâng cao trải nghiệm\",\n" +
                "  \"html_url\": \"https://github.com/dev2k6/MKUPortal/releases/tag/v1.0.1\",\n" +
                "  \"published_at\": \"2026-09-09T23:00:00Z\",\n" +
                "  \"assets\": [\n" +
                "    {\n" +
                "      \"name\": \"MKUPortal-release.apk\",\n" +
                "      \"browser_download_url\": \"https://github.com/dev2k6/MKUPortal/releases/download/v1.0.1/MKUPortal-release.apk\",\n" +
                "      \"size\": 3984588,\n" +
                "      \"content_type\": \"application/vnd.android.package-archive\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Gson gson = new Gson();
        GitHubReleaseModel release = gson.fromJson(json, GitHubReleaseModel.class);

        assertNotNull(release);
        assertEquals("v1.0.1", release.getTagName());
        assertEquals("1.0.1", release.getCleanVersion());
        assertEquals("MKUPortal v1.0.1 - Cập nhật tối ưu", release.getName());
        assertEquals("- Sửa lỗi hiển thị logo\n- Nâng cao trải nghiệm", release.getBody());
        assertEquals("https://github.com/dev2k6/MKUPortal/releases/tag/v1.0.1", release.getHtmlUrl());

        // Test APK direct download extraction
        assertEquals("https://github.com/dev2k6/MKUPortal/releases/download/v1.0.1/MKUPortal-release.apk", 
                release.getApkDownloadUrl());
        assertEquals("3.8 MB", release.getApkSizeFormatted());
    }
}
