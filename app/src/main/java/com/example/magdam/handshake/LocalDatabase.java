package com.example.magdam.handshake;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
/**
 * Created by Magdalena on 2016-06-27.
 */
public class LocalDatabase extends SQLiteOpenHelper {

        public static final String TAG = LocalDatabase.class.getName();
        // Database Version


        String DATABASE_VERSION = "0.0.4";
        // Database Name
        private static final String DATABASE_NAME = "users";


        public LocalDatabase(Context context) {
            super(context, DATABASE_NAME, null, 3);
        }

        public void addUser(User u){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", u.id);
            values.put("name", u.name);
            values.put("surname", u.surname);
            values.put("googleId", u.googleId);
            db.insert("users", null, values);
            db.close();
        }

        public void setNadawca(User u){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", u.id);
            values.put("name", u.name);
            values.put("surname", u.surname);
            values.put("googleId", u.googleId);
            db.insert("users", null, values);
            db.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Cursor cursor = SQLiteDatabase.openOrCreateDatabase(":memory:", null).rawQuery("select sqlite_version() AS sqlite_version", null);
            while (cursor.moveToNext()) {
                DATABASE_VERSION += cursor.getString(0);
            }

            Log.i(TAG, "wersja sql"+DATABASE_VERSION);
            String CREATE_USERS_TABLE = "CREATE TABLE users (id INTEGER PRIMARY KEY, " +
                    "name TEXT,"+
                    "surname TEXT," +
                    "displayName TEXT," +
                    "googleId TEXT" +
                    ")";
            String CREATE_HELPER_TABLE = "CREATE TABLE helpers (id INTEGER PRIMARY KEY, " +
                    "nadawca INTEGER,"+
                    "updateTime INTEGER DEFAULT 0" +
                    ")";
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_HELPER_TABLE);
        }
        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + "users");
            db.execSQL("DROP TABLE IF EXISTS " + "helpers");
            Log.i(TAG, "Drop databases");
            // Create tables again
            onCreate(db);
        }

    public void setUpdate(long time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("updateTime", time);
        db.update("helpers", cv, "id=0", null);
        db.close();
    }

    public void initUpdate(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", 0);
        cv.put("updateTime", "");
        db.insert("helpers", null, cv);
        db.close();
    }
    public long getLastUpdated(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor mCount= db.rawQuery("select updateTime from helpers where id=0;", null);
        mCount.moveToFirst();
        if(mCount.getCount()>=1) {
            long count = (long)mCount.getInt(0);
            mCount.close();
            return count;
        } else {
            this.initUpdate();
            return 0;
        }
    }
        public ArrayList<User> allUsers(){
            long last=this.getLastUpdated();

            Long current = System.currentTimeMillis()/1000;
            this.setUpdate(current);
            if(current-last>60*60*1000){
                LoadUsers usersDB=new LoadUsers();
                Log.d(TAG, Long.toString(last));
                ArrayList<User> users=usersDB.getAllUsers(last);
                for(int i=0; i<users.size();i++) {
                    Log.d(TAG, users.get(i).toString());
                    this.addUser(users.get(i));
                }
                this.setUpdate(current);
            }
            SQLiteDatabase db=this.getReadableDatabase();
            ArrayList<User> wejscia=new ArrayList<User>();
            Cursor cursor= db.rawQuery("select * from users", null);
            if (cursor.moveToFirst()) {
                do {

                    User w=new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
                    Log.d(TAG, "display"+cursor.getString(3));
                    w.setGoogleId(cursor.getString(4));
                    wejscia.add(w);

                } while (cursor.moveToNext());
            }

            cursor.close();
            return wejscia;
        }
    }

