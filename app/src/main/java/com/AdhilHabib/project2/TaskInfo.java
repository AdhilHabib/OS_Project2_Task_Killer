package com.AdhilHabib.project2;


import androidx.annotation.NonNull;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskInfo extends AsyncTask<Void, Void, List<AppAttributes>>{
    private PackageManager mPackageManager;
    private UsageStatsManager mUsageStatsManager;
    private long mEndTime;
    private long mStartTime;
    private OnTaskCompleteListener mListener;


    public interface OnTaskCompleteListener {
        void onTaskComplete(@NonNull List<AppAttributes> processDetails);
    }

    public TaskInfo(@NonNull PackageManager packageManager,
                    @NonNull UsageStatsManager usageStatsManager,
                    long startTime,
                    long endTime,
                    @NonNull OnTaskCompleteListener listener) {
        mPackageManager = packageManager;
        mUsageStatsManager = usageStatsManager;
        mListener = listener;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    @Override
    protected List<AppAttributes> doInBackground(Void... params) {
        List<AppAttributes> processDetails = new ArrayList<>();
        Map<String, UsageStats> usageStatsList = mUsageStatsManager
                .queryAndAggregateUsageStats(0, mEndTime);
        for (String packageName : usageStatsList.keySet()) {
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = mPackageManager.getPackageInfo(packageName, 0).applicationInfo;
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            long lastUsedTimestamp = usageStatsList.get(packageName).getLastTimeUsed();
            if (lastUsedTimestamp < mStartTime) {
                continue;
            }
            processDetails.add(
                    new AppAttributes(
                            applicationInfo.packageName,
                            applicationInfo.loadLabel(mPackageManager).toString(),
                            applicationInfo.loadIcon(mPackageManager),
                            lastUsedTimestamp));
        }
        return processDetails;
    }

    @Override
    protected void onCancelled(List<AppAttributes> processDetails) {
        super.onCancelled(processDetails);
        mUsageStatsManager = null;
        mListener = null;
        mPackageManager = null;
    }

    @Override
    protected void onPostExecute(List<AppAttributes> processDetails) {
        if (mListener == null) {
            return;
        }
        mListener.onTaskComplete(processDetails);
    }
}
