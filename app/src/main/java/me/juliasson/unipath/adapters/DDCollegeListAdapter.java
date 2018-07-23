package me.juliasson.unipath.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class DDCollegeListAdapter extends RecyclerView.Adapter<DDCollegeListAdapter.ViewHolder> {

    private List<UserDeadlineRelation> mCollegeDeadlines;
    private Context mContext;

    public DDCollegeListAdapter (List<UserDeadlineRelation> deadlines) {
        mCollegeDeadlines = deadlines;
    }

    @NonNull
    @Override
    public DDCollegeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View collegeView = inflater.inflate(R.layout.card_row, viewGroup, false);
        DDCollegeListAdapter.ViewHolder viewHolder = new DDCollegeListAdapter.ViewHolder(collegeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DDCollegeListAdapter.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mCollegeDeadlines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
