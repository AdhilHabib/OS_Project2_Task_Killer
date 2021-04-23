package com.AdhilHabib.project2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ProcessHelper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppAttributes> mProcessDetails;
    private OnProcessDetailClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            // Simply hold the item view
            super(itemView);
        }
    }

    public interface OnProcessDetailClickListener {
        void onProcessDetailClick(@NonNull AppAttributes processDetail);
    }

    public ProcessHelper(@NonNull List<AppAttributes> processDetails) {
        mProcessDetails = processDetails;
    }

    @Override
    public int getItemCount() {
        return mProcessDetails.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PackageAttributes processDetailView = (PackageAttributes) holder.itemView;
        final AppAttributes processDetail = mProcessDetails.get(position);
        processDetailView.setData(processDetail);
        processDetailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null) {
                    return;
                }
                mListener.onProcessDetailClick(processDetail);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.view_process_detail_inflatable,
                        parent,
                        false));
    }

    public void setOnProcessDetailClickListener(@Nullable OnProcessDetailClickListener listener) {
        mListener = listener;
    }

}
