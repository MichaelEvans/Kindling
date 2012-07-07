package com.evans.kindling.model;

import java.util.ArrayList;
import java.util.Date;

public class Room {
	private String name;
	private Date createdAt;
	private Date updatedAt;
	private String topic;
	private Integer id;
	private ArrayList<String> usersInRoom;
	
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
}
