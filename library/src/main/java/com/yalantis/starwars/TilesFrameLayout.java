package com.yalantis.starwars;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yalantis.starwars.interfaces.TilesFrameLayoutListener;
import com.yalantis.starwars.render.StarWarsRenderer;
import com.yalantis.starwars.widget.StarWarsTilesGLSurfaceView;

/**
 * Created by Artem Kholodnyi on 11/9/15.
 */
public class TilesFrameLayout extends FrameLayout {

    private StarWarsTilesGLSurfaceView mGLSurfaceView;
    private StarWarsRenderer mRenderer;
    private TilesFrameLayoutListener mListener;
    private int mAnimationDuration;
    private int mNumberOfTilesX;

    public TilesFrameLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TilesFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TilesFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TilesFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.sw_StarWarsTilesFrameLayout, defStyle, 0);

        mAnimationDuration = a.getInt(R.styleable.sw_StarWarsTilesFrameLayout_sw_animationDuration, 1500);
        mNumberOfTilesX = a.getInt(R.styleable.sw_StarWarsTilesFrameLayout_sw_numberOfTilesX, 35);

        a.recycle();
        initGlSurfaceView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(mGLSurfaceView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void initGlSurfaceView() {
        mGLSurfaceView = new StarWarsTilesGLSurfaceView(getContext());
        mGLSurfaceView.setBackgroundColor(Color.TRANSPARENT);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            mRenderer = new StarWarsRenderer(mGLSurfaceView, this, mAnimationDuration, mNumberOfTilesX);
            mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
            mGLSurfaceView.setRenderer(mRenderer);
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            mGLSurfaceView.setZOrderOnTop(true);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void startAnimation() {
        drawToTexture();
    }

    private void drawToTexture() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        super.draw(canvas);
        mRenderer.updateTexture(bitmap);
    }

    public void reveal() {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mRenderer.startAnimation();
                        removeViewAt(0);
                    }
                });
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void onResume() {
        mGLSurfaceView.onResume();
    }

    public void onPause() {
        mGLSurfaceView.onPause();
    }

    public void onAnimationFinished() {
        if (mListener != null) {
            mListener.onAnimationFinished();
        }
    }

    public void setOnAnimationFinishedListener(TilesFrameLayoutListener listener) {
        this.mListener = listener;
    }
}
