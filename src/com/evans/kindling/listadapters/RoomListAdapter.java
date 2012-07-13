package com.evans.kindling.listadapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evans.kindling.R;
import com.evans.kindling.model.Room;

public class RoomListAdapter extends BaseAdapter {

	private final ArrayList<Room> values;
	private static LayoutInflater inflater;

	public RoomListAdapter(Context ctx, ArrayList<Room> rooms) {
		values = rooms;
		inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return values.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		return values.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		ViewHolder holder;
		Room room = values.get(position);
		if(convertView==null){
			vi = inflater.inflate(R.layout.room_list_item, null);
			holder=new ViewHolder();
			holder.title=(TextView)vi.findViewById(R.id.firstLineTextView);
			holder.count=(TextView)vi.findViewById(R.id.secondLineTextView);
			vi.setTag(holder);
		}
		else
			holder=(ViewHolder)vi.getTag();

		holder.title.setText(room.getName());
		holder.count.setText("1");//TODO: Update this
		return vi;
	}

	public static class ViewHolder{
		public TextView title;
		public TextView count;
	}
}
