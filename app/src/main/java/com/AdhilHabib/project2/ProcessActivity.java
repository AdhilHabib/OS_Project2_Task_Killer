package com.AdhilHabib.project2;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import 	androidx.appcompat.app.AppCompatActivity;
import com.AdhilHabib.project2.AppLifecycleMethods.ProcessType;

import java.util.ArrayList;
import java.util.List;


public class ProcessActivity extends AppCompatActivity {

    private Views entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        entry = new Views(findViewById(R.id.main_content));
        setSupportActionBar(entry.toolbar);
        BuildPage();
        getSystemPermission();
    }

    private void getSystemPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.enable_usage_stats_title)
                .setMessage(getString(R.string.enable_usage_stats_message))
                .setPositiveButton(R.string.enable_usage_stats_ok_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
    }

    static class Views {
        TabLayout tabLayout;
        Toolbar toolbar;
        ViewPager viewPager;
        Views(View root) {
            tabLayout = root.findViewById(R.id.tabs);
            toolbar = root.findViewById(R.id.toolbar);
            viewPager = root.findViewById(R.id.view_pager);
        }
    }



    private void BuildPage() {
        List<RowElement> rowElements = new ArrayList<>();
        rowElements.add(new RowElement(AppLifecycleMethods.newInstance(ProcessType.RECENT), getString(R.string.process_type_recent)));
        entry.viewPager.setAdapter(new TabHelper(getSupportFragmentManager(), rowElements));
        entry.tabLayout.setupWithViewPager(entry.viewPager);
    }


    private static class TabHelper extends FragmentPagerAdapter {

        private List<RowElement> mRowElements;

        public TabHelper(@NonNull FragmentManager fm,
                         @NonNull List<RowElement> rowElements) {
            super(fm);
            mRowElements = rowElements;
        }

        @Override
        public Fragment getItem(int position) {
            return mRowElements.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return mRowElements.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mRowElements.get(position).getLabel();
        }

    }

    private static class RowElement {

        private Fragment mFragment;
        private String mLabel;

        public RowElement(@NonNull Fragment frag,
                          @NonNull String label) {
            mFragment = frag;
            mLabel = label;
        }

        public Fragment getFragment() {
            return mFragment;
        }

        public String getLabel() {
            return mLabel;
        }

    }

}
