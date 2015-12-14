package com.yalantis.starwarsdemo.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.yalantis.starwarsdemo.R;
import com.yalantis.starwarsdemo.interfaces.DemoActivityInterface;
import com.yalantis.starwarsdemo.interfaces.GreetingFragmentInterface;
import com.yalantis.starwarsdemo.interfaces.TilesRendererInterface;
import com.yalantis.starwarsdemo.particlesys.ParticleSystemRenderer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Artem Kholodnyi on 11/11/15.
 */
public class DemoActivity extends AppCompatActivity implements GreetingFragmentInterface,
        DemoActivityInterface, TilesRendererInterface {
    @Bind(R.id.gl_surface_view)
    GLSurfaceView mGlSurfaceView;

    private SideFragment mDarkFragment;
    private SideFragment mBrightFragment;
    private GreetingsFragment mGreetingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            mGlSurfaceView.setEGLContextClientVersion(2);

            // Set the renderer to our demo renderer, defined below.
            ParticleSystemRenderer mRenderer = new ParticleSystemRenderer(mGlSurfaceView);
            mGlSurfaceView.setRenderer(mRenderer);
            mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        } else {
            throw new UnsupportedOperationException();
        }

        if (savedInstanceState == null) {
            showGreetings();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlSurfaceView.onResume();
    }

    private void showGreetings() {
        mGreetingsFragment = GreetingsFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_downward, 0, R.anim.slide_downward, 0)
                .add(R.id.container, mGreetingsFragment, "greetings")
                .commit();
    }

    @Override
    public void onSetupProfileClick() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_upward, 0)
                        .add(R.id.container, BrightSideFragment.newInstance(), "bright")
                        .commit();
            }
        });

    }

    @Override
    public void goToSide(int cx, int cy, boolean appBarExpanded, String side) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


        mDarkFragment = DarkSideFragment.newInstance(cx, cy, appBarExpanded);
        Fragment fragment;
        switch (side) {
            case "bright":
                fragment = BrightSideFragment.newInstance(cx, cy, appBarExpanded);
                break;
            case "dark":
                fragment = DarkSideFragment.newInstance(cx, cy, appBarExpanded);
                break;
            default:
                throw new IllegalStateException();
        }
        ft.add(R.id.container, fragment, side).commit();
    }

    @Override
    public void removeAllFragmentExcept(@Nullable String tag) {
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment frag;
        for (int i = 0; i < frags.size(); i++) {
            frag = frags.get(i);
            if (frag == null) {
                continue;
            }
            if (tag == null || !tag.equals(frag.getTag())) {
                ft.remove(frag);
            }
        }
        ft.commit();
    }

    @Override
    public void onTilesFinished() {
        showGreetings();
    }

}
