package dashing.dual.com.eregister;

import android.app.Application;
import android.content.Context;

/**
 * Created by ashishrawat on 6/6/16.
 */
public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}