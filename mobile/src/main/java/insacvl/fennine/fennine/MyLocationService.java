package insacvl.fennine.fennine;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrateur on 22/11/2017.
 */

public class MyLocationService extends Service {

    private static final String TAG = "MyLocationService";
    private LocationManager myLocationManager;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1f;
    FileOutputStream file;
    int frequency;
    LocationListener locationListener;



    private class LocationListener  implements android.location.LocationListener  {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            try {
                file.write(("Altitude:"+ mLastLocation.getAltitude()+":Longitude:"+ mLastLocation.getLongitude()+"\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }


    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");
        initializeLocationManager();

        locationListener = new LocationListener(LocationManager.NETWORK_PROVIDER);

        try {
            file = openFileOutput("superFile.txt", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        frequency = intent.getExtras().getInt("frequency");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.err.println("checkSelfPermission");
        }

        if (myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, frequency , LOCATION_DISTANCE, locationListener);
            Toast.makeText(MyLocationService.this, "Altitude: "+ locationListener.mLastLocation.getAltitude()+" Longitude: "+ locationListener.mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();

            registerReceiver(new ProximityReceiver(), new IntentFilter("insacvl.fennine.fennine"));
            Intent proximityIntent = new Intent("insacvl.fennine.fennine");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),-1,proximityIntent,0);
            myLocationManager.addProximityAlert(47.082158,2.415858,10,-1,pendingIntent);
            Log.i("KP","Proximity alert");

        }else{
            Toast.makeText(this, "Turn on GPS and Network", Toast.LENGTH_LONG).show();
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        myLocationManager.removeUpdates(locationListener);
        Toast.makeText(this, "MyLocationService Stopped.", Toast.LENGTH_SHORT).show();

    }



    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (myLocationManager == null) {
            myLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





}
