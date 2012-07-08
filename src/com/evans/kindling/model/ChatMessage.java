package com.evans.kindling.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatMessage implements Comparable<ChatMessage>, Parcelable{
	private Integer id;
	private String body;
	private int roomId;
	private int userId;
	private Date createdAt;
	private boolean starred;
	public ChatMessage(){
		
	}
	public ChatMessage(Parcel in){
		String[] data = new String[2];
		in.readStringArray(data);
		this.id = Integer.parseInt(data[0]);
		this.body = data[1];
	}
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public boolean isStarred() {
		return starred;
	}
	public void setStarred(boolean starred) {
		this.starred = starred;
	}
	@Override
	public int compareTo(ChatMessage another) {
		return this.getId().compareTo(another.getId());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { Integer.toString(id), body });
		
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public ChatMessage createFromParcel(Parcel in) {
			return new ChatMessage(in); 
		}

		public ChatMessage[] newArray(int size) {
			return new ChatMessage[size];
		}
	};
}
