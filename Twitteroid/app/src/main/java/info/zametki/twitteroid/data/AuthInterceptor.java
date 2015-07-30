package info.zametki.twitteroid.data;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import info.zametki.twitteroid.controller.LoginController;
import info.zametki.twitteroid.data.prefs.StringPreference;
import info.zametki.twitteroid.util.Names;
import retrofit.RequestInterceptor;

@Singleton
public class AuthInterceptor implements Interceptor {

    public static final String HEADER_AUTHORIZATION = "Authorization";

    private final OAuthService oAuthService;
    private final StringPreference accessTokenPreference;

    @Inject
    AuthInterceptor(OAuthService oAuthService, @Named(Names.OAUTH_ACESS_TOKEN) StringPreference accessTokenPreference) {
        this.oAuthService = oAuthService;
        this.accessTokenPreference = accessTokenPreference;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (accessTokenPreference.isSet()) {
            Verb verb = Verb.valueOf(request.method());
            OAuthRequest oAuthRequest = new OAuthRequest(verb, request.urlString());
            Token accessToken = LoginController.rawToToken(accessTokenPreference.get());
            oAuthService.signRequest(accessToken, oAuthRequest);

            request = request.newBuilder()
                    .header(HEADER_AUTHORIZATION, oAuthRequest.getHeaders().get(HEADER_AUTHORIZATION))
                    .build();
        }
        return chain.proceed(request);
    }
}
