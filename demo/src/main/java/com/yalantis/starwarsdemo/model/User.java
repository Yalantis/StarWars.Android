package com.yalantis.starwarsdemo.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.yalantis.starwarsdemo.R;

/**
 * Created by Artem Kholodnyi on 11/19/15.
 */
public class User {
    private final String mFullName;
    private final boolean mDarkSide;
    private final String mHomeworld;
    private final String mBirthday;

    public User(boolean darkSide, String fullName, String homeworld, String birthday) {
        mDarkSide = darkSide;
        mFullName = fullName;
        mHomeworld = homeworld;
        mBirthday = birthday;
    }

    public @DrawableRes
    int getPhotoRes() {
        return mDarkSide ? R.drawable.darth : R.drawable.anakin;
    }

    public boolean isDarkSide() {
        return mDarkSide;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getHomeworld() {
        return mHomeworld;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public @StringRes
    int getSideText() {
        return isDarkSide() ? R.string.dark_side_label : R.string.light_side_label;
    }
}
