package com.evans.kindling;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.github.kevinsawicki.http.HttpRequest;

public class LoginActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Button submitButton = (Button) findViewById(R.id.loginButton);
		final EditText login = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);

		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//doLogin(login.getText().toString(), password.getText().toString());	
				new LoginTask().execute(login.getText().toString(), password.getText().toString());
			}
		});
		//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		//        SharedPreferences.Editor editor = preferences.edit();
		//        editor.putInt("storedInt", storedPreference); // value to store
		//        editor.commit();
	}

	protected void doLogin(String username, String password) {
		Log.e("Kindling", username + " " + password);

		//JSONObject results = 



	}
	class LoginTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... credentials) {
			HttpRequest response = HttpRequest.get("https://michaelevans.campfirenow.com/users/me.json").basic(credentials[0], credentials[1]);
			if(response.code() != 200){
				return null;
			}else{
				String body = response.body();
				Log.e("Kindling", body);
				JSONObject user;
				try {
					user = new JSONObject(body);
					return user.getJSONObject("user").getString("api_auth_token");
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}

			}
		}

		@Override
		protected void onPostExecute(String token) {
			if(token != null){
				SharedPreferences preferences = getSharedPreferences("Kindling", MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("token", token); // value to store
				editor.commit();
				
				Intent intent = new Intent();
	            intent.setClass(LoginActivity.this, RoomActivity.class);
	            startActivity(intent);
	            finish();
			}else{
				//TODO: Give an error message;
			}

		}

	}



}
