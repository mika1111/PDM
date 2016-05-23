package com.example.magdam.handshake;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Sensors extends Service implements SensorEventListener {
    public static final String TAG = Sensors.class.getName();
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
    private String TRANSFORM_PREF="transform";
    private SensorManager mSensorManager = null;
    private WakeLock mWakeLock = null;
    HashMap<Integer, CSVWriter> hashMap;
    float[] gravity= new float[3];
    final float alpha = 0.8f;
    long sendValue=0;
    float[] linear_acceleration=new float[3];
    private int algorytm=0;


    private void startFile(int typeAccelerometer, String currentDateandTime, String filename) {
        if (mSensorManager.getDefaultSensor(typeAccelerometer) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(typeAccelerometer), SensorManager.SENSOR_DELAY_UI);
            String file=filename+currentDateandTime+".txt";
            hashMap.put(typeAccelerometer, new CSVWriter(file));
        }

    }
    /**
     *
     * @param chars
     * @return the max value in the array of chars
     */
    private static float maxValue(float[] chars) {
        float max = chars[0];
        for (int ktr = 0; ktr < chars.length; ktr++) {
            if (chars[ktr] > max) {
                max = chars[ktr];
            }
        }
        return max;
    }
    /**
     *
     * @param chars
     * @return the max value in the array of chars
     */
    private static float minValue(float[] chars) {
        float max = chars[0];
        for (int ktr = 0; ktr < chars.length; ktr++) {
            if (chars[ktr] > max) {
                max = chars[ktr];
            }
        }
        return max;
    }
    private void sendVal(long sendVal){
        try {
            Log.i(TAG, "VALUE: " + sendVal);
            JSONObject object = new JSONObject();
            object.put("wiadomosc", Long.toString(sendVal));
            UDPSender sender= new UDPSender(false, object, this);
            sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sending() {
        long value=0;
        switch(algorytm){
            case 1: value=srednia(linear_acceleration);
                break;
            case 2:
                value=vectorLength(linear_acceleration);
                break;
            default:
                value=maxModule(linear_acceleration);
        }
        if(value!=sendValue){
            sendValue=value;
            sendVal(sendValue);
        }
    }
    private void getSensorValue(int type, float[] values, long timestamp) {
        if(type==Sensor.TYPE_LINEAR_ACCELERATION) {
            sending();
        }
        else if(type==Sensor.TYPE_ACCELEROMETER){
            gravity[0] = alpha * gravity[0] + (1 - alpha) * values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * values[2];

            linear_acceleration[0] = values[0] - gravity[0];
            linear_acceleration[1] = values[1] - gravity[1];
            linear_acceleration[2] = values[2] - gravity[2];
            sending();
        }
    }

    private long vectorLength(float[] linear_acceleration){
        return (long) Math.sqrt(linear_acceleration[0]*linear_acceleration[0]+linear_acceleration[1]*linear_acceleration[1]+linear_acceleration[2]*linear_acceleration[2]);
    }

    private long srednia(float[] linear_acceleration){
        float av=0;
        for(int i=0; i<linear_acceleration.length; i++){
            av=av+Math.abs(linear_acceleration[0]);
        }
        return (long) av/linear_acceleration.length;
    }


    private long maxModule(float[] linear_acceleration){
        if(Math.abs(minValue(linear_acceleration))>Math.abs(maxValue(linear_acceleration))) {
            return Math.round(Math.abs(minValue(linear_acceleration)));
        }
        else{
            return Math.round(Math.abs(maxValue(linear_acceleration)));
        }
    }
    /*
     * Register this as a sensor event listener.
     */
    private void registerListener(int typeAccelerometer) {
        List<Sensor> mList= mSensorManager.getSensorList(Sensor.TYPE_ALL);
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
            Log.i(TAG, "onReceive(" + intent + ")");
            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener(Sensor.TYPE_ACCELEROMETER);
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
      //  Log.i(TAG, "onSensorChanged().");
        List<Sensor> mList= mSensorManager.getSensorList(Sensor.TYPE_ALL);
        //Log.i(TAG, mList.toString());
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
        startFile(Sensor.TYPE_LINEAR_ACCELERATION, currentDateandTime, "liniowyAkc");
        startFile(Sensor.TYPE_GRAVITY, currentDateandTime, "gravity");
        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        gravity[0] = 0;
        gravity[1] = 0;
        gravity[2] = 0;
        List<Sensor> mList= mSensorManager.getSensorList(Sensor.TYPE_ALL);
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterListener();
        mWakeLock.release();
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        algorytm=Integer.parseInt(intent.getStringExtra("path"));
        Log.i(TAG, "algorytm2"+intent.getStringExtra("path"));
        startForeground(Process.myPid(), new Notification());
        registerListener(Sensor.TYPE_ACCELEROMETER);
        registerListener(Sensor.TYPE_LINEAR_ACCELERATION);
        registerListener(Sensor.TYPE_GRAVITY);


        mWakeLock.acquire();

        return START_STICKY;
    }
}