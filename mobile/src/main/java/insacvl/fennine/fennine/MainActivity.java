package insacvl.fennine.fennine;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, SensorEventListener {

    private SensorManager sensorManager;
    private long lastUpdate;


    private static final int READ_BLOCK_SIZE =100 ;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
    }


    /**
     * Cursor graphique pour changer la fréquence d'enregistrement des coordonnées de localisation
     * @param seekBar
     * @param i
     * @param b
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Toast.makeText(getApplicationContext(),"frequency : "+i*100, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();

    }

    /**
     * Arrêt et démarrage du service de localisation
     * @param view
     */

    public void startService(View view) {

        Intent intent = new Intent(this, MyLocationService.class);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        intent.putExtra("frequency", seekBar.getProgress()*100);

        startService(intent);

    }

    public void stopService(View view) {

        stopService(new Intent(this, MyLocationService.class));
    }


    /**
     * Affichage des logs de localisation enregistrés dans le ficher à travers l'acitivité DisplayActivity
     * @param view
     * @throws IOException
     */
    public void displayData(View view) throws IOException {
        String data = "";
        try {
            FileInputStream file = openFileInput("superFile.txt");
            InputStreamReader inputReader = new InputStreamReader(file);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            int charRead;

            while ((charRead = inputReader.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                //Toast.makeText(this, readstring, Toast.LENGTH_SHORT).show();
                data += readstring;
            }
            inputReader.close();
            Log.i("Data ", data);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
        intent.putExtra("data", data);
        //System.out.println("data: "+ data.toString());
        startActivity(intent);


    }


    /**
     * Supression du fichier des logs
     * @param view
     */

    public void deleteFile(View view) {

        File dir = getFilesDir();
        File file = new File(dir, "superFile.txt");
        file.delete();
        Toast.makeText(this," File deleted", Toast.LENGTH_SHORT).show();

    }


    /**
     * Lancement du service de localisation en secouant le smartphone
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(this, "Device was shuffled", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MyLocationService.class);
            seekBar = (SeekBar)findViewById(R.id.seekBar);
            seekBar.setOnSeekBarChangeListener(this);

            intent.putExtra("frequency", seekBar.getProgress()*100);

            startService(intent);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {

        super.onPause();
        sensorManager.unregisterListener(this);
    }


}
