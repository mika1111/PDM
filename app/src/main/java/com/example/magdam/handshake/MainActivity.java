package com.example.magdam.handshake;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    public static final String TAG = MainActivity.class.getName();
    public Intent i;
    int start;
    UDPSender server= null;
    int b = 9;
    int ODBIORCA_RESULS=1;
    int NADAWCA_RESULS=2;
    String NAD_PREF="Nadawca";
    String ODB_PREF="Odbiorca";
    Button nad;
    View background;
    Button odb;
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: " + this.start);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: " + this.start);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: " + this.start);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = (Button) findViewById(R.id.startButton);
        background=(View) findViewById(R.id.layout);
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        nad = (Button) findViewById(R.id.nadawca);
        odb = (Button) findViewById(R.id.odbiorca);
        start.setOnClickListener(this);
        nad.setOnClickListener(this);
        odb.setOnClickListener(this);
        SharedPreferences settings = getSharedPreferences(this.ODB_PREF, 0);
        String odbiorca = settings.getString(this.ODB_PREF, "");

        if(odbiorca!=""){
            odb.setText(odbiorca);
        }
        SharedPreferences nadawca = getSharedPreferences(this.NAD_PREF, 0);
        String nada = nadawca.getString(this.NAD_PREF, "");
        if(nada!=""){
            nad.setText(nada);
        }
        if (savedInstanceState != null) {
            this.start = savedInstanceState.getInt("start");

            Log.i(TAG, "OnCreate: called save start" + this.start);
        } else {
            this.start = -1;

            Log.i(TAG, "OnCreate: called new start" + this.start);
        }
this.register();

    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void register(){
    JSONObject object = new JSONObject();
    try {
        object.put("wiadomosc", "rejestracja");
    } catch (JSONException e) {
        e.printStackTrace();
    }
        if(server!=null){
            server.setListen(false);
        }
        server=new UDPSender(true, object, this);
        server.setBacgoud(this);
        server.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object.toString());
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            String user = data.getStringExtra("User");
        if (requestCode == this.NADAWCA_RESULS) {
                nad.setText(user);
            SharedPreferences settings = getSharedPreferences(this.NAD_PREF, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(this.NAD_PREF, user);
            editor.apply();
            this.register();

        } else if(requestCode==this.ODBIORCA_RESULS){
            odb.setText(user);
            SharedPreferences settings = getSharedPreferences(this.ODB_PREF, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(this.ODB_PREF, user);
            editor.apply();
         }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("start", start);
        Log.i(TAG, "onSaveInstanceState" + this.start);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        start = savedInstanceState.getInt("start");
        Log.i(TAG, "onRestoreInstanceState" + this.start);

    }

    @Override
    public void onResume() {
        super.onResume();
        i = new Intent(this, Sensors.class);
        if (start == 0) {
            SharedPreferences algorytm = PreferenceManager.getDefaultSharedPreferences(this);
            int transform= Integer.parseInt(algorytm.getString("transform","0"));
            Log.i(TAG, "algorytm:  "+algorytm.getString("transform","0"));
            i.putExtra("path", transform);
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
            Intent i = new Intent(this, SettingsActivity.class);

            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private String getRadiobuttons(int group, int opcja, int opcja2, String a, String b) {
        String s = " ";
        RadioGroup as = (RadioGroup) findViewById(group);
        int id = as.getCheckedRadioButtonId();
        if (id == opcja) {
            s = s + a;
        } else if (id == opcja2) {
            s = s + b;
        }

        return s;
    }
    int from= Color.BLUE;

    public void setColor(int c) {
        int to;
        if(c==0){
            to= Color.BLUE;
        } else if(c==1){
            to=Color.YELLOW;
        } else {
            to= Color.RED;
        }
               View background=(View)  findViewById(R.id.layout);
                background.setBackgroundColor(to);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startButton:
                Log.i(TAG, "Start:" + start);
                    start = 0;
                    SharedPreferences algorytm = PreferenceManager.getDefaultSharedPreferences(this);
                    this.register();
                    Log.i(TAG, "algorytm:  "+algorytm.getString("transform","0"));
                    i.putExtra("path", algorytm.getString("transform","0"));
                    startService(i);
                break;
            case R.id.nadawca:
                Intent nad=new Intent(MainActivity.this,UserListActivity.class);
                startActivityForResult(nad, this.NADAWCA_RESULS);
                break;
            case R.id.odbiorca:
                Intent odb=new Intent(MainActivity.this,UserListActivity.class);
                startActivityForResult(odb, this.ODBIORCA_RESULS);
                break;


        }
    }
}

