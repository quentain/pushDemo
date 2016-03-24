package com.ds.model;

public class TaskModel {
	private String post;
	
	private String DateTime;
	private String place;
	
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getDateTime() {
		return DateTime;
	}
	public void setDateTime(String dateTime) {
		DateTime = dateTime;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
   public TaskModel(String post,String dateTime,String place){
	   this.post = post;
	   DateTime = dateTime;
	   this.place = place;
   }
}
