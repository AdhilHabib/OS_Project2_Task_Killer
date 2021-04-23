package com.AdhilHabib.project2;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class PackageAttributes extends FrameLayout {
    private Views entry;
    static class Views {
        TextView packageName;
        ImageView AppThumbnail;
        TextView AppName;
        Views(View source) {
            AppName = source.findViewById(R.id.application_name);
            packageName = source.findViewById(R.id.package_name);
            AppThumbnail = source.findViewById(R.id.icon);
        }
    }

    public PackageAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize();
    }

    private void Initialize() {
        inflate(getContext(), R.layout.view_process_detail, this);
        entry = new Views(this);
    }

    public void setData(@NonNull AppAttributes processDetail) {
        entry.AppName.setText(processDetail.getApplicationName());
        entry.packageName.setText(processDetail.getPackageName());
        entry.AppThumbnail.setImageDrawable(processDetail.getIcon());
    }
    public PackageAttributes(Context context) {
        super(context);
        Initialize();
    }

    public PackageAttributes(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initialize();
    }

}
