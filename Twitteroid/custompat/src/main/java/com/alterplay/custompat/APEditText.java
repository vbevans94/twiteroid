package com.alterplay.custompat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;


public class APEditText extends EditText {

    private final static int[] ERROR_STATE = new int[] {R.attr.state_error};

    private boolean isError;

    public APEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

        APTextView.considerTypefont(this, attrs);
        setBackgroundResource(R.drawable.edit_text_background);
	}

    @Override
    public void setBackgroundResource(int resid) {
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getResources().getDimensionPixelOffset(R.dimen.edittext_bottom_space);

        super.setBackgroundResource(resid);

        setPadding(0, paddingTop, paddingRight, paddingBottom);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isError) {
            isError = false;
        }
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] state = super.onCreateDrawableState(extraSpace + 1);
        if (isError) {
            state = mergeDrawableStates(state, ERROR_STATE);
        }
        return state;
    }

    public void setError(boolean isErrorState) {
        isError = isErrorState;
        refreshDrawableState();
    }
}