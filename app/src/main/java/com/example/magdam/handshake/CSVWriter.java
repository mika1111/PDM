package com.example.magdam.handshake;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Magda M on 2015-01-10.
 */
public class CSVWriter {
    public static final String TAG = CSVWriter.class.getName();
    FileOutputStream fOut;
    OutputStreamWriter myOutWriter;
    //  Context c;
    public CSVWriter(String file) {
        // this.c=c;
        Log.i(TAG, Environment.getExternalStorageState());
        if(isExternalStorageWritable()==true){

            try{

                File dir = new File ("/mnt/ext_card/Download");
                // File dir = new File ("/mnt/external_sd/Serwis");
                //File dir = new File ("/storage/extSdCard/Serwis2");
                // File dir = Environment.getExternalStorageDirectory();
                //       File dir = new File("/storage/emulated/0/DCIM");
                dir.mkdir();
                File file2 = new File(dir, file);
                fOut = new FileOutputStream(file2);
                myOutWriter = new OutputStreamWriter(fOut);
                Log.i(TAG, "Stworzenie pliku:"+file2.getAbsolutePath()+(file2==null)+"fout"+(fOut==null)+"writer:"+(myOutWriter==null));

            }catch (Exception e) {
                Log.i(TAG, e.toString());
            }

        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void write(String row) {

        Log.i(TAG, "dodanie lini:"+row);
        Log.i(TAG, "istnieje?"+(myOutWriter==null));

        try{
            myOutWriter.append(row);
            myOutWriter.flush();
        }catch (FileNotFoundException ex) {
            Log.i(TAG, ex.toString());
        } catch (IOException e) {
            Log.i(TAG, e.toString());
        }

    }

    public void close() {
        try{
            myOutWriter.close();
            fOut.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
