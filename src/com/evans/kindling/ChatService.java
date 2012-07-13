package com.evans.kindling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
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
	private static HashMap<Room, Integer> lastMessageForRoom = new HashMap<Room, Integer>();
	static final int UPDATE_INTERVAL= 1000;
	private static String token;
	private Timer timer = new Timer();
	private static ChatService instance = null;
	private HashMap<String,String> userNames = new HashMap<String,String>();
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private String hold;

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
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
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
					
					Log.w("Kindling", "" + lastMessageForRoom.get(r) + " " + r.getMessages().size());
					HttpRequest response = HttpRequest.get("https://michaelevans.campfirenow.com/room/"+r.getId()+"/recent.json?since_message_id="+lastMessageForRoom.get(r)).basic(token, "x");
					//HttpRequest response2 = HttpRequest.get("https://michaelevans.campfirenow.com/users/1090301.json").basic(token, "x");
					//Log.d("testA", response2.body());
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
								cm.setDate(simpleDateFormat.parse(temp.getString("created_at")).toLocaleString());
								hold = temp.getString("user_id");
								if(!userNames.containsKey(hold)){
									HttpRequest resp = HttpRequest.get("https://michaelevans.campfirenow.com/users/"+hold+".json").basic(token, "x");
									String b = resp.body();
									Log.d("testA",b);
									JSONObject jsn = new JSONObject(b);
									try{
										b = jsn.getJSONObject("user").getString("name");
										Log.d("testA",b);
										userNames.put(hold, b);
									}catch(JSONException e1){
										Log.d("testA","name json fail");
									}
								}
								cm.setAuthor(userNames.get(hold));
								if(!r.containsMessage(cm)){
									Log.d("Kindling", ""+ cm.getId() + " " +r.getLastMessageId());
									//							if(temp.getString("user_id") != null)
									//								cm.setUserId(temp.getInt("user_id"));
									//if(temp.getInt("id")<r.getLastMessageId())
									//if(r.addMessage(cm)){
									lastMessageForRoom.put(r, cm.getId());
									Intent broadcast = new Intent();
									Bundle b = new Bundle();
									b.putInt("room", r.getId());
									b.putParcelable("message", cm);
									//Log.d("testA", "CS"+cm.output2());
									broadcast.putExtras(b);
									broadcast.setAction(BROADCAST_ACTION);
									sendOrderedBroadcast(broadcast, null);
								}
								//}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						break;
					} catch (ParseException e) {
						//Catch for simpleDate parser 
						e.printStackTrace();
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
	public static void addRoom(Room r){
		activeRooms.add(r);
		lastMessageForRoom.put(r, 0);
	}
}
