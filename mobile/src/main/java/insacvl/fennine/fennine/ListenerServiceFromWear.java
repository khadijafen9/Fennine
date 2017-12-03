package insacvl.fennine.fennine;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Administrateur on 01/12/2017.
 */

public class ListenerServiceFromWear extends WearableListenerService {

    private static final String START_SERVICE = "start-service";
    private static final String STOP_SERVICE = "stop-service";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        /*
         * Receive the message from wear
         */
        if (messageEvent.getPath().equals(START_SERVICE)) {
            startService(new Intent(this, MyLocationService.class));
        }else if(messageEvent.getPath().equals(STOP_SERVICE)){
            stopService(new Intent(this, MyLocationService.class));
        }

    }
}
