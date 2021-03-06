package com.example.magdam.handshake;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.lang.Exception;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDPSender extends AsyncTask<String, Integer, String> {
    private static final String MUSIC = "music";
    int UDP_SERVER_PORT=11111;
    String nadawca;
    String odbiorca;
    String ip;
    boolean listen;
    String NAD_PREF="Nadawca";
    String ODB_PREF="Odbiorca";
    Context c;
    int from =Color.BLUE;
    MainActivity view;
    MediaPlayer mp;
    int progress=0;
    int length=0;
    JSONObject w;
    public UDPSender(boolean l,  JSONObject wiadomosc, Context context){
        c=context;
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        ip=SP.getString("IP","127.0.0.1");
        SharedPreferences settings = context.getSharedPreferences(this.ODB_PREF, 0);
        String odbiorca = settings.getString(this.ODB_PREF, "");
        SharedPreferences nadawcaP = context.getSharedPreferences(this.NAD_PREF, 0);
        String nadawca = nadawcaP.getString(this.NAD_PREF, "");

        listen=l;
        w=wiadomosc;
        try {
            w.put("nadawca", nadawca);
            w.put("adresat", odbiorca);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected String doInBackground(String... messages) {
        String udpMsg=w.toString();
        runUdpClient(udpMsg);
        return null;
    }
    public void setListen(boolean l){
        listen=l;
    }


    private long setVibration(long r){
        if(r>10){
            return 10;
        }
        else{
            return r;
        }
    }
    public void runUdpClient(String udpMsg)  {

        DatagramSocket ds = null;

        try {

            ds = new DatagramSocket();

            InetAddress serverAddr = InetAddress.getByName(ip);

            DatagramPacket dp;

            dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, UDP_SERVER_PORT);

            ds.send(dp);
            Log.d("UDP", "Send "+udpMsg);
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while(listen) {
                Log.d("UDP","Waiting..");
                        ds.receive(packet);
                String senderIP = packet.getAddress().getHostAddress();
                String message = new String(packet.getData()).trim();
                Log.d("UDP", "Got UDB from " + senderIP + ", message: " + message);
                String[] values=message.replace("[", "").replace("]", "").split(",");
                if(values.length>=2) {
                    try{
                        Long fv=Long.parseLong(values[0]);
                        Vibrations v = new Vibrations(10, setVibration(fv), c);
                        v.vibrate();
                        Log.d("UDP", "Got UDB from " + senderIP + ", message: " + message);
                        Integer color=Integer.parseInt(values[1]);
                        publishProgress(color);
                    } catch (Exception ex) {
                        Log.d("UDP", "Cannot parse "+ex.getMessage());
                    }

                } else {
                    Log.w("UDP", "Got UDB from " + senderIP + ", message: " + message);
                }
            }


        } catch (SocketException e) {

            e.printStackTrace();

        }catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (ds != null) {

                ds.close();

            }

        }

    }
    private int ZERO=0;
    @Override
    public void onProgressUpdate(Integer... progress) {
        this.length=this.length+1;
        if(progress[0]==0){
            ZERO=ZERO+1;
        }
        this.progress=this.progress+progress[0];
        double color= (double)this.progress/(double)this.length;
        Log.d("COLOR", ""+color+","+progress[0]+","+this.progress+","+this.length);
        view.setColor(color);
        view.startMusic(color);
        view.stopMusic();
      //  ;
     }
    public void setBacgoud(MainActivity color) {
        view = color;
    }
    @Override
    protected void onPostExecute(String res) {
        Log.d("UDP", "Postexecute "+res);
    }
}
