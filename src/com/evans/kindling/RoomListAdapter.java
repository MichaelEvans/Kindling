package com.evans.kindling;

import android.content.Context;
import android.widget.ArrayAdapter;

public class RoomListAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final String[] values;

	public RoomListAdapter(Context context, String[] values) {
		super(context, R.layout.activity_login, values);
		this.context = context;
		this.values = values;
	}
}
