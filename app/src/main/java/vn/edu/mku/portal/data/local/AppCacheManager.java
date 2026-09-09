/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.LruCache;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppCacheManager {

    private static final String PREF_CACHE_NAME = "mku_app_cache";
    private static final int MEMORY_CACHE_SIZE = 100; // max entries in RAM

    public static final long TTL_DAY = 24 * 60 * 60 * 1000L;
    public static final long TTL_HOURS_2 = 2 * 60 * 60 * 1000L;
    public static final long TTL_MINUTES_30 = 30 * 60 * 1000L;
    public static final long TTL_MINUTES_15 = 15 * 60 * 1000L;

    private static AppCacheManager instance;
    private final SharedPreferences prefs;
    private final LruCache<String, CacheEntry> memoryCache;
    private final Gson gson;
    private final ExecutorService diskExecutor;

    private static class CacheEntry {
        Object data; // In-memory Object for 0ms zero-copy access
        String json; // Serialized JSON string for disk persistence
        long timestamp;
        long ttlMillis;

        CacheEntry(Object data, String json, long timestamp, long ttlMillis) {
            this.data = data;
            this.json = json;
            this.timestamp = timestamp;
            this.ttlMillis = ttlMillis;
        }

        boolean isExpired() {
            if (ttlMillis <= 0) return false;
            return System.currentTimeMillis() - timestamp > ttlMillis;
        }
    }

    private AppCacheManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_CACHE_NAME, Context.MODE_PRIVATE);
        this.memoryCache = new LruCache<>(MEMORY_CACHE_SIZE);
        this.gson = new Gson();
        this.diskExecutor = Executors.newSingleThreadExecutor();
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new AppCacheManager(context);
        }
    }

    public static synchronized AppCacheManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AppCacheManager is not initialized. Call init(context) first.");
        }
        return instance;
    }

    public synchronized <T> void put(String key, T data, long ttlMillis) {
        if (key == null || data == null) return;
        long timestamp = System.currentTimeMillis();
        // 1. Immediately store data in RAM without waiting for serialization
        CacheEntry entry = new CacheEntry(data, null, timestamp, ttlMillis);
        memoryCache.put(key, entry);

        // 2. Offload JSON serialization and disk storage to background executor
        diskExecutor.execute(() -> {
            try {
                String json = gson.toJson(data);
                entry.json = json;
                String meta = timestamp + "|" + ttlMillis + "|" + json;
                prefs.edit().putString(key, meta).apply();
            } catch (Exception ignored) {}
        });
    }

    public synchronized boolean hasValidCache(String key) {
        CacheEntry entry = getEntry(key);
        return entry != null && !entry.isExpired();
    }

    public synchronized boolean hasAnyCache(String key) {
        return getEntry(key) != null;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(String key, Type typeOfT) {
        CacheEntry entry = getEntry(key);
        if (entry == null || entry.isExpired()) return null;
        if (entry.data != null) {
            try {
                return (T) entry.data;
            } catch (ClassCastException ignored) {}
        }
        if (entry.json != null) {
            try {
                T parsed = gson.fromJson(entry.json, typeOfT);
                entry.data = parsed;
                return parsed;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getOrStale(String key, Type typeOfT) {
        CacheEntry entry = getEntry(key);
        if (entry == null) return null;
        if (entry.data != null) {
            try {
                return (T) entry.data;
            } catch (ClassCastException ignored) {}
        }
        if (entry.json != null) {
            try {
                T parsed = gson.fromJson(entry.json, typeOfT);
                entry.data = parsed;
                return parsed;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private synchronized CacheEntry getEntry(String key) {
        if (key == null) return null;

        // Check RAM
        CacheEntry entry = memoryCache.get(key);
        if (entry != null) {
            return entry;
        }

        // Check Disk
        String raw = prefs.getString(key, null);
        if (raw != null) {
            int firstPipe = raw.indexOf('|');
            int secondPipe = raw.indexOf('|', firstPipe + 1);
            if (firstPipe > 0 && secondPipe > firstPipe) {
                try {
                    long timestamp = Long.parseLong(raw.substring(0, firstPipe));
                    long ttl = Long.parseLong(raw.substring(firstPipe + 1, secondPipe));
                    String json = raw.substring(secondPipe + 1);
                    CacheEntry diskEntry = new CacheEntry(null, json, timestamp, ttl);
                    memoryCache.put(key, diskEntry);
                    return diskEntry;
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    public synchronized void remove(String key) {
        if (key == null) return;
        memoryCache.remove(key);
        diskExecutor.execute(() -> prefs.edit().remove(key).apply());
    }

    public synchronized void clearStudentCache(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            clearUserCache();
            return;
        }
        String prefix = studentId.trim() + "_";

        // Evict from memory
        Map<String, CacheEntry> snapshot = memoryCache.snapshot();
        for (String k : snapshot.keySet()) {
            if (k.startsWith(prefix) || k.contains(studentId)) {
                memoryCache.remove(k);
            }
        }

        // Evict from disk
        diskExecutor.execute(() -> {
            SharedPreferences.Editor editor = prefs.edit();
            for (String k : prefs.getAll().keySet()) {
                if (k.startsWith(prefix) || k.contains(studentId)) {
                    editor.remove(k);
                }
            }
            editor.apply();
        });
    }

    public synchronized void clearMemory() {
        memoryCache.evictAll();
    }

    public synchronized void clearUserCache() {
        memoryCache.evictAll();
        diskExecutor.execute(() -> prefs.edit().clear().apply());
    }
}
