package vn.edu.mku.portal.ui.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkMonitor {

    private static NetworkMonitor instance;
    private final Context context;
    private final MutableLiveData<Boolean> isConnectedLiveData = new MutableLiveData<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isCurrentlyConnected = true;

    private NetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
        this.isCurrentlyConnected = checkInitialConnection();
        this.isConnectedLiveData.postValue(this.isCurrentlyConnected);
        registerNetworkCallback();
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new NetworkMonitor(context);
        }
    }

    public static synchronized NetworkMonitor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NetworkMonitor is not initialized. Call init(context) first.");
        }
        return instance;
    }

    private boolean checkInitialConnection() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return true;

            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork == null) return false;

            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        } catch (Exception e) {
            return true;
        }
    }

    private void registerNetworkCallback() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return;

            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();

            cm.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    isCurrentlyConnected = true;
                    mainHandler.post(() -> isConnectedLiveData.setValue(true));
                }

                @Override
                public void onLost(@NonNull Network network) {
                    boolean stillConnected = checkInitialConnection();
                    isCurrentlyConnected = stillConnected;
                    mainHandler.post(() -> isConnectedLiveData.setValue(stillConnected));
                }
            });
        } catch (Exception ignored) {}
    }

    public boolean isOnline() {
        return isCurrentlyConnected;
    }

    public LiveData<Boolean> getIsConnectedLiveData() {
        return isConnectedLiveData;
    }
}
