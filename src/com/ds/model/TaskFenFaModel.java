package com.ds.model;

public class TaskFenFaModel {
	private String StartTime;
	
	private String EndTime;
	private String txtname;
	
	
   public String getStartTime() {
		return StartTime;
	}


	public void setStartTime(String startTime) {
		StartTime = startTime;
	}


	public String getEndTime() {
		return EndTime;
	}


	public void setEndTime(String endTime) {
		EndTime = endTime;
	}


	public String getTxtname() {
		return txtname;
	}


	public void setTxtname(String txtname) {
		this.txtname = txtname;
	}


public TaskFenFaModel(String StartTime,String EndTime,String txtname){
	   this.StartTime = StartTime;
	   this.EndTime = EndTime;
	   this.txtname = txtname;
   }
}
