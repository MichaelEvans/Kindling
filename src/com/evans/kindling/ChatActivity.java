package com.evans.kindling;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.evans.kindling.model.ChatMessage;
import com.evans.kindling.model.Room;

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
	static ArrayList<Room> activeRooms;
	private static ChatMessageAdapter adapter;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updateUI(intent);       
        }

		
    };
    private void updateUI(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("Kindling", ""+intent.getExtras().getInt("message"));
		
		if (adapter != null){
			//Log.d("Kindling", "NOTIFY " + .getInt("message"));
			ChatMessage cm = intent.getExtras().getParcelable("message");
			if(activeRooms.get(0).addMessage(cm))
				adapter.notifyDataSetChanged();
		}else{
			
		}
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if(activeRooms == null)
			activeRooms = new ArrayList<Room>();
		Room room = getIntent().getExtras().getParcelable("room");
		Log.e("Kindling", "Entering room: " + room.getName());
		activeRooms.add(room);

		// Create the adapter that will return a fragment for each of the three primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
		registerReceiver(broadcastReceiver, filter);

	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		activeRooms = new ArrayList<Room>();
		unregisterReceiver(broadcastReceiver);
	}
	//    @Override
	//    public boolean onCreateOptionsMenu(Menu menu) {
	//        getMenuInflater().inflate(R.menu.activity_chat, menu);
	//        return true;
	//    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
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
			return activeRooms.get(position).getName();
			//return null;
		}
	}


	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		public DummySectionFragment() {
		}

		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Bundle args = getArguments();
			//View view = inflater.inflate(R.id.roomList, container);
			RelativeLayout rlView = new RelativeLayout(getActivity());
			ListView listView = new ListView(getActivity());
			if(adapter == null)
				adapter = new ChatMessageAdapter(getActivity(), activeRooms.get(args.getInt(ARG_SECTION_NUMBER)-1).getMessages());
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
					Log.e("Kindling", v.getText().toString());
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
