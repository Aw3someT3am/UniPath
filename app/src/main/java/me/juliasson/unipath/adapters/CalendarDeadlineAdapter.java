package me.juliasson.unipath.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class CalendarDeadlineAdapter extends RecyclerView.Adapter<CalendarDeadlineAdapter.ViewHolder> {

    private Context mContext;
    private List<UserDeadlineRelation> relations;
    private Date currentDate = Calendar.getInstance().getTime();

    public CalendarDeadlineAdapter(List<UserDeadlineRelation> deadlines) {
        this.relations = deadlines;
    }

    @NonNull
    @Override
    public CalendarDeadlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View deadlineView = inflater.inflate(R.layout.item_deadline_card, viewGroup, false);
        CalendarDeadlineAdapter.ViewHolder viewHolder = new CalendarDeadlineAdapter.ViewHolder(deadlineView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarDeadlineAdapter.ViewHolder viewHolder, int i) {
        final UserDeadlineRelation relation = relations.get(i);

        Date relationDate = relation.getDeadline().getDeadlineDate();

        if (currentDate.after(relationDate)) {
            if (!relation.getCompleted()) {
                viewHolder.deadlineStatusIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_missed));
            } else {
                viewHolder.deadlineStatusIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_active));
            }
        } else {
            if (!relation.getCompleted()) {
                viewHolder.deadlineStatusIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_inactive));
            } else {
                viewHolder.deadlineStatusIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_active));
            }
        }

        viewHolder.tvDeadlineDesc.setText(String.format("%s\n%s", relation.getCollege().getCollegeName(), relation.getDeadline().getDescription()));
//        viewHolder.tvDeadlineDate.setText(DateTimeUtils.parseDateTime(deadline.getDeadlineDate().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));

//        if (deadline.getIsFinancial()) {
//            Glide.with(mContext)
//                    .load(R.drawable.ic_attach_money)
//                    .into(viewHolder.ivDeadlineIcon);
//        }
    }

    @Override
    public int getItemCount() {
        return relations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDeadlineDesc;
        public ImageView deadlineImage;
        public ImageView deadlineStatusIndicator;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDeadlineDesc = itemView.findViewById(R.id.tvDeadlineDesc);
            deadlineImage = itemView.findViewById(R.id.image);
            deadlineStatusIndicator = itemView.findViewById(R.id.deadlineStatusIndicator);
            //itemView.setOnClickListener(this);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        relations.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<UserDeadlineRelation> list) {
        relations.addAll(list);
        notifyDataSetChanged();
    }
}
