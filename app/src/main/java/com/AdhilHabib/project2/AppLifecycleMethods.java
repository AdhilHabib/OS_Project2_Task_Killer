package com.AdhilHabib.project2;

import android.app.ActivityManager;
import android.app.usage.UsageStatsManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.AdhilHabib.project2.ProcessHelper.OnProcessDetailClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppLifecycleMethods extends Fragment {

    @ProcessType
    private int mProcessType;
    private ActivityManager mActivityManager;
    private List<AppAttributes> mProcessDetails = new ArrayList<>();
    private ProcessHelper mAdapter;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            ProcessType.ALL,
            ProcessType.RECENT})
    public @interface ProcessType {
        int ALL = 1;
        int RECENT = 4;
    }

    private static final String ARG_PROCESS_TYPE = "argProcessType";

    private static final String[] IGNORED_SYSTEM_PACKAGES = new String[] {
            "android",
            "com.android.systemui"
    };

    private static Comparator<AppAttributes> ALPHABETIC_COMPARATOR = new Comparator<AppAttributes>() {
        @Override
        public int compare(AppAttributes lhs, AppAttributes rhs) {
            return lhs.getApplicationName().compareTo(rhs.getApplicationName());
        }
    };

    private static Comparator<AppAttributes> TIMESTAMP_COMPARATOR = new Comparator<AppAttributes>() {
        @Override
        public int compare(AppAttributes lhs, AppAttributes rhs) {
            return Long.compare(
                    rhs.getLastUsedTimestamp(),
                    lhs.getLastUsedTimestamp());
        }
    };


    @Nullable
    private TaskInfo mProcessDetailTask;
    private PackageManager mPackageManager;
    private List<String> mIgnoredPackages = new ArrayList<>();
    private UsageStatsManager mUsageStatsManager;
    private Views mViews;

    static class Views {
        RecyclerView recyclerView;
        View spinner;
        Views(View root) {
            recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
            spinner = root.findViewById(R.id.spinner);
        }
    }

    public static AppLifecycleMethods newInstance(@ProcessType int processType) {
        AppLifecycleMethods fragment = new AppLifecycleMethods();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PROCESS_TYPE, processType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsageStatsManager = (UsageStatsManager) getActivity().getApplicationContext().getSystemService(AppCompatActivity.USAGE_STATS_SERVICE);
        mIgnoredPackages.add(getActivity().getPackageName());
        mIgnoredPackages.addAll(Arrays.asList(IGNORED_SYSTEM_PACKAGES));
        mProcessType = getArguments().getInt(ARG_PROCESS_TYPE, ProcessType.RECENT);
        mActivityManager = (ActivityManager) getActivity().getApplicationContext().getSystemService(AppCompatActivity.ACTIVITY_SERVICE);
        mPackageManager = getActivity().getApplicationContext().getPackageManager();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_process, container, false);
        mViews = new Views(root);
        BuildView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViews = null;
        if (mProcessDetailTask != null) {
            mProcessDetailTask.cancel(true);
            mProcessDetailTask = null;
        }
    }


    private void refreshData() {
        long now = Calendar.getInstance().getTimeInMillis();
        long start;
        switch (mProcessType) {
            case ProcessType.ALL:
                start = 0;
                break;
            case ProcessType.RECENT:
            default:
                start = now - TimeUnit.MINUTES.toMillis(10);
                break;
        }
        showProgress(mProcessDetails.isEmpty());

        if (mProcessDetailTask != null) {
            mProcessDetailTask.cancel(true);
        }
        mProcessDetailTask = new TaskInfo(mPackageManager, mUsageStatsManager, start, now,
                new TaskInfo.OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(@NonNull List<AppAttributes> processDetails) {
                        if (getView() == null) {
                            return;
                        }
                        mProcessDetailTask = null;
                        showProgress(false);
                        mProcessDetails.clear();
                        for (AppAttributes processDetail : processDetails) {
                            if (mIgnoredPackages.contains(processDetail.getPackageName())) {
                                continue;
                            }

                            mProcessDetails.add(processDetail);
                        }
                        Collections.sort(
                                mProcessDetails,
                                mProcessType == ProcessType.RECENT ?
                                        TIMESTAMP_COMPARATOR :
                                        ALPHABETIC_COMPARATOR);
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );
        mProcessDetailTask.execute();
    }

    private void BuildView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false);
        mAdapter = new ProcessHelper(mProcessDetails);
        mViews.recyclerView.setAdapter(mAdapter);
        mViews.recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter.setOnProcessDetailClickListener(new OnProcessDetailClickListener() {
            @Override
            public void onProcessDetailClick(@NonNull final AppAttributes processDetail) {
                mActivityManager.killBackgroundProcesses(processDetail.getPackageName());
                Snackbar.make(mViews.recyclerView, R.string.snackbar_process_killed_message, Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void showProgress(boolean prog) {
        mViews.spinner.setVisibility( prog ? View.VISIBLE : View.GONE);
    }

}
