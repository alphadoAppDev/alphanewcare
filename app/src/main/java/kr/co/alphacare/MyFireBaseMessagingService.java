package kr.co.alphacare;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token){
        Log.d("FCM Log", "Refreshed token: " + token);
// 나중에 c#서버에다가 토큰 날려야함
    }
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.d("FCM Log", "From: " + remoteMessage.getFrom());
            if (remoteMessage.getData().size() > 0) {

                Log.d("FCM Log", "Message data payload: " + remoteMessage.getData());
            }
            String messageBody = remoteMessage.getNotification().getBody();
            String messageTitle = remoteMessage.getNotification().getTitle();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            String channelId = "Channel ID";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new  NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(messageTitle)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
            NotificationManager notificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelName = "Channel Name";
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManger.createNotificationChannel(channel);
            }
            notificationManger.notify(0, notificationBuilder.build());

        }
    }
}
