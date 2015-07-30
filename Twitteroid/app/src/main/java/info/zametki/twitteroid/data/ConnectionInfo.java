package info.zametki.twitteroid.data;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Author vbevans94.
 */
@Singleton
public class ConnectionInfo {

    private final Application application;

    @Inject
    public ConnectionInfo(Application application) {
        this.application = application;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = connectivityManager.getActiveNetworkInfo();
        return i != null && i.isConnected() && i.isAvailable();
    }
}
