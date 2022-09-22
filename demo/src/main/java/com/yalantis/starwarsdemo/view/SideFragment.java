package com.yalantis.starwarsdemo.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yalantis.starwars.interfaces.TilesFrameLayoutListener;
import com.yalantis.starwarsdemo.R;
import com.yalantis.starwarsdemo.adapter.ProfileAdapter;
import com.yalantis.starwarsdemo.databinding.FragmentSideBinding;
import com.yalantis.starwarsdemo.interfaces.DemoActivityInterface;
import com.yalantis.starwarsdemo.interfaces.ProfileAdapterListener;
import com.yalantis.starwarsdemo.interfaces.TilesRendererInterface;
import com.yalantis.starwarsdemo.model.User;
import com.yalantis.starwarsdemo.widget.ClipRevealFrame;

/**
 * Created by Artem Kholodnyi on 11/19/15.
 */
public abstract class SideFragment extends Fragment implements ProfileAdapterListener,
        TilesFrameLayoutListener {
    public static final String ARG_CX = "cx";
    public static final String ARG_CY = "cy";
    public static final String ARG_SHOULD_EXPAND = "should expand";
    private static final long ANIM_DURATION = 250L;
    protected float mRadius;

    private FragmentSideBinding binding;

    private final Toolbar.OnMenuItemClickListener onMenuItemClickListener = item -> {
        if (R.id.action_close == item.getItemId()) {
            doBreak();
        }
        return false;
    };
    private TilesRendererInterface mTilesListener;
    private DemoActivityInterface mDemoActivityInterface;

    @Override
    public void onResume() {
        super.onResume();
//        mTilesFrameLayout.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.tessellationFrameLayout.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TilesRendererInterface) {
            mTilesListener = (TilesRendererInterface) context;
        }
        if (context instanceof DemoActivityInterface) {
            mDemoActivityInterface = (DemoActivityInterface) context;
        }
    }

    abstract @StyleRes
    int getTheme();

    protected Animator createCheckoutRevealAnimator(final ClipRevealFrame view, int x, int y, float startRadius, float endRadius) {
        setMenuVisibility(false);
        Animator retAnimator;
        mRadius = endRadius;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            retAnimator = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        } else {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            view.setClipRadius(startRadius);

            retAnimator = ObjectAnimator.ofFloat(view, "clipRadius", startRadius, endRadius);
        }
        retAnimator.setDuration(ANIM_DURATION);
        retAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setClipOutLines(false);
                removeOldSideFragment();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        retAnimator.setInterpolator(new AccelerateInterpolator(2.0f));
        return retAnimator;
    }

    private void removeOldSideFragment() {
        if (mDemoActivityInterface != null) {
            mDemoActivityInterface.removeAllFragmentExcept(getTagString());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSideBinding.inflate(onGetLayoutInflater(savedInstanceState).cloneInContext(new ContextThemeWrapper(getContext(), getTheme())), container, false);

        final Bundle args = getArguments();
        if (args != null) {
            binding.getRoot().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                           int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    int cx = args.getInt("cx");
                    int cy = args.getInt("cy");
                    // get the hypothenuse so the mRadius is from one corner to the other
                    float radius = (float) Math.hypot(right, bottom);

                    // Hardware-supported clipPath()
                    // http://developer.android.com/guide/topics/graphics/hardware-accel.html
                    if (Build.VERSION.SDK_INT >= 18) {
                        Animator reveal = createCheckoutRevealAnimator((ClipRevealFrame) v, cx, cy, 28f, radius);
                        reveal.start();
                    } else {
                        removeOldSideFragment();
                    }
                }
            });
        }

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tessellationFrameLayout.setOnAnimationFinishedListener(this);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setAdapter(new ProfileAdapter(getContext(), getUser(), this));
        setUpToolbar(binding.toolbar);
        binding.header.setImageResource(getUser().getPhotoRes());
        if (getArguments() != null) {
            binding.appBarLayout.setExpanded(getArguments().getBoolean(ARG_SHOULD_EXPAND), false);
        }

        binding.btnSave.setOnClickListener(v -> {
            doBreak();
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTilesListener = null;
        mDemoActivityInterface = null;
    }

    private void setUpToolbar(final Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setTitle(R.string.settings);
        toolbar.inflateMenu(R.menu.menu_star_wars);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    abstract User getUser();


    @Override
    public void onSideSwitch(SwitchCompat v) {
        Rect rect = new Rect();
        v.getGlobalVisibleRect(rect);
        final int cy = rect.centerY() - getStatusBarHeight();
        final int halfThumbWidth = v.getThumbDrawable().getIntrinsicWidth() / 2;
        final int cx;

        if (this instanceof BrightSideFragment && v.isChecked()) {
            cx = rect.right - halfThumbWidth;
            postGoToSide(cy, cx, "dark");
        } else if (!v.isChecked()) {
            cx = rect.left + halfThumbWidth;
            postGoToSide(cy, cx, "bright");
        }
    }

    private void postGoToSide(final int cy, final int cx, final String side) {
        new android.os.Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mDemoActivityInterface != null) {
                    mDemoActivityInterface.goToSide(cx, cy, isAppBarExpanded(), side);
                }
            }
        });
    }

    private boolean isAppBarExpanded() {
        return binding.appBarLayout.getBottom() == binding.appBarLayout.getHeight();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void doBreak() {
        if (mDemoActivityInterface != null) {
            mDemoActivityInterface.removeAllFragmentExcept(getTagString());
        }
        binding.tessellationFrameLayout.startAnimation();
    }

    @Override
    public void onAnimationFinished() {
        if (mTilesListener != null) {
            mTilesListener.onTilesFinished();
        }
    }

    public abstract String getTagString();
}
