package com.example.magdam.handshake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class UserListActivity extends ActionBarActivity {
    private ListView list ;
    private ArrayAdapter<User> adapter ;
    LocalDatabase ldb;
    ArrayList<User> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ldb=new LocalDatabase(this);
        allUsers=ldb.allUsers();
        list = (ListView) findViewById(R.id.listView);
         adapter = new ArrayAdapter<User>(this,
                android.R.layout.simple_list_item_1, allUsers);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                User clicked = allUsers.get(position);
                Intent intent=new Intent();
                intent.putExtra("User", clicked.name);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
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

}
