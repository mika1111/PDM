package com.example.magdam.handshake;
import android.os.Vibrator;
import android.content.Context;
/**
 * Created by Magdalena on 2015-03-03.
 */
public class Vibrations {

    public void vibrate(int t, Context context){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }

}

