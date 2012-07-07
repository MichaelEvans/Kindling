package com.evans.kindling;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class RoomActivity extends Activity {
	
	private String token;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        token = preferences.getString("token", null);
        
        if(token == null){
        	Intent intent = new Intent();
            intent.setClass(RoomActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_room, menu);
        return true;
    }

    
}
