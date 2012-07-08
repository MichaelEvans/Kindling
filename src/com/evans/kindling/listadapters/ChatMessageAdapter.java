package com.evans.kindling.listadapters;

import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evans.kindling.R;
import com.evans.kindling.model.ChatMessage;
import com.google.common.collect.Iterables;

public class ChatMessageAdapter extends BaseAdapter {

	private final TreeSet<ChatMessage> values;
	private static LayoutInflater inflater;
	
	public ChatMessageAdapter(Context ctx, TreeSet<ChatMessage> messages) {
		values = messages;
		inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.size();
	}

	@Override
	public Object getItem(int k) {
		ChatMessage element = Iterables.get(values, k);
		return element;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		ViewHolder holder;
		ChatMessage room = Iterables.get(values, position);
		if(convertView==null){
			vi = inflater.inflate(R.layout.room_list_item, null);
			holder=new ViewHolder();
			holder.title=(TextView)vi.findViewById(R.id.firstLineTextView);
			//holder.count=(TextView)vi.findViewById(R.id.secondLineTextView);
			vi.setTag(holder);
		}
		else
			holder=(ViewHolder)vi.getTag();

		holder.title.setText(room.getBody());
		//holder.count.setText("1");//TODO: Update this
		return vi;
	}
	public static class ViewHolder{
		public TextView title;
		public TextView count;
	}
}
