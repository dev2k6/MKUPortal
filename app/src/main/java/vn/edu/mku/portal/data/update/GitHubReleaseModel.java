/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.update;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class GitHubReleaseModel implements Serializable {

    @SerializedName("tag_name")
    private String tagName;

    @SerializedName("name")
    private String name;

    @SerializedName("body")
    private String body;

    @SerializedName("html_url")
    private String htmlUrl;

    @SerializedName("published_at")
    private String publishedAt;

    @SerializedName("assets")
    private List<Asset> assets;

    public static class Asset implements Serializable {
        @SerializedName("name")
        private String name;

        @SerializedName("browser_download_url")
        private String browserDownloadUrl;

        @SerializedName("size")
        private long size;

        @SerializedName("content_type")
        private String contentType;

        public String getName() { return name; }
        public String getBrowserDownloadUrl() { return browserDownloadUrl; }
        public long getSize() { return size; }
        public String getContentType() { return contentType; }
    }

    public String getTagName() {
        return tagName != null ? tagName : "";
    }

    public String getCleanVersion() {
        String tag = getTagName().trim();
        if (tag.startsWith("v") || tag.startsWith("V")) {
            return tag.substring(1);
        }
        return tag;
    }

    public String getName() {
        return name != null && !name.trim().isEmpty() ? name : getTagName();
    }

    public String getBody() {
        return body != null ? body : "";
    }

    public String getHtmlUrl() {
        return htmlUrl != null ? htmlUrl : AppUpdateConfig.getReleasesWebUrl();
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    /**
     * Tìm đường dẫn tải trực tiếp file APK từ danh sách assets nếu có,
     * nếu không có thì trả về link trang web release.
     */
    public String getApkDownloadUrl() {
        if (assets != null) {
            for (Asset asset : assets) {
                if (asset.getName() != null && asset.getName().toLowerCase(Locale.ROOT).endsWith(".apk")) {
                    return asset.getBrowserDownloadUrl();
                }
            }
        }
        return getHtmlUrl();
    }

    /**
     * Lấy dung lượng file APK hiển thị dạng "3.8 MB"
     */
    public String getApkSizeFormatted() {
        if (assets != null) {
            for (Asset asset : assets) {
                if (asset.getName() != null && asset.getName().toLowerCase(Locale.ROOT).endsWith(".apk") && asset.getSize() > 0) {
                    double mb = asset.getSize() / (1024.0 * 1024.0);
                    return String.format(Locale.US, "%.1f MB", mb);
                }
            }
        }
        return null;
    }
}
