package me.juliasson.unipath.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.DeadlineDetailsActivity;
import me.juliasson.unipath.model.OrderStatus;
import me.juliasson.unipath.model.TimeLine;
import me.juliasson.unipath.utils.DateTimeUtils;
import me.juliasson.unipath.utils.VectorDrawableUtils;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private List<TimeLine> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(List<TimeLine> feedList) {
        mFeedList = feedList;
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

        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            viewHolder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            viewHolder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

        if(!timeLineModel.getDate().isEmpty()) {
            viewHolder.mDate.setVisibility(View.VISIBLE);
            viewHolder.mDate.setText(DateTimeUtils.parseDateTime(timeLineModel.getDate(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
        }
        else
            viewHolder.mDate.setVisibility(View.GONE);

        viewHolder.mMessage.setText(timeLineModel.getMessage());
    }

    @Override
    public int getItemCount() {
        return mFeedList.size()
                ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_timeline_date)
        TextView mDate;
        @BindView(R.id.text_timeline_title)
        TextView mMessage;
        @BindView(R.id.time_marker)
        TimelineView mTimelineView;

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
