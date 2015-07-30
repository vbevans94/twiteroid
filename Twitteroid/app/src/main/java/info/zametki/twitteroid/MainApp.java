package info.zametki.twitteroid;

import android.app.Application;
import android.content.Context;

import info.zametki.twitteroid.module.AppModule;
import io.realm.Realm;
import timber.log.Timber;

/**
 * Author vbevans94.
 */
public class MainApp extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // set up logs
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // build graph
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        // set up Realm
        Realm.setDefaultConfiguration(appComponent.realmConfiguration());
    }

    public static MainApp get(Context context) {
        return (MainApp) context.getApplicationContext();
    }

    public AppComponent appComponent() {
        return appComponent;
    }
}
