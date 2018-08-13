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
import me.juliasson.unipath.MyFirebaseMessagingService;
import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.DeadlineDetailsDialog;
import me.juliasson.unipath.internal.GetDeadlineCheckedInterface;
import me.juliasson.unipath.internal.GetItemDetailOpenedInterface;
import me.juliasson.unipath.internal.UpdateLinearTimelineInterface;
import me.juliasson.unipath.internal.UpdateProfileProgressBarInterface;
import me.juliasson.unipath.internal.UpdateTimelineAdapterInterface;
import me.juliasson.unipath.model.OrderStatus;
import me.juliasson.unipath.model.TimeLine;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.VectorDrawableUtils;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> implements
        UpdateTimelineAdapterInterface,
        UpdateProfileProgressBarInterface {

    private List<TimeLine> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private HashMap<TimeLine, ArrayList<UserDeadlineRelation>> numEvents;
    private Date currentDate;
    private UpdateLinearTimelineInterface ultInterface;
    private UpdateTimelineAdapterInterface utaInterface;
    private GetItemDetailOpenedInterface gidInterface;
    private GetDeadlineCheckedInterface dcInterface;

    public TimeLineAdapter(List<TimeLine> list,
                           HashMap<TimeLine, ArrayList<UserDeadlineRelation>> events,
                           UpdateLinearTimelineInterface ultInterface,
                           GetItemDetailOpenedInterface gidInterface,
                           GetDeadlineCheckedInterface dcInterface) {
        this.mFeedList = list;
        this.numEvents = events;
        this.ultInterface = ultInterface;
        this.gidInterface = gidInterface;
        this.dcInterface = dcInterface;
    }

    @NonNull
    @Override
    public TimeLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate(R.layout.item_timeline, viewGroup, false);
        utaInterface = this;
        DeadlineDetailsDialog.setPpbInterface(this);
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
            viewHolder.mDate.setText(timeLineModel.getDate());
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
            } else if (freq == 3) {
                viewHolder.mEventDots.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_event_dot_3));
            } else if (freq > 3) {
                viewHolder.mEventDots.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_events_3more));
            }
        }

        //setting status of timeline event
        for (UserDeadlineRelation relation : relationList) {
            if (currentDate.after(timeLineModel.getDDate())) {
                if (!relation.getCompleted()) {
                    timeLineModel.setStatus(OrderStatus.MISSED);
                    break;
                } else {
                    timeLineModel.setStatus(OrderStatus.COMPLETED);
                }
            } else {
                if (!relation.getCompleted()) {
                    timeLineModel.setStatus(OrderStatus.ACTIVE);
                    break;
                } else {
                    timeLineModel.setStatus(OrderStatus.COMPLETED_EARLY);
                }
            }
        }

        if(timeLineModel.getStatus() == OrderStatus.COMPLETED) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.
                    getDrawable(mContext, R.drawable.ic_check_active));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.
                    getDrawable(mContext, R.drawable.ic_check_inactive));
        } else if (timeLineModel.getStatus() == OrderStatus.MISSED) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.
                    getDrawable(mContext, R.drawable.ic_check_missed));
        } else if (timeLineModel.getStatus() == OrderStatus.COMPLETED_EARLY) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.
                    getDrawable(mContext, R.drawable.ic_check_active));
        } else {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.
                    getDrawable(mContext, R.drawable.ic_check_inactive));
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
                gidInterface.getItemDetailOpened(true);
                TimeLine timeline = mFeedList.get(position);

                Intent intent = new Intent (mContext, DeadlineDetailsDialog.class);
                DeadlineDetailsDialog.setUtaInterface(utaInterface);
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

    //-------------------Implementing interface-------------------

    @Override
    public void updateItemRemoval(boolean status) {
        if (status) {
            ultInterface.updateItemRemoval(true);
            MyFirebaseMessagingService.clear();
        }
    }

    @Override
    public void updateItemStatus(boolean status) {
        gidInterface.getItemDetailOpened(false);
        if (status) {
            ultInterface.updateItemComplete(true);
        }
    }

    @Override
    public void updateProgressBar(boolean update) {
        if (update) {
            dcInterface.getDeadlineChecked(true);
        }
    }
}
