package com.evans.kindling;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.evans.kindling.listadapters.ChatMessageAdapter;
import com.evans.kindling.model.ChatMessage;
import com.evans.kindling.model.Room;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.google.common.collect.Iterables;

public class ChatActivity extends FragmentActivity {
	public static String BROADCAST_ACTION = "com.evans.kindling.NEWMESSAGE";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
	 * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	static Set<Room> activeRooms;
	//private static ChatMessageAdapter adapter;
	private static String token;
	private HashMap<Room, Fragment> mapping;
	private static HttpClient httpclient = new DefaultHttpClient();
	private Room room;
	private AlertDialog alert;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);   
			abortBroadcast();
		}


	};
	private void updateUI(Intent intent) {
		// TODO Auto-generated method stub
		//Log.d("Kindling", ""+intent.getExtras().getInt("message"));

		//if (adapter != null){
		//Log.d("Kindling", "NOTIFY " + .getInt("message"));
		int roomId = intent.getExtras().getInt("room");
		ChatMessage cm = intent.getExtras().getParcelable("message");
		//Log.d("testA", "ChatActivity"+ cm.output());
		for(Room r : activeRooms){
			if(r.getId() == roomId){
				if(r.addMessage(cm))
					((DummySectionFragment) mapping.get(r)).getAdapter().notifyDataSetChanged();
			}
		}

		//		}else{
		//
		//		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		SharedPreferences preferences = this.getSharedPreferences("Kindling", MODE_PRIVATE);
		token = preferences.getString("token", null);

		mapping = new HashMap<Room, Fragment>();

		if(activeRooms == null)
			activeRooms = new TreeSet<Room>();
		room = getIntent().getExtras().getParcelable("room");
		Log.d("testA", "Room id is:"+room.getId());
		Log.e("Kindling", "Entering room: " + room.getName());
		activeRooms.add(room);
		Log.d("testA","OnCreate was called");
		new RequestEnter().execute("https://michaelevans.campfirenow.com/room/"+room.getId()+"/join.xml");

		// Create the adapter that will return a fragment for each of the three primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Log.e("Kindling", "" + position);
				((DummySectionFragment) mapping.get(Iterables.get(activeRooms, position))).scrollToBottom();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_ACTION);
		registerReceiver(broadcastReceiver, filter);

	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		//activeRooms = new TreeSet<Room>();
		unregisterReceiver(broadcastReceiver);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_chat, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_users:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Who\'s here?");
			builder.setMessage("Loading");
			alert = builder.create();
			new RequestUsers().execute("https://michaelevans.campfirenow.com/room/"+room.getId()+".json");
			return true;
		case R.id.menu_leave:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("Leave");
			builder2.setMessage("Loading");
			alert = builder2.create();
			new Requestleave().execute("https://michaelevans.campfirenow.com/room/"+room.getId()+"/leave.xml");
			return true;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private class RequestUsers extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg) {
			HttpRequest resp = null;
			StringBuffer names = new StringBuffer();
			try {
				resp = HttpRequest.get(arg[0]).basic(token, "x");
				JSONObject jsn = new JSONObject(resp.body());
				JSONArray jArray = jsn.getJSONObject("room").getJSONArray("users");
				for(int i=0;i<jArray.length();i++){
					jsn = jArray.getJSONObject(i);
					names.append(jsn.getString("name") +"\n");
				}
			} catch (HttpRequestException e) {
				Log.d("testA","http failed!");
			} catch (JSONException e) {
				Log.d("testA","Json failed!");
			}
			if(resp == null)
				return "Request Failed";
			else
				return names.toString();
		}
		protected void onPreExecute(){
			alert.show();
		}
		protected void onPostExecute(String result) {
			alert.setMessage(result);
	    }
		
	}
	
	private class RequestEnter extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg) {
			HttpResponse r = null;
			try {
				URI url= new URI(arg[0]);
				HttpPost httppost = new HttpPost(url);
				httppost.setHeader(new BasicHeader("Authorization", "Basic " + new String(Base64.encode((token + ":" + "X").getBytes(),Base64.NO_WRAP))));
				httppost.setHeader(new BasicHeader("Content-Type", "application/xml"));
				//HttpResponse r
				r = httpclient.execute(httppost);
			} catch (HttpRequestException e) {
				Log.d("testA","Leave http failed!");
			} catch (URISyntaxException e) {
				Log.d("testA","URI Syntax problem!");
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				Log.d("testA","execute problem!");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(r == null)
				return "Request Failed";
			else
				return "\nStatus: "+ r.getStatusLine().getStatusCode();
		}
		protected void onPostExecute(String result) {
	    }
	}
	
	private class Requestleave extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg) {
			HttpResponse r = null;
			try {
				URI url= new URI(arg[0]);
				HttpPost httppost = new HttpPost(url);
				httppost.setHeader(new BasicHeader("Authorization", "Basic " + new String(Base64.encode((token + ":" + "X").getBytes(),Base64.NO_WRAP))));
				httppost.setHeader(new BasicHeader("Content-Type", "application/xml"));
				//HttpResponse r
				r = httpclient.execute(httppost);
			} catch (HttpRequestException e) {
				Log.d("testA","Leave http failed!");
			} catch (URISyntaxException e) {
				Log.d("testA","URI Syntax problem!");
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				Log.d("testA","execute problem!");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(r == null)
				return "Request Failed";
			else
				return "\nStatus: "+ r.getStatusLine().getStatusCode();
		}
		protected void onPostExecute(String result) {
			alert.setMessage(result);
			alert.show();
			finish();
	    }
	}




	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Log.e("Kindling", "Flicked to view");
			Fragment fragment = new DummySectionFragment();
			mapping.put(Iterables.get(activeRooms, i), fragment);
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return activeRooms.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			//            switch (position) {
			//                case 0: return getString(R.string.title_section1).toUpperCase();
			//                case 1: return getString(R.string.title_section2).toUpperCase();
			//                case 2: return getString(R.string.title_section3).toUpperCase();
			//            }
			return Iterables.get(activeRooms, position).getName();
			//return null;
		}
	}
	public static void removeAllRooms(){
		activeRooms = new HashSet<Room>();
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		private ChatMessageAdapter adapter;
		private ListView listView;
		public DummySectionFragment() {

		}
		public ChatMessageAdapter getAdapter(){
			return adapter;
		}
		public void scrollToBottom(){
			listView.setSelection(adapter.getCount());
		}
		public static final String ARG_SECTION_NUMBER = "section_number";

		class SubmitMessage extends AsyncTask<String, String, String> {
			@Override
			protected String doInBackground(String... arg0) {
				Log.e("Kindling", "TEST: "+arg0[0] + " " +arg0[1]);
				String message = "<message><type>TextMessage</type><body>"+arg0[0]+"</body></message>";
				int roomId = Integer.parseInt(arg0[1]);
				String url = "https://michaelevans.campfirenow.com/room/"+ roomId +"/speak.xml";
				Log.e("Kindling", message + "\n" + url);

				//return null;

				

				HttpPost httppost = new HttpPost(url);
				httppost.setHeader(new BasicHeader("Authorization", "Basic " + new String(Base64.encode((token + ":" + "X").getBytes(),Base64.NO_WRAP))));
				httppost.setHeader(new BasicHeader("Content-Type", "application/xml"));
				try {
					HttpEntity entity = new StringEntity(message);
					httppost.setEntity(entity);

					// Execute HTTP Post Request
					httpclient.execute(httppost);
					//TODO: If fail, tell user
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
				return null;
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Log.e("Kindling", "Switched to view");
			final Bundle args = getArguments();
			//View view = inflater.inflate(R.id.roomList, container);
			RelativeLayout rlView = new RelativeLayout(getActivity());
			listView = new ListView(getActivity());
			if(adapter == null)
				adapter = new ChatMessageAdapter(getActivity(), Iterables.get(activeRooms, args.getInt(ARG_SECTION_NUMBER)).getMessages());
			listView.setSelection(Iterables.get(activeRooms, args.getInt(ARG_SECTION_NUMBER)).getMessages().size());
			listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			listView.setAdapter(adapter);
			listView.setId(1111);
			//return listView;
			RelativeLayout.LayoutParams lvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
			RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			EditText edit = new EditText(getActivity());
			edit.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if(v.getText().toString().length()>0)
						new SubmitMessage().execute(v.getText().toString(), String.valueOf(Iterables.get(activeRooms, args.getInt(ARG_SECTION_NUMBER)).getId()));
					v.setText("");
					return true;
				}
			});
			edit.setId(2222);
			tvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			lvParams.addRule(RelativeLayout.ABOVE, 2222);
			rlView.addView(edit, tvParams);
			rlView.addView(listView, lvParams);
			return rlView;

			//			TextView textView = new TextView(getActivity());
			//			textView.setGravity(Gravity.CENTER);

			//			textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
			//			return textView;

		}
	}
}
