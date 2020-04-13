package moe.noitamina.ohli;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotiManager {

    private static final String channelId = "channel_id_ohli";
    private static final String channelGroup = "channel_group_ohli";
    private static final String channelName = "자막";
    private static final String channelGroupName = "OHLI";

    private NotificationManager nm = null;
    private NotificationChannel nc = null;
    private NotificationChannelGroup ncg = null;

    private static NotiManager instance;
    public static NotiManager getInstance(Context context) {
        if (instance == null) {
            instance = new NotiManager(context);
        }
        if (instance.context == null) {
            instance.context = context;
        }
        return instance;
    }

    private Context context;
    private NotiManager(Context context) {
        this.context = context;
    }

    private void noti(int notiId, String title, String text, String url) {
        if (nm == null) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

//        Intent intent = new Intent(Intent.ACTION_VIEW);
        Intent intent = new Intent().setClass(context, NotiActivity.class);
        intent.setData(Uri.parse(url));
        PendingIntent pi = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder ncb = new NotificationCompat.Builder(context, channelId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ncg == null) {
                ncg = new NotificationChannelGroup(channelGroup, channelGroupName);
                nm.createNotificationChannelGroup(ncg);

                nc = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                nc.setGroup(channelGroup);
                nc.enableLights(true);
                nc.setLightColor(Color.GREEN);
//                nc.enableVibration(true);
//                nc.setVibrationPattern(new long[]{100, 200, 100, 200});
//                nc.enableVibration(false);
                nc.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                nm.createNotificationChannel(nc);
            }
//            ncb.setGroup(channelId).setGroupSummary(false);

        } else {

        }

        ncb.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
//                .setLights(Color.GREEN, 1, 1);
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        ;
        if (true) {
            ncb.setVibrate(new long[] {});
        } else {
            ncb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[] { 1000, 1000 });
        }
        ncb.setContentIntent(pi);

        nm.notify(notiId, ncb.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        }
    }

    public void notiSubtitle(int notiId, String nick, String aniTitle, String ep, String url) {
        double episode = Double.parseDouble(ep);
        if (episode >= 9999) {
            ep = "완결";
        } else if (episode * 10 == ((int) episode) * 10) {
            ep = "" + (int) episode + "화";
        } else {
            ep += "화";
        }
        if (!url.startsWith("http")) url = "http://" + url;

        String msg = "[" + nick + "] " + aniTitle + " " + ep;

        noti(notiId, "신규 자막 알림", msg, url);
    }

}
