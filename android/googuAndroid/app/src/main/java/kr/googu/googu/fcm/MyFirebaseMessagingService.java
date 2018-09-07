package kr.googu.googu.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.android.volley.VolleyLog.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i("fcmResult", "getFrom: " + remoteMessage.getFrom());
        Log.i("fcmResult", "getTo: " + remoteMessage.getTo());
        Log.i("fcmResult", "getMessageId: " + remoteMessage.getMessageId());
        Log.i("fcmResult", "getMessageType: " + remoteMessage.getMessageType());
        Log.i("fcmResult", "getCollapseKey: " + remoteMessage.getCollapseKey());
        Log.i("fcmResult", "getNotifiy: " + remoteMessage.getNotification());
        Log.i("fcmResult", "getData: " + remoteMessage.getData());
        Log.i(TAG, "fcmResult: " + remoteMessage.getFrom());
        Log.i(TAG,"bodyYey"+remoteMessage.getData().get("test"));
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
