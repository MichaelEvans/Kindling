package com.evans.kindling;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.evans.kindling.model.Room;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RoomActivity extends Activity {

	private String token;
	private ListView roomListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);
		roomListView = (ListView) findViewById(R.id.roomList);

		SharedPreferences preferences = this.getSharedPreferences("Kindling", MODE_PRIVATE);
		token = preferences.getString("token", null);
		Log.e("Kindling", "Token: " + token);


		if(token == null){
			Intent intent = new Intent();
			intent.setClass(RoomActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}

		new RoomFetch().execute();


	}

	class RoomFetch extends AsyncTask<String, String, ArrayList<Room>> {

		@Override
		protected ArrayList<Room> doInBackground(String... arg0) {
			HttpRequest response = HttpRequest.get("https://michaelevans.campfirenow.com/rooms.json").basic(token, "x");
			ArrayList<Room> roomList = new ArrayList<Room>();
			String body = response.body();
			Log.e("Kindling", body);
			try {
				JSONObject rooms = new JSONObject(body);
				JSONArray roomArray = rooms.getJSONArray("rooms");
				for(int i=0; i<roomArray.length(); i++){
					JSONObject obj = (JSONObject) roomArray.get(i);
					Room room = new Room();
					room.setId(obj.getInt("id"));
					room.setName(obj.getString("name"));
					new RoomInfoFetch().execute(room);
					roomList.add(room);
				}
			} catch (HttpRequestException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return roomList;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Room> list) {
			
//			String str[] = (String []) list.toArray (new String[list.size()]);
//			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
//					android.R.layout.simple_list_item_1, android.R.id.text1, str);
//
//			roomListView.setAdapter(adapter);
		}

	}
	
	class RoomInfoFetch extends AsyncTask<Room, String, Room>{

		@Override
		protected Room doInBackground(Room... params) {
			// TODO Auto-generated method stub
			Room temp = params[0];
			HttpRequest response = HttpRequest.get("https://michaelevans.campfirenow.com/room/"+ temp.getId() +".json").basic(token, "x");
			String body = response.body();
			try {
				JSONObject json = new JSONObject(body);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("Kindling", "Room Info: " + body);
			return params[0];
		}
		
//		@Override
//		protected void onPostExecute(Room> room) {
//			
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_room, menu);
		return true;
	}


}
