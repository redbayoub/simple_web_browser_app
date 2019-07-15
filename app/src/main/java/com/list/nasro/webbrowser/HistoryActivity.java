package com.list.nasro.webbrowser;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    private DBConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbConnection=new DBConnection(this);

        ListView historyListView=findViewById(R.id.history_listView);


        ArrayList<String> urls=new ArrayList<>();
        for ( WebPage webPage:dbConnection.getData()){
                urls.add(webPage.getUrl());
        }
        Collections.reverse(urls);

        ArrayAdapter<String> webPageArrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,urls);
        historyListView.setAdapter(webPageArrayAdapter);


        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(HistoryActivity.this,MainActivity.class);
                String user_url=(String) parent.getItemAtPosition(position);


                intent.setData(Uri.parse(userInputToUrl(user_url)));
                startActivity(intent);
            }
        });
    }

    private String userInputToUrl(String user_input){
        if(user_input.startsWith("http://") || user_input.startsWith("https://")){
            return user_input;
        }else{
            return "http://"+user_input;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.close_mi:{
                startActivity(new Intent(this,MainActivity.class));
                return true;
            }
            case R.id.clear_mi:{
                showClearDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showClearDialog() {
        AlertDialog.Builder  builder=new AlertDialog.Builder(this);
        builder.setTitle("Clear History Confirmation")
                .setMessage("Are you sure ?")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbConnection.clearData();
                        startActivity(new Intent(HistoryActivity.this,MainActivity.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();

    }
}
