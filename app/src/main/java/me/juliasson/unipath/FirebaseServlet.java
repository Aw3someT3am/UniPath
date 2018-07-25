package me.juliasson.unipath;

import com.google.firebase.messaging.FirebaseMessagingService;

public class  FirebaseServlet extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
