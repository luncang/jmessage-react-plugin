package com.sample.application;

import android.app.Application;
import android.util.Log;

import com.facebook.react.ReactApplication;

import cn.jpush.im.android.api.JMessageClient;
import io.jchat.android.JMessageReactPackage;

import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.honaf.dialog.MyDialogPackage;
import com.horcrux.svg.SvgPackage;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new JMessageReactPackage(),
                    new MyDialogPackage(),
                    new SvgPackage()
            );
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);
        Log.i("MainApplication", "Init JMessageClient");
        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext());
    }
}
