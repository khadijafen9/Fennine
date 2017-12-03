package insacvl.fennine.fennine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrateur on 30/11/2017.
 */

public class ProximityReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {

        final String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Intent intent1 =  new Intent();
        intent1.setClassName("insacvl.fennine.fennine","insacvl.fennine.fennine.AlertActivity");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent2 =  new Intent();
        intent2.setClassName("insacvl.fennine.fennine","insacvl.fennine.fennine.MainActivity");
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final Boolean entering = intent.getBooleanExtra(key, false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification = new Notification();
        Notification.Builder builder = new Notification.Builder(context).setContentIntent(pendingIntent).setSmallIcon(R.drawable.bart).setContentTitle("Proximity Alert");
        notification = builder.build();


        if (entering) {
            notificationManager.notify(NOTIFICATION_ID, notification);
            //Toast.makeText(context, "entering", Toast.LENGTH_SHORT).show();
            context.startActivity(intent1);
            //context.startActivity(new Intent(context, DisplayActivity.class));


        } else {
            Toast.makeText(context, "exiting", Toast.LENGTH_SHORT).show();
            context.startActivity(intent2);
        }





    }



}
