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
	private String date;
	
	public ChatMessageAdapter(Context ctx, TreeSet<ChatMessage> messages) {
		values = messages;
		inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
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
		ChatMessage message = Iterables.get(values, position);
		if(convertView==null){
			vi = inflater.inflate(R.layout.message_list_item, null);
			holder=new ViewHolder();
			holder.author=(TextView)vi.findViewById(R.id.message_author);
			holder.message=(TextView)vi.findViewById(R.id.message_text);
			holder.date=(TextView)vi.findViewById(R.id.message_date);
			vi.setTag(holder);
		}
		else{
			holder=(ViewHolder)vi.getTag();
		}
		holder.author.setText(message.getAuthor()+":");
		holder.message.setText(message.getBody());
		//Log.d("testA","Display"+message.output2());
		holder.date.setText(message.getDate());
		//holder.date.setText(room.getCreatedAt().toString());
		/*date = room.getCreatedAt();
		holder.date.setText(""+
		date.getDay() + " " +
		date.getHours() + ":" +
		date.getMinutes());
		*/
		//holder.count.setText("5");//TODO: Update this
		return vi;
	}
	public static class ViewHolder{
		public TextView author;
		public TextView message;
		public TextView date;
	}
}
