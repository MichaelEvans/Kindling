package com.evans.kindling;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("storedInt", storedPreference); // value to store
//        editor.commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_login, menu);
//        return true;
//    }

    
}
