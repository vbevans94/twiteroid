package info.zametki.twitteroid;

import android.app.Application;

import com.squareup.picasso.Picasso;

import org.scribe.oauth.OAuthService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import info.zametki.twitteroid.data.ConnectionInfo;
import info.zametki.twitteroid.data.apis.TwitterApi;
import info.zametki.twitteroid.data.prefs.StringPreference;
import info.zametki.twitteroid.module.AppModule;
import info.zametki.twitteroid.util.Names;
import io.realm.RealmConfiguration;

/**
 * Author vbevans94.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Application application();

    TwitterApi twitterApi();

    ConnectionInfo connectionInfo();

    @Named(Names.OAUTH_ACESS_TOKEN)
    StringPreference accessTokenPreference();

    @Named(Names.OAUTH_REQUEST_TOKEN)
    StringPreference requestTokenPreference();

    Picasso picasso();

    OAuthService oAuthService();

    RealmConfiguration realmConfiguration();
}
