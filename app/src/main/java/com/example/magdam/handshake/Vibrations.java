package com.example.magdam.handshake;
import android.os.Vibrator;
import android.content.Context;
import android.util.Log;

/**
 * Created by Magdalena on 2015-03-03.
 */
public class Vibrations {
String TAG="Vibrations";
    public void vibrate(long a, long b, Context context){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long pattern[]={0,a,b, a,b,a,b,a,b, a,b,a,b, a,b, a,b,a,b,a,b, a,b,a,b,a,b, a,b,a,b,a,b, a,b,a,b, a,b, a,b,a,b,a,b, a,b,a,b};
        Log.i(TAG, "v1:"+a+" "+b);
            v.vibrate(pattern, -1);
      }

}

