/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.local;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;

public class SettingsManager {

    private static final String PREF_NAME = "mku_settings_pref";
    private static final String KEY_THEME = "pref_theme";
    private static final String KEY_RTL = "pref_rtl";
    private static final String KEY_RESPONSIVE_FONT = "pref_responsive_font";
    private static final String KEY_COMPACT = "pref_compact";
    private static final String KEY_ROUNDED_CORNERS = "pref_rounded_corners";

    public static final String THEME_LIGHT = "Light";
    public static final String THEME_DARK = "Dark";
    public static final String THEME_SYSTEM = "System Default";

    private static SettingsManager instance;
    private final Context appContext;
    private final SharedPreferences prefs;

    private SettingsManager(Context context) {
        this.appContext = context.getApplicationContext();
        this.prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new SettingsManager(context);
        }
    }

    public static synchronized SettingsManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SettingsManager is not initialized. Call SettingsManager.init(context) first.");
        }
        return instance;
    }

    public void saveSettings(String theme, boolean isRtl, boolean isResponsiveFont, boolean isCompact, boolean isRoundedCorners) {
        prefs.edit()
                .putString(KEY_THEME, theme)
                .putBoolean(KEY_RTL, isRtl)
                .putBoolean(KEY_RESPONSIVE_FONT, isResponsiveFont)
                .putBoolean(KEY_COMPACT, isCompact)
                .putBoolean(KEY_ROUNDED_CORNERS, isRoundedCorners)
                .apply();
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, THEME_LIGHT);
    }

    public boolean isRtl() {
        return prefs.getBoolean(KEY_RTL, false);
    }

    public boolean isResponsiveFont() {
        return prefs.getBoolean(KEY_RESPONSIVE_FONT, true);
    }

    public boolean isCompact() {
        return prefs.getBoolean(KEY_COMPACT, true);
    }

    public boolean isRoundedCorners() {
        return prefs.getBoolean(KEY_ROUNDED_CORNERS, false);
    }

    public void applyAppTheme() {
        String theme = getTheme();
        if (THEME_DARK.equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (THEME_SYSTEM.equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void applySettingsToActivity(Activity activity) {
        if (activity == null) return;

        // Apply Theme
        applyAppTheme();

        // Apply RTL / LTR Layout Direction
        View root = activity.findViewById(android.R.id.content);
        if (root != null) {
            root.setLayoutDirection(isRtl() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }

        // Apply Responsive Font Scale
        Resources res = activity.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.fontScale = isResponsiveFont() ? 0.95f : 1.0f;
        res.updateConfiguration(config, res.getDisplayMetrics());

        // Apply Rounded Corners to MaterialCardViews if present
        if (isRoundedCorners() && root != null) {
            applyRoundedCornersToView(root);
        }
    }

    private void applyRoundedCornersToView(View view) {
        if (view == null) return;
        if (view instanceof MaterialCardView) {
            float cornerPx = 20 * appContext.getResources().getDisplayMetrics().density;
            ((MaterialCardView) view).setRadius(cornerPx);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int idx = 0; idx < count; idx++) {
                applyRoundedCornersToView(group.getChildAt(idx));
            }
        }
    }
}