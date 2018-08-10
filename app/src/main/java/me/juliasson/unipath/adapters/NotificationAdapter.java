package me.juliasson.unipath.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.Notify;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Notify> mNotifications;

    public NotificationAdapter (ArrayList<Notify> notifications) {
        mNotifications = notifications;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View notificationView = inflater.inflate(R.layout.item_notify, viewGroup, false);
        NotificationAdapter.ViewHolder viewHolder = new NotificationAdapter.ViewHolder(notificationView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder viewHolder, int position) {
        final Notify notify = mNotifications.get(position);
        viewHolder.tvBody.setText(notify.getBody());
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mNotifications.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Notify> list) {
        mNotifications.addAll(list);
        notifyDataSetChanged();
    }
}
