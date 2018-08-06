package me.juliasson.unipath;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.ParseUser;

import me.juliasson.unipath.activities.TimelineActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int BROADCAST_NOTIFICATION_ID = 1;

    private ParseUser user;

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        if (remoteMessage.getNotification().getBody() != null) {
//            Log.e("FIREBASE", "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage);
//        }

//        super.onMessageReceived(remoteMessage);
//        Notification notification = new NotificationCompat.Builder(this)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setSmallIcon(R.drawable.ic_insert_emoticon_black_24dp)
//                .build();
//        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
//        manager.notify(123, notification);
        user = ParseUser.getCurrentUser();

        String notificationBody = "";
        String notificationTitle = "";
        String notificationData = "";
        try{
            notificationData = remoteMessage.getData().toString();
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }catch (NullPointerException e){
            Log.e(TAG, "onMessageReceived: NullPointerException: " + e.getMessage() );
        }
        Log.d(TAG, "onMessageReceived: data: " + notificationData);
        Log.d(TAG, "onMessageReceived: notification body: " + notificationBody);
        Log.d(TAG, "onMessageReceived: notification title: " + notificationTitle);


//        String dataType = remoteMessage.getData().get(getString(R.string.data_type));
//        if(dataType.equals(getString(R.string.reminder))){
//            Log.d(TAG, "onMessageReceived: new incoming message.");
//            String title = remoteMessage.getData().get(getString(R.string.data_title));
//            String message = remoteMessage.getData().get(getString(R.string.data_message));
//            String messageId = remoteMessage.getData().get(getString(R.string.data_message_id));
//            sendMessageNotification(title, message);
//        }
        String title = "String";
        String message = notificationBody;
        sendMessageNotification(title, message);
    }

    /**
     * Build a push notification
     * @param title
     * @param message
     */
    private void sendMessageNotification(String title, String message){
        Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

        //get the notification id
        //int notificationId = buildNotificationId(messageId);

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getString(R.string.default_notification_channel_id));
        // Creates an Intent for the Activity
        Intent pendingIntent = new Intent(this, TimelineActivity.class);
        // Sets the Activity to start in a new, empty task
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        pendingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.ic_insert_emoticon_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_insert_emoticon_black_24dp))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setColor(getColor(R.color.background_dark_orange))
                .setAutoCancel(true)
                //.setSubText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(user.getUsername()))
                .setOnlyAlertOnce(true);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //mNotificationManager.notify(notificationId, builder.build());

//        Intent intent = new Intent(this, TimelineActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_insert_emoticon_black_24dp)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, TimelineActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_insert_emoticon_black_24dp))
                .setSmallIcon(R.drawable.ic_insert_emoticon_black_24dp)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }



    private int buildNotificationId(String id){
        Log.d(TAG, "buildNotificationId: building a notification id.");

        int notificationId = 0;
        for(int i = 0; i < 9; i++){
            notificationId = notificationId + id.charAt(0);
        }
        Log.d(TAG, "buildNotificationId: id: " + id);
        Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
        return notificationId;
    }

}
