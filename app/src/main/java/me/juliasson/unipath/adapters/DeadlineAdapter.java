package me.juliasson.unipath.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.utils.DateTimeUtils;

public class DeadlineAdapter extends RecyclerView.Adapter<DeadlineAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Deadline> deadlines;

    public DeadlineAdapter(ArrayList<Deadline> deadlines) {
        this.deadlines = deadlines;
    }

    @NonNull
    @Override
    public DeadlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View deadlineView = inflater.inflate(R.layout.item_collegedetails_deadline, viewGroup, false);
        DeadlineAdapter.ViewHolder viewHolder = new DeadlineAdapter.ViewHolder(deadlineView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeadlineAdapter.ViewHolder viewHolder, int i) {
        final Deadline deadline = deadlines.get(i);
        viewHolder.tvDeadlineDesc.setText(deadline.getDescription());
        viewHolder.tvDeadlineDate.setText(DateTimeUtils.parseDateTime(deadline.getDeadlineDate().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));

        if (deadline.getIsFinancial()) {
            Glide.with(mContext)
                    .load(R.drawable.ic_attach_money)
                    .into(viewHolder.ivDeadlineIcon);
        }
    }

    @Override
    public int getItemCount() {
        return deadlines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDeadlineDate;
        public TextView tvDeadlineDesc;
        public ImageView ivDeadlineIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDeadlineDate = itemView.findViewById(R.id.tvDeadlineDate);
            tvDeadlineDesc = itemView.findViewById(R.id.tvDeadlineDesc);
            ivDeadlineIcon = itemView.findViewById(R.id.ivDeadlineIcon);

            //itemView.setOnClickListener(this);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        deadlines.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Deadline> list) {
        deadlines.addAll(list);
        notifyDataSetChanged();
    }
}
