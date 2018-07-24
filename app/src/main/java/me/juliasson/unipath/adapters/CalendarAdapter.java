package me.juliasson.unipath.adapters;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

import me.juliasson.unipath.model.UserDeadlineRelation;

public class CalendarAdapter {

    private List<UserDeadlineRelation> mCalendarList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CalendarAdapter(List<UserDeadlineRelation> list) {
        mCalendarList = list;
    }
//
//    @NonNull
//    @Override
//    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//        mContext = viewGroup.getContext();
//        mLayoutInflater = LayoutInflater.from(mContext);
//        View view;
//        view = mLayoutInflater.inflate(R.layout.item_timeline, viewGroup, false);
//        return new CalendarAdapter.ViewHolder(view, viewType);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TimeLineAdapter.ViewHolder viewHolder, int position) {
//        TimeLine timeLineModel = mCalendarList.get(position);
//
//        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
//            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
//        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
//            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
//        } else {
//            viewHolder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
//        }
//
//        if(!timeLineModel.getDate().isEmpty()) {
//            viewHolder.mDate.setVisibility(View.VISIBLE);
//            viewHolder.mDate.setText(DateTimeUtils.parseDateTime(timeLineModel.getDate(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
//        }
//        else
//            viewHolder.mDate.setVisibility(View.GONE);
//
//        viewHolder.mMessage.setText(timeLineModel.getMessage());
//    }
//
//    @Override
//    public int getItemCount() {
//        return mCalendarList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        public ViewHolder(View itemView, int viewType) {
//            super(itemView);
//
//            ButterKnife.bind(this, itemView);
////            mTimelineView.initLine(viewType);
//
//            itemView.setOnClickListener(this);
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return TimelineView.getTimeLineViewType(position, getItemCount());
//    }
//
//    // Clean all elements of the recycler
//    public void clear() {
//        mCalendarList.clear();
//        notifyDataSetChanged();
//    }
//
//    // Add a list of items -- change to type used
//    public void addAll(List<UserDeadlineRelation> list) {
//        mCalendarList.addAll(list);
//        notifyDataSetChanged();
//    }

}
