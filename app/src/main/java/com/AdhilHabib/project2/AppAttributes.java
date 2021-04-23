package com.AdhilHabib.project2;

import androidx.annotation.NonNull;
import android.graphics.drawable.Drawable;

public class AppAttributes {

    private String mApplicationName;
    private String mPackageName;
    private long mTimestamp;
    private Drawable mIcon;


    public AppAttributes(@NonNull String packageName, @NonNull String applicationName, @NonNull Drawable icon,
                         long lastUsedTimestamp) { mPackageName = packageName;mApplicationName = applicationName;mIcon = icon; mTimestamp = lastUsedTimestamp;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getApplicationName() {
        return mApplicationName;
    }

    public long getLastUsedTimestamp() {
        return mTimestamp;
    }



}
