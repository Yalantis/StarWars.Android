package com.yalantis.starwars.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.yalantis.starwars.render.StarWarsRenderer;

/**
 * Created by Artem Kholodnyi on 11/3/15.
 */
public class StarWarsTilesGLSurfaceView extends GLSurfaceView {
    private StarWarsRenderer mRenderer;

    public StarWarsTilesGLSurfaceView(Context context) {
        super(context);
    }

    public StarWarsTilesGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRenderer.cancelAnimation();
    }

    public void setRenderer(StarWarsRenderer renderer) {
        super.setRenderer(renderer);
        mRenderer = renderer;
    }

}
