package kr.co.alphanewcare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private final static int NOTICATION_ID = 222;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.alphado_icon);
        builder.setSmallIcon(R.drawable.alphado_icon);
        builder.setContentTitle("알림 제목");
        builder.setContentText("알람 세부 텍스트");
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setLargeIcon(largeIcon);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }

//    @Override
//    public void onCreate() {
//        Intent intent = new Intent(this, MainActivity.class);
//        long[] pattern = {0, 300, 0};
//        PendingIntent pi = PendingIntent.getActivity(this, 01234, intent, 0);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.alphado_icon)
//                .setContentTitle("Take Questionnaire")
//                .setContentText("Take questionnaire for Duke Mood Study.")
//                .setVibrate(pattern)
//                .setAutoCancel(true);
//
//        mBuilder.setContentIntent(pi);
//        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
//        mBuilder.setAutoCancel(true);
//        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(01234, mBuilder.build());
//    }


}
