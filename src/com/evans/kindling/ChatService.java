package com.evans.kindling;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.evans.kindling.model.ChatMessage;
import com.evans.kindling.model.Room;
import com.github.kevinsawicki.http.HttpRequest;

public class ChatService extends Service {
	public static String BROADCAST_ACTION = "com.evans.kindling.NEWMESSAGE";

	private static Set<Room> activeRooms = new ConcurrentSkipListSet<Room>();
	static final int UPDATE_INTERVAL= 1000;
	private static String token;
	private Timer timer = new Timer();

	private static ChatService instance = null;

	public static boolean isInstanceCreated() { 
		return instance != null; 
	}//met

	public static ChatService getInstance(){
		return instance;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		instance = this;
		Log.e("Kindling", "Service Started");
		SharedPreferences preferences = this.getSharedPreferences("Kindling", MODE_PRIVATE);
		token = preferences.getString("token", null);
		doSomethingRepeatedly();
		//Log.d(TAG, "onCreate");

		//		player = MediaPlayer.create(this, R.raw.braincandy);
		//		player.setLooping(false); // Set looping
	}

	private void doSomethingRepeatedly() {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				//Log.e("Kindling", "Fetching new messages for room " + activeRooms.toArray()[0]);
				for(Room r : getActiveRooms()){
					HttpRequest response = HttpRequest.get("https://michaelevans.campfirenow.com/room/"+r.getId()+"/recent.json?since_message_id="+r.getLastMessageId()).basic(token, "x");
					String body = response.body();
					try {
						JSONObject object = new JSONObject(body);
						JSONArray array = object.getJSONArray("messages");
						for(int i=0;i<array.length();i++){
							JSONObject temp = array.getJSONObject(i);
							ChatMessage cm = new ChatMessage();
							if(temp.getString("body") != null && !temp.getString("body").equals("null")){
								cm.setId(temp.getInt("id"));
								cm.setBody(temp.getString("body"));
								Log.d("Kindling", ""+ cm.getId());
								//							if(temp.getString("user_id") != null)
								//								cm.setUserId(temp.getInt("user_id"));
								//if(temp.getInt("id")<r.getLastMessageId())
									//if(r.addMessage(cm)){
										Intent broadcast = new Intent();
										Bundle b = new Bundle();
										b.putInt("room", r.getId());
										b.putParcelable("message", cm);
										broadcast.putExtras(b);
										broadcast.setAction(BROADCAST_ACTION);
										sendBroadcast(broadcast);
									//}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						break;
					}
				}
				//Log.d("MyService", String.valueOf(++counter));
			}
		}, 0,UPDATE_INTERVAL);
	}
	@Override
	public void onDestroy() {
		String temp = "";
		for(Room r : getActiveRooms()){
			temp += r.getName() + " ";
		}
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		instance = null;
		timer.cancel();
		Log.e("Kindling", temp + " Service Stopped");
		//		Log.d(TAG, "onDestroy");
		//		player.stop();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		//		Log.d(TAG, "onStart");
		//		player.start();
	}

	public static Set<Room> getActiveRooms() {
		return activeRooms;
	}

	public static void setActiveRooms(Set<Room> activeRooms) {
		ChatService.activeRooms = activeRooms;
	}
}
