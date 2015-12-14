package com.yalantis.starwarsdemo.view;

import android.os.Bundle;

import com.yalantis.starwarsdemo.R;
import com.yalantis.starwarsdemo.model.User;

/**
 * Created by Artem Kholodnyi on 11/17/15.
 */
public class BrightSideFragment extends SideFragment {

    public static BrightSideFragment newInstance(int centerX, int centerY, boolean appBarExpanded) {
        Bundle args = new Bundle();
        args.putInt(ARG_CX, centerX);
        args.putInt(ARG_CY, centerY);
        args.putBoolean(ARG_SHOULD_EXPAND, appBarExpanded);

        BrightSideFragment frag = newInstance();
        frag.setArguments(args);

        return frag;
    }

    public static BrightSideFragment newInstance() {
        return new BrightSideFragment();
    }

    @Override
    int getTheme() {
        return R.style.StarWarsAppThemeLight;
    }

    @Override
    User getUser() {
        return new User(false, "Anakin Skywalker", "Tatooine", "41.9 BBY");
    }

    @Override
    public String getTagString() {
        return "bright";
    }
}
