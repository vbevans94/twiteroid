package info.zametki.twitteroid.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.zametki.twitteroid.data.AuthInterceptor;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Author vbevans94.
 */
@Module(includes = ApiModule.class)
public class DataModule {

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    RealmConfiguration provideRealmConfiguration(Application application) {
        return new RealmConfiguration.Builder(application)
                .build();
    }

    @Provides
    OkHttpClient provideClient(Application application, AuthInterceptor interceptor) {
        OkHttpClient client = new OkHttpClient();
        try {
            File cacheDir = new File(application.getCacheDir(), "http");
            client.setCache(new Cache(cacheDir, DISK_CACHE_SIZE));
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }
        client.interceptors().add(interceptor);

        return client;
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application application, OkHttpClient client) {
        return new Picasso.Builder(application)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Timber.e(e, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }
}
