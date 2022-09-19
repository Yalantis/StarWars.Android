package com.yalantis.starwarsdemo;

import android.app.Application;
import android.content.Context;
import timber.log.Timber;

/**
 * Created by Artem Kholodnyi on 11/2/15.
 */
public class App extends Application{
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Timber.plant(new Timber.DebugTree());
    }

    public static Context getAppContext() {
        return app;
    }
}
