package me.juliasson.unipath.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.DeadlineDetailsActivity;
import me.juliasson.unipath.model.OrderStatus;
import me.juliasson.unipath.model.TimeLine;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.DateTimeUtils;
import me.juliasson.unipath.utils.VectorDrawableUtils;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private List<TimeLine> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private HashMap<TimeLine, ArrayList<UserDeadlineRelation>> numEvents;
    private Date currentDate;

    public TimeLineAdapter(List<TimeLine> list, HashMap<TimeLine, ArrayList<UserDeadlineRelation>> events) {
        mFeedList = list;
        numEvents = events;
    }

    @NonNull
    @Override
    public TimeLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate(R.layout.item_timeline, viewGroup, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineAdapter.ViewHolder viewHolder, int position) {
        TimeLine timeLineModel = mFeedList.get(position);
        ArrayList<UserDeadlineRelation> relationList = new ArrayList<>();
        currentDate = Calendar.getInstance().getTime();

        //setting the date of the timeline
        if(!timeLineModel.getDate().isEmpty()) {
            viewHolder.mDate.setVisibility(View.VISIBLE);
            viewHolder.mDate.setText(DateTimeUtils.parseDateTime(timeLineModel.getDate(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
        } else
            viewHolder.mDate.setVisibility(View.GONE);

        //setting timeline event dots
        if (numEvents.containsKey(timeLineModel)) {
            relationList = numEvents.get(timeLineModel);
            int freq = relationList.size();
            if (freq == 1) {
                viewHolder.mEventDots.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_event_dot));
            } else if (freq == 2) {
                viewHolder.mEventDots.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_event_dot_2));
            } else if (freq == 3 || freq > 3) {
                viewHolder.mEventDots.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_event_dot_3));
            }
        }

        //setting status of timeline event
        for (UserDeadlineRelation relation : relationList) {
            if (currentDate.after(timeLineModel.getDDate()) && !relation.getCompleted()) {
                timeLineModel.setStatus(OrderStatus.MISSED);
                break;
            } else if (currentDate.after(timeLineModel.getDDate()) && relation.getCompleted()) {
                timeLineModel.setStatus(OrderStatus.COMPLETED);
            } else if (relation.getCompleted()) {
                timeLineModel.setStatus(OrderStatus.COMPLETED_EARLY);
            } else {
                timeLineModel.setStatus(OrderStatus.ACTIVE);
            }
        }

        if(timeLineModel.getStatus() == OrderStatus.COMPLETED) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.holo_green_light));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, android.R.color.holo_blue_light));
        } else if (timeLineModel.getStatus() == OrderStatus.MISSED) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_missed, android.R.color.holo_orange_light));
        } else if (timeLineModel.getStatus() == OrderStatus.COMPLETED_EARLY) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_completed_early, android.R.color.holo_green_light));
        } else {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_timeline_date)
        TextView mDate;
        @BindView(R.id.time_marker)
        TimelineView mTimelineView;
        @BindView(R.id.ivEventDots)
        ImageView mEventDots;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            mTimelineView.initLine(viewType);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                TimeLine timeline = mFeedList.get(position);

                Intent intent = new Intent (mContext, DeadlineDetailsActivity.class);
                intent.putExtra(TimeLine.class.getSimpleName(), Parcels.wrap(timeline));
                intent.putExtra(HashMap.class.getSimpleName(), Parcels.wrap(numEvents));
                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    // Clean all elements of the recycler
    public void clear() {
        mFeedList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<TimeLine> list) {
        mFeedList.addAll(list);
        notifyDataSetChanged();
    }

}
