package vn.edu.mku.portal;

import android.app.Application;
import android.content.Context;

import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.local.SessionManager;
import vn.edu.mku.portal.data.local.SettingsManager;

public class MKUApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        vn.edu.mku.portal.data.crash.CrashReporter.init(this);
        LanguageManager.init(this);
        SessionManager.init(this);
        SettingsManager.init(this);
        vn.edu.mku.portal.data.local.AppCacheManager.init(this);
        vn.edu.mku.portal.data.local.DraftManager.init(this);
        vn.edu.mku.portal.ui.common.NetworkMonitor.init(this);
        SettingsManager.getInstance().applyAppTheme();
    }

    public static Context getAppContext() {
        return appContext;
    }
}