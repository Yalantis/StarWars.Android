package com.yalantis.starwarsdemo.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Artem Kholodnyi on 11/23/15.
 */
public class CenterTopImageView extends ImageView {
    private Matrix matrix = new Matrix();

    public CenterTopImageView(Context context) {
        super(context);
        init();
    }

    public CenterTopImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CenterTopImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getDrawable() != null) {
            float k = (right - left) / (float)getDrawable().getIntrinsicWidth();
            matrix.setScale(k, k);
            setImageMatrix(matrix);
        }
    }

}
