package info.zametki.twitteroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.zametki.twitteroid.MainApp;
import info.zametki.twitteroid.R;

public class MediaActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGE_URL = "extra_image_url";
    private static final String TWEET_NAME_FORMAT = "tweet%d.png";

    @InjectView(R.id.image_full)
    ImageView imageFull;

    public static void start(Context context, String imageUrl) {
        Intent intent = new Intent(context, MediaActivity.class);
        intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_media);

        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        MainApp.get(this).appComponent().picasso().load(imageUrl)
                .error(R.drawable.loading_error_black)
                .into(imageFull);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_save:
                saveToSdCard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToSdCard() {
        BitmapDrawable drawable = (BitmapDrawable) imageFull.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File sdCardDirectory = Environment.getExternalStorageDirectory();

        File image = new File(sdCardDirectory, String.format(TWEET_NAME_FORMAT, System.currentTimeMillis()));

        boolean success = false;

        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            outStream.flush();
            outStream.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        int message = R.string.message_saved;
        if (!success) {
            message = R.string.message_save_failed;
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
