package com.example.magdam.handshake;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Magda M on 2015-01-24.
 */
public class LoadUsers {
    String adresKodu = null;
    public static final String TAG = LoadUsers.class.getName();

    public String getNearest(double x, double y){
        String result=null;
        adresKodu="http://magda.istalacar.com/sciana.php";
        String scianka=null;
        try {
            MyAsyncTask ast=new MyAsyncTask();
            result = ast.execute(x, y).get();
            JSONArray jArray = new JSONArray(result);
            JSONObject jObject = jArray.getJSONObject(0);
            scianka=jObject.getString("Nazwa");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scianka;
    }
    public ArrayList<String> getAllUsers(){
        String result=null;
        adresKodu="http://magda.istalacar.com/userList.php";
        ArrayList<String> users=null;
        try {
            MyAsyncTask ast=new MyAsyncTask();
            result = ast.execute().get();
            JSONArray jArray = new JSONArray(result);
            if(jArray.length()>0){
                users=new ArrayList<String>();
            }
            for(int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                users.add(jObject.getString("NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private class MyAsyncTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... args) {
            String result = "";
            InputStream is = null;
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for (double cos : args) {

                nameValuePairs.add(new BasicNameValuePair("x", Double.toString(cos)));
            }

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(adresKodu);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i(TAG, "Baza wyniki: " + result);
            return result;
        }
    }

}
