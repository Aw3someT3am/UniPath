package me.juliasson.unipath.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;

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

        View collegeView = inflater.inflate(R.layout.item_deadlinedetails_college, viewGroup, false);
        DDCollegeListAdapter.ViewHolder viewHolder = new DDCollegeListAdapter.ViewHolder(collegeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DDCollegeListAdapter.ViewHolder viewHolder, int position) {
        final UserDeadlineRelation relation = mCollegeDeadlines.get(position);
        viewHolder.tvCollegeName.setText(relation.getCollege().getCollegeName());
        viewHolder.tvDeadlineDescription.setText(relation.getDeadline().getDescription());
        viewHolder.lbCheckBox.setLiked(relation.getCompleted());

        viewHolder.lbCheckBox.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                relation.setCompleted(true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                relation.setCompleted(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCollegeDeadlines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCollegeName;
        TextView tvDeadlineDescription;
        LikeButton lbCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
            tvDeadlineDescription = itemView.findViewById(R.id.tvDeadlineDesc);
            lbCheckBox = itemView.findViewById(R.id.lbLikeButton);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mCollegeDeadlines.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<UserDeadlineRelation> list) {
        mCollegeDeadlines.addAll(list);
        notifyDataSetChanged();
    }
}
