package me.juliasson.unipath.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.juliasson.unipath.R;

public class CalendarDeadlineAdapter extends RecyclerView.Adapter<CalendarDeadlineAdapter.ViewHolder> {

    private Context mContext;
    private List<String> deadlines;

    public CalendarDeadlineAdapter(List<String> deadlines) {
        this.deadlines = deadlines;
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
        final String deadline = deadlines.get(i);

        viewHolder.tvDeadlineDesc.setText(deadline);
//        viewHolder.tvDeadlineDate.setText(DateTimeUtils.parseDateTime(deadline.getDeadlineDate().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));

//        if (deadline.getIsFinancial()) {
//            Glide.with(mContext)
//                    .load(R.drawable.ic_attach_money)
//                    .into(viewHolder.ivDeadlineIcon);
//        }
    }

    @Override
    public int getItemCount() {
        return deadlines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDeadlineDesc;
        public ImageView deadlineImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDeadlineDesc = itemView.findViewById(R.id.tvDeadlineDesc);
            deadlineImage = itemView.findViewById(R.id.image);

            //itemView.setOnClickListener(this);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        deadlines.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<String> list) {
        deadlines.addAll(list);
        notifyDataSetChanged();
    }
}
