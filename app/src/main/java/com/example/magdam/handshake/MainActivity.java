package com.example.magdam.handshake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    public static final String TAG = MainActivity.class.getName();
    public Intent i;
    int start;
int a=1;
    int b=9;
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: " + this.start);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: "+this.start);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: "+this.start);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button stop = (Button) findViewById(R.id.stopButton);
        Button start = (Button) findViewById(R.id.startButton);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        if(savedInstanceState!=null){
            this.start=savedInstanceState.getInt("start");
            Log.i(TAG, "OnCreate: called save start"+this.start);
        }
        else{
            this.start=-1;

            Log.i(TAG, "OnCreate: called new start"+this.start);
        }

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("start", start);
        Log.i(TAG, "onSaveInstanceState"+this.start);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        start = savedInstanceState.getInt("start");
        Log.i(TAG, "onRestoreInstanceState"+this.start);

    }
    @Override
    public void onResume() {
        super.onResume();
        i=new Intent(this, Sensors.class);
        if(start==0) {
            i.putExtra("path", "costam");
            startService(i);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private String getRadiobuttons(int group, int opcja, int opcja2, String a, String b){
        String s=" ";
        RadioGroup as = (RadioGroup) findViewById(group);
        int id=as.getCheckedRadioButtonId();
        if(id==opcja){
            s=s+a;
        }
        else if(id==opcja2){
            s=s+b;
        }

        return s;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startButton:
                Log.i(TAG, "Start:"+start);
                if(start==-1){
                    start=0;

                    startService(i);
                    TextView stan=(TextView) findViewById(R.id.stan);
                    stan.setText("Stan: zapis");
                }
                // start++;
                break;
            case R.id.stopButton:
                Vibrations w=new Vibrations();
                w.vibrate(a,b,this.getBaseContext());
                if(b>0){
                    a=a+1;
                    b=b-1;
                }
                if(start==0){
                    start=-1;
                    stopService(i);
                    TextView stan=(TextView) findViewById(R.id.stan);
                    stan.setText("Stan:");

                }
        }
    }
}
