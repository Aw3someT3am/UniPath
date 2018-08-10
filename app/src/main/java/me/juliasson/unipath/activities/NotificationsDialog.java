package me.juliasson.unipath.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import java.util.ArrayList;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.NotificationAdapter;
import me.juliasson.unipath.model.Notify;
import me.juliasson.unipath.utils.Constants;

public class NotificationsDialog extends AppCompatActivity {

    private NotificationAdapter mNotificationAdapter;
    private ArrayList<Notify> mNotifications;
    private RecyclerView rvNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notifications);
        this.setFinishOnTouchOutside(true);

        setSize();

        rvNotifications = findViewById(R.id.rvNotifications);
        mNotifications = new ArrayList<>();
        mNotificationAdapter = new NotificationAdapter(mNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(mNotificationAdapter);

        loadNotifications();
    }

    private void loadNotifications() {
        ArrayList<Notify> list = getIntent().getParcelableArrayListExtra(Constants.KEY_NOTIFICATIONS);
        mNotificationAdapter.addAll(list);
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, (3 * height)/4);
    }
}
