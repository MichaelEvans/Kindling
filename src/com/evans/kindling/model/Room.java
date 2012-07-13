package com.evans.kindling.model;

import java.util.ArrayList;
import java.util.TreeSet;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable, Comparable<Room>{
	private String name;
	//	private Date createdAt;
	//	private Date updatedAt;
	//	private String topic;
	private Integer id;
	private ArrayList<String> usersInRoom;
	private Integer userCount;
	private TreeSet<ChatMessage> recentMessages = new TreeSet<ChatMessage>();
	public Room(){
		
	}
	public Room(Parcel in){
		String[] data = new String[2];
		in.readStringArray(data);
		this.id = Integer.parseInt(data[0]);
		this.name = data[1];
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ArrayList<String> getUsersInRoom() {
		return usersInRoom;
	}
	public void setUsersInRoom(ArrayList<String> usersInRoom) {
		this.usersInRoom = usersInRoom;
	}
	public Integer getUserCount() {
		return userCount;
	}
	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}
	//@Override
	public int describeContents() {
		return 0;
	}
	//@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { Integer.toString(id), name });
		//		dest.writeInt(id);
		//		dest.writeString(name);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Room createFromParcel(Parcel in) {
			return new Room(in); 
		}

		public Room[] newArray(int size) {
			return new Room[size];
		}
	};
	public boolean containsMessage(ChatMessage message){
		return recentMessages.contains(message);
	}
	public boolean addMessage(ChatMessage message){
		boolean added = recentMessages.add(message);
//		if(recentMessages.size()>100){
//			Iterator<Integer> i = recentMessages.keySet().iterator();
//			Integer next = i.next();
//			recentMessages.remove(next);
//		}
		return added;
	}
	public int getLastMessageId(){
		try{
			return recentMessages.first().getId();
		}catch(Exception e){
			return 0;
		}
	}
	public TreeSet<ChatMessage> getMessages(){
		return recentMessages;
	}
	//@Override
	public int compareTo(Room another) {
		return this.getId().compareTo(another.getId());
	}
}
