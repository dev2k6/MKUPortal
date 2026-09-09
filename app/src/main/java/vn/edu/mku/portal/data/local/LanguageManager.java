package vn.edu.mku.portal.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.mku.portal.data.network.ApiClient;
import vn.edu.mku.portal.data.network.model.ResourceLanguageItem;

public class LanguageManager {

    private static final String PREF_NAME = "mku_language_pref";
    private static final String KEY_CURRENT_LANG = "current_language";
    private static final String KEY_CACHE_PREFIX = "resource_lang_cache_";

    public static final String LANG_VI = "vi";
    public static final String LANG_EN = "en";

    private static LanguageManager instance;
    private final Context appContext;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    // In-memory cache for active language lookup (lowercase keys)
    private final Map<String, String> memoryCache = new HashMap<>();
    private final MutableLiveData<Map<String, String>> languageLiveData = new MutableLiveData<>(memoryCache);

    private LanguageManager(Context context) {
        this.appContext = context.getApplicationContext();
        this.prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String currentLang = getCurrentLanguage();
        applyLocaleToApp(appContext, currentLang);
        loadCachedFromLocal(currentLang);

        fetchLanguageFromServer(currentLang, null);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new LanguageManager(context);
        }
    }

    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LanguageManager is not initialized. Call LanguageManager.init(context) first.");
        }
        return instance;
    }

    public String getCurrentLanguage() {
        return prefs.getString(KEY_CURRENT_LANG, LANG_VI);
    }

    public void setCurrentLanguage(String langCode) {
        String targetLang = LANG_EN.equalsIgnoreCase(langCode) ? LANG_EN : LANG_VI;
        prefs.edit().putString(KEY_CURRENT_LANG, targetLang).apply();
        applyLocaleToApp(appContext, targetLang);
        loadCachedFromLocal(targetLang);
        fetchLanguageFromServer(targetLang, null);
    }

    public LiveData<Map<String, String>> getLanguageLiveData() {
        return languageLiveData;
    }

    /**
     * Get dynamic string with fallback
     */
    public String getString(String componentId, String keyLanguage, String defaultFallback) {
        if (TextUtils.isEmpty(keyLanguage)) {
            return defaultFallback;
        }

        // 1. Direct lookup by lowercase "ComponentID.KeyLanguage"
        if (!TextUtils.isEmpty(componentId)) {
            String directKey = buildKey(componentId, keyLanguage).toLowerCase(Locale.ROOT);
            String val = memoryCache.get(directKey);
            if (!TextUtils.isEmpty(val)) {
                return val;
            }
        }

        // 2. Global lookup across all components by lowercase keyLanguage
        String keySuffix = "." + keyLanguage.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> entry : memoryCache.entrySet()) {
            if (entry.getKey().endsWith(keySuffix)) {
                if (!TextUtils.isEmpty(entry.getValue())) {
                    return entry.getValue();
                }
            }
        }

        // 3. Fallback
        return defaultFallback;
    }

    /**
     * Get localized string from Android app resources for current active language
     */
    public String getAppString(@StringRes int resId, Object... formatArgs) {
        try {
            String lang = getCurrentLanguage();
            Locale locale = new Locale(lang);
            Configuration config = new Configuration(appContext.getResources().getConfiguration());
            config.setLocale(locale);
            Context localizedContext = appContext.createConfigurationContext(config);
            if (formatArgs != null && formatArgs.length > 0) {
                return localizedContext.getString(resId, formatArgs);
            } else {
                return localizedContext.getString(resId);
            }
        } catch (Exception e) {
            return appContext.getString(resId, formatArgs);
        }
    }

    private String buildKey(String componentId, String keyLanguage) {
        return (componentId != null ? componentId : "") + "." + (keyLanguage != null ? keyLanguage : "");
    }

    public Context wrapContext(Context context) {
        String lang = getCurrentLanguage();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }

    public void applyLocaleToApp(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private void loadCachedFromLocal(String langCode) {
        String json = prefs.getString(KEY_CACHE_PREFIX + langCode, null);
        if (!TextUtils.isEmpty(json)) {
            try {
                Type type = new TypeToken<List<ResourceLanguageItem>>() {}.getType();
                List<ResourceLanguageItem> list = gson.fromJson(json, type);
                if (list != null) {
                    populateCache(list);
                }
            } catch (Exception ignored) {}
        }
    }

    public void fetchLanguageFromServer(String langCode, Runnable onComplete) {
        ApiClient.getApiService().getResourceLanguage(langCode).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ResourceLanguageItem>> call, @NonNull Response<List<ResourceLanguageItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ResourceLanguageItem> items = response.body();
                    saveCacheToLocal(langCode, items);
                    if (langCode.equalsIgnoreCase(getCurrentLanguage())) {
                        populateCache(items);
                    }
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResourceLanguageItem>> call, @NonNull Throwable t) {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    private void populateCache(List<ResourceLanguageItem> items) {
        memoryCache.clear();
        for (ResourceLanguageItem item : items) {
            if (item.getComponentId() != null && item.getKeyLanguage() != null && item.getDescription() != null) {
                String key = buildKey(item.getComponentId(), item.getKeyLanguage()).toLowerCase(Locale.ROOT);
                memoryCache.put(key, item.getDescription());
            }
        }
        languageLiveData.postValue(new HashMap<>(memoryCache));
    }

    private void saveCacheToLocal(String langCode, List<ResourceLanguageItem> items) {
        try {
            String json = gson.toJson(items);
            prefs.edit().putString(KEY_CACHE_PREFIX + langCode, json).apply();
        } catch (Exception ignored) {}
    }
}