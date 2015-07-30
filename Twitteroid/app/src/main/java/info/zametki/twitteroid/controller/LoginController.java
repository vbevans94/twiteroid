package info.zametki.twitteroid.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Named;

import info.zametki.twitteroid.annotations.ActivityScope;
import info.zametki.twitteroid.data.ConnectionInfo;
import info.zametki.twitteroid.data.prefs.StringPreference;
import info.zametki.twitteroid.module.ApiModule;
import info.zametki.twitteroid.util.Names;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Author vbevans94.
 */
@ActivityScope
public class LoginController {

    private final OAuthService service;
    private final ConnectionInfo connectionInfo;
    private final StringPreference requestTokenPreference;
    private final StringPreference accessTokenPreference;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Inject
    public LoginController(OAuthService service, ConnectionInfo connectionInfo,
                           @Named(Names.OAUTH_REQUEST_TOKEN) StringPreference requestTokenPreference,
                           @Named(Names.OAUTH_ACESS_TOKEN) StringPreference accessTokenPreference) {
        this.service = service;
        this.connectionInfo = connectionInfo;
        this.requestTokenPreference = requestTokenPreference;
        this.accessTokenPreference = accessTokenPreference;
    }

    /**
     * Starts authorization process by obtaining authorization url.
     *
     * @param callback to handle the result
     */
    public void getAuthorizationUrl(final Callback<String> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (connectionInfo.isNetworkAvailable()) {
                    Token requestToken = service.getRequestToken();
                    // persist for future authorization steps
                    requestTokenPreference.set(requestToken.getRawResponse());

                    final String authUrl = service.getAuthorizationUrl(requestToken);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(authUrl, null);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.failure(RetrofitError.networkError("", new IOException()));
                        }
                    });
                }
            }
        });
    }

    /**
     * Checks whether this intent is not redirect from Twitter with the verifier.
     *
     * @param intent          to check
     * @param successCallback to handle successful authorization
     */
    public void checkVerifier(Intent intent, final Runnable successCallback) {
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null && ApiModule.CALLBACK.contains(data.getHost())) {
                final Verifier verifier = new Verifier(data.getQueryParameter(OAuthConstants.VERIFIER));
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Token requestToken = rawToToken(requestTokenPreference.get());

                        Token accessToken = service.getAccessToken(requestToken, verifier);

                        accessTokenPreference.set(accessToken.getRawResponse());

                        handler.post(successCallback);
                    }
                });
            }
        }
    }

    public static Token rawToToken(String rawResponse) {
        Uri uri = Uri.parse(ApiModule.CALLBACK + "?" + rawResponse);
        String token = uri.getQueryParameter(OAuthConstants.TOKEN);
        String secret = uri.getQueryParameter(OAuthConstants.TOKEN_SECRET);

        return new Token(token, secret);
    }

    public boolean loggedIn() {
        return accessTokenPreference.isSet();
    }
}
