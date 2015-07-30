package info.zametki.twitteroid.module;

import android.content.SharedPreferences;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.squareup.okhttp.OkHttpClient;

import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.zametki.twitteroid.data.apis.TwitterApi;
import info.zametki.twitteroid.data.model.Coordinates;
import info.zametki.twitteroid.data.model.RealmFloat;
import info.zametki.twitteroid.data.prefs.StringPreference;
import info.zametki.twitteroid.util.Names;
import io.realm.RealmList;
import io.realm.RealmObject;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Author vbevans94.
 */
@Module
@Singleton
public class ApiModule {

    public static final String CALLBACK = "twitteroid://callback";
    private static String CONSUMER_KEY = "TPZXfg0FgZ48iEMsn3OxQ";
    private static String CONSUMER_SECRET = "qfJF0xH5JjwRXuc7gPYB7DAL1p6ZUOgPVG1j9T9zY";
    private static final String TWITTER_API_URL = "https://api.twitter.com/1.1";

    @Provides
    RestAdapter provideRestAdapter(Gson gson, Endpoint endpoint, OkHttpClient client) {
        return new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setConverter(new GsonConverter(gson))
                .setEndpoint(endpoint)
                .build();
    }

    @Provides
    TwitterApi provideTwitterApi(RestAdapter adapter) {
        return adapter.create(TwitterApi.class);
    }

    @Provides
    OAuthService provideService() {
        return new ServiceBuilder()
                .provider(org.scribe.builder.api.TwitterApi.class)
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .callback(CALLBACK)
                .build();
    }

    @Provides
    @Named(Names.OAUTH_ACESS_TOKEN)
    StringPreference provideAccessTokenPreference(SharedPreferences preferences) {
        return new StringPreference(preferences, Names.OAUTH_ACESS_TOKEN);
    }

    @Provides
    @Named(Names.OAUTH_REQUEST_TOKEN)
    StringPreference provideRequestTokenPreference(SharedPreferences preferences) {
        return new StringPreference(preferences, Names.OAUTH_REQUEST_TOKEN);
    }

    @Provides
    Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(TWITTER_API_URL);
    }

    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(new TypeToken<RealmList<Coordinates>>() {
                }.getType(), new TypeAdapter<RealmList<Coordinates>>() {

                    @Override
                    public void write(JsonWriter out, RealmList<Coordinates> value) throws IOException {
                        // Ignore
                    }

                    @Override
                    public RealmList<Coordinates> read(JsonReader in) throws IOException {
                        RealmList<Coordinates> list = new RealmList<>();
                        in.beginArray();
                        in.beginArray();
                        while (in.hasNext()) {
                            in.beginArray();
                            Coordinates coordinates = new Coordinates(in.nextDouble(), in.nextDouble());
                            list.add(coordinates);
                            in.endArray();
                        }
                        in.endArray();
                        in.endArray();
                        return list;
                    }
                })
                .setDateFormat("EEE MMM d HH:mm:ss +0000 y")
                .create();
    }
}
