package com.example.magdam.handshake;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by Magdalena Marusczyk on 2015-04-25.
 */
public class Vibrations {
    String TAG = "Vibrations";
    int DURATION = 500;
    long pattern[];
    long t;
    long active;
    Context context;
    boolean pulsacja;
    int value=0;

    public Vibrations(long T, long active, Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        int vi=SP.getInt("vibration",0);
        Log.w("Vibration picker", ""+vi);
        this.context = context;
        this.t=T;
        this.active=active+(long) vi;
        Log.d(TAG, "index=" + T);
        int paternLength = (int) Math.floor(DURATION / T) * 2;
        Log.d(TAG, "paternLength=" + paternLength);
        if (DURATION - paternLength >= active) {
            paternLength += 2;
        } else {
            paternLength++;
        }

        boolean temp = true;
        pattern = new long[paternLength];
        for (int i = 0; i < paternLength; i++) {
            if (temp) {
                pattern[i] = T - active;
                temp=false;
            } else {
                pattern[i] = active;
                temp=true;
            }
        }

    }
    public long getT(){
        return t;
    }
    public long getActive(){
        return active;
    }
    public void vibrate() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern, -1);
    }
    public void setPulsacja(boolean p){
        pulsacja=p;
    }
    public  void setValue(int v){
        value=v;
    }
    public int getValue(){
        return value;
    }
    public boolean getPulsacja(){
        return pulsacja;
    }
    @Override
    public String toString() {
        String result="T:"+t+" active:"+active+"pattern:";
        for(long p : pattern){
            result+=p+",";
        }
        return result;
    }

}
