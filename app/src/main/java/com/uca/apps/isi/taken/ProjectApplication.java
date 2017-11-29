package com.uca.apps.isi.taken;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tumblr.remember.Remember;

/**
 * Created by Administrador on 09/10/2017.
 */

public class ProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        Remember.init(getApplicationContext(), "com.uca.apps.isi.taken");
    }
}