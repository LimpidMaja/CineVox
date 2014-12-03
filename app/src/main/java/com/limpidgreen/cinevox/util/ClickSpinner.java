package com.limpidgreen.cinevox.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

/**
 * Created by Maja on 28/11/14.
 */
public class ClickSpinner extends Spinner {

    public ClickSpinner(Context context) {
        super(context);
    }

    public ClickSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void
    setSelection(int position) {
        Log.i(Constants.TAG, "POSITION ON SELECT:" + position);
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected) {
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

}
