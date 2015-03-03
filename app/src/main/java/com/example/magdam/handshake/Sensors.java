package com.example.magdam.handshake;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class Sensors extends Service implements SensorEventListener {
    public static final String TAG = Sensors.class.getName();
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private SensorManager mSensorManager = null;
    private WakeLock mWakeLock = null;
    HashMap<Integer, CSVWriter> hashMap;

    private void startFile(int typeAccelerometer, String currentDateandTime, String filename) {
        if (mSensorManager.getDefaultSensor(typeAccelerometer) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(typeAccelerometer), SensorManager.SENSOR_DELAY_UI);
            String file=filename+currentDateandTime+".txt";
            hashMap.put(typeAccelerometer, new CSVWriter(file));
        }

    }
    private void getSensorValue(int type, float[] values, long timestamp) {
        if(hashMap.get(type)!=null) {
            String row = Long.toString(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            row = row + ", " + currentDateandTime;
            for (int i = 0; i < values.length; i++) {
                row = row + ", " + Float.toString(values[i]);
            }
            row = row + "\n";
            Log.i(TAG, row + " typ:" + type + (hashMap.get(type) == null));
            hashMap.get(type).write(row);
        }




    }
    /*
     * Register this as a sensor event listener.
     */
    private void registerListener(int typeAccelerometer) {
        if (mSensorManager.getDefaultSensor(typeAccelerometer) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(typeAccelerometer), SensorManager.SENSOR_DELAY_NORMAL);
        }
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * Un-register this as a sensor event listener.
     */
    private void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener(Sensor.TYPE_ACCELEROMETER);
                    registerListener(Sensor.TYPE_PRESSURE);
                    registerListener(Sensor.TYPE_LINEAR_ACCELERATION);
                    registerListener(Sensor.TYPE_GRAVITY);

                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged().");
    }

    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "onSensorChanged().");
        Sensor s=event.sensor;
        getSensorValue(s.getType(), event.values, event.timestamp);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Staaaaaaaaaaaaaaaaaart");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        hashMap=new HashMap<Integer, CSVWriter>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        startFile(Sensor.TYPE_ACCELEROMETER, currentDateandTime, "akcelerometr");
        startFile(Sensor.TYPE_GYROSCOPE, currentDateandTime, "cisnienie");
        startFile(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, currentDateandTime, "liniowyAkc");
        startFile(Sensor.TYPE_GRAVITY, currentDateandTime, "gravity");
        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterListener();
        mWakeLock.release();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startForeground(Process.myPid(), new Notification());
        registerListener(Sensor.TYPE_ACCELEROMETER);
        registerListener(Sensor.TYPE_GYROSCOPE);
        registerListener(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        registerListener(Sensor.TYPE_GRAVITY);


        mWakeLock.acquire();

        return START_STICKY;
    }
}