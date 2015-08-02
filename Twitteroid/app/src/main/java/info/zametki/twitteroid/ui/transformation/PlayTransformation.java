package info.zametki.twitteroid.ui.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @since 08.07.15.
 */
public class PlayTransformation implements Transformation {

    private final Bitmap playBitmap;

    public PlayTransformation(Context context, int resId) {
        playBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(source, 0, 0, paint);
        int left = (canvas.getWidth() - playBitmap.getWidth()) / 2;
        int top = (canvas.getHeight() - playBitmap.getHeight()) / 2;
        canvas.drawBitmap(playBitmap, left, top, paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "play_transform)";
    }
}