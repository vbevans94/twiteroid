package info.zametki.twitteroid.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.Component;
import info.zametki.twitteroid.AppComponent;
import info.zametki.twitteroid.MainApp;
import info.zametki.twitteroid.R;
import info.zametki.twitteroid.annotations.ActivityScope;
import info.zametki.twitteroid.controller.LoginController;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Author vbevans94.
 */
public class LoginActivity extends AppCompatActivity implements Callback<String> {

    @Inject
    LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerLoginActivity_LoginComponent.builder()
                .appComponent(MainApp.get(this).appComponent())
                .build()
                .inject(this);

        if (loginController.loggedIn()) {
            // user already obtained authorization
            goToMain();
            return;
        }

        loginController.getAuthorizationUrl(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        loginController.checkVerifier(intent, onAuthorizationSuccess);
    }

    private void goToMain() {
        startActivity(new Intent(this, TimelineActivity.class));
        finish();
    }

    @Override
    public void success(String authUrl, Response response) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        startActivity(intent);
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_LONG).show();
    }

    private final Runnable onAuthorizationSuccess = new Runnable() {
        @Override
        public void run() {
            goToMain();
        }
    };

    @ActivityScope
    @Component(dependencies = AppComponent.class)
    public interface LoginComponent {

        void inject(LoginActivity activity);
    }
}
