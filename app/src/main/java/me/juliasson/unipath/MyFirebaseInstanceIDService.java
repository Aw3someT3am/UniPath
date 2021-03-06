package me.juliasson.unipath;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        try {
            String refreshedToken = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("Firbase id login", "Refreshed token: " + refreshedToken);
            sendRegistrationToServer(refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
//    mUser.getIdToken(true)
//        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//        public void onComplete(@NonNull Task<GetTokenResult> task) {
//            if (task.isSuccessful()) {
//                String idToken = task.getResult().getToken();
//                // Send token to your backend via HTTPS
//                // ...
//            } else {
//                // Handle error -> task.getException();
//            }
//        }
//    });


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_messaging_token))
                .setValue(token);
    }
}
