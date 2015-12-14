package com.yalantis.starwarsdemo.view;

import android.os.Bundle;

import com.yalantis.starwarsdemo.R;
import com.yalantis.starwarsdemo.model.User;

/**
 * Created by Artem Kholodnyi on 11/17/15.
 */
public class DarkSideFragment extends SideFragment {

    public static DarkSideFragment newInstance(int centerX, int centerY, boolean appBarExpanded) {
        Bundle args = new Bundle();
        args.putInt(ARG_CX, centerX);
        args.putInt(ARG_CY, centerY);
        args.putBoolean(ARG_SHOULD_EXPAND, appBarExpanded);

        DarkSideFragment frag = newInstance();
        frag.setArguments(args);

        return frag;
    }

    public static DarkSideFragment newInstance() {
        return new DarkSideFragment();
    }


    @Override
    int getTheme() {
        return R.style.StarWarsAppThemeDark;
    }

    @Override
    User getUser() {
        return new User(true, "Darth Vader", "Tatooine", "41.9 BBY");
    }

    @Override
    public String getTagString() {
        return "dark";
    }
}
