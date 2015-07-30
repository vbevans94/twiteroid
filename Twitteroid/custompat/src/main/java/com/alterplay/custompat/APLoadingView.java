package com.alterplay.custompat;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 02.04.15.
 */
public class APLoadingView extends ImageView {

    public APLoadingView(Context context) {
        this(context, null);
    }

    public APLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public APLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.APLoadingView, 0, 0);
        boolean run = array.getBoolean(R.styleable.APLoadingView_init_run, false);
        array.recycle();

        if (run) {
            show();
        }
    }

    public void show() {
        setVisibility(View.VISIBLE);
        RotateAnimation r = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration(1000);
        r.setInterpolator(new LinearInterpolator());
        r.setRepeatCount(Animation.INFINITE);
        startAnimation(r);
    }

    public void hide() {
        clearAnimation();
        setVisibility(View.GONE);
    }
}
