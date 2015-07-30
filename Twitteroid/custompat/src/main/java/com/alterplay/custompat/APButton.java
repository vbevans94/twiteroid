package com.alterplay.custompat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;


public class APButton extends Button {

    public APButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);
    }

    public APButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        APTextView.considerTypefont(this, attrs);
    }
}
