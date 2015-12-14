package com.yalantis.starwarsdemo.interfaces;

/**
 * Created by Artem Kholodnyi on 11/19/15.
 */
public interface DemoActivityInterface {
    void goToSide(int cx, int cy, boolean appBarExpanded, String side);
    void removeAllFragmentExcept(String tag);
}
