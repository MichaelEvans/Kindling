package com.evans.kindling;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.evans.kindling.model.Room;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class RoomActivity extends Activity {

	private String token;
	private ListView roomListView;
	private ArrayList<Room> roomList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);
		roomListView = (ListView) findViewById(R.id.roomList);
		roomListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    // When clicked, show a toast with the TextView text
				Intent i = new Intent(RoomActivity.this, ChatActivity.class);
				Bundle b = new Bundle();
				Room r = roomList.get(position);
				//Log.e("Kindling", r.toString());
		        b.putParcelable("room", r);
				i.putExtras(b);
//				//extras.putParcelable("room", r);
//				
				
				
				ChatService.getActiveRooms().add(r);
				Log.e("Kindling", Integer.toString(ChatService.getActiveRooms().size()));
				
				startActivity(i);
			}
		});
		Intent serviceIntent = new Intent(getApplicationContext(), ChatService.class);
		if(!ChatService.isInstanceCreated())
			startService(serviceIntent);
		
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
		protected void onPostExecute(ArrayList<Room> roomlist) {
			roomList = roomlist;
			//new RoomInfoFetch().execute(list);
//			for(Room r : list){
//				Log.e("Kindling", r.getName() + " " + r.getUserCount());
//			}
			//			String str[] = (String []) list.toArray (new String[list.size()]);
			RoomListAdapter adapter = new RoomListAdapter(getApplicationContext(), roomlist);
			roomListView.setAdapter(adapter);
			Log.e("Kindling", "msg");
		}

	}

	//	class RoomInfoFetch extends AsyncTask<ArrayList<Room>, String, ArrayList<Room>>{
	//
	//		@Override
	//		protected ArrayList<Room> doInBackground(ArrayList<Room>... params) {
	//			// TODO Auto-generated method stub
	//			ArrayList<Room> rooms = params[0];
	//			ArrayList<Room> newList = new ArrayList<Room>();
	//			for(Room temp : rooms){
	//				HttpRequest response = HttpRequest.get("https://michaelevans.campfirenow.com/room/"+ temp.getId() +".json").basic(token, "x");
	//				String body = response.body();
	//				try {
	//					JSONObject json = new JSONObject(body);
	//					JSONObject room = json.getJSONObject("room");
	//					temp.setUserCount(room.getJSONArray("users").length());
	//				} catch (JSONException e) {
	//					e.printStackTrace();
	//				}
	//			}
	//			return newList;
	//		}
	//
	//		@Override
	//		protected void onPostExecute(ArrayList<Room> list) {
	//			
	//		}
	//		//		@Override
	//		//		protected void onPostExecute(Room room) {
	//		//			
	//		//		}
	//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_room, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//ChatActivity.removeAllRooms();
		Intent intent = new Intent(RoomActivity.this, ChatService.class);
		stopService(intent);
	}

}
