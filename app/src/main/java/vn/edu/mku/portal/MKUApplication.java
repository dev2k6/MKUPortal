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

        LanguageManager.init(this);
        SessionManager.init(this);
        SettingsManager.init(this);
        SettingsManager.getInstance().applyAppTheme();
    }

    public static Context getAppContext() {
        return appContext;
    }
}