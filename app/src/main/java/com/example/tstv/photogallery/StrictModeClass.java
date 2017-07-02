package com.example.tstv.photogallery;

import android.os.Build;
import android.os.StrictMode;

import static com.example.tstv.photogallery.BuildConfig.DEBUG;

/**
 * Created by tstv on 29.06.2017.
 */

public class StrictModeClass {
    public static void initStrictMode(){
        if (DEBUG){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectNetwork()
                        .detectResourceMismatches()
                        .detectCustomSlowCalls()
                        .penaltyDialog()
                        .penaltyLog()
                        .build()
                );
            }
        }
    }
}
