package com.ds.model;

import java.util.HashMap;
import java.util.Map;

public class NewUserData {
	/**
	 * 用户数据.因为整个项目需要用到，用户数据模型定义为静态的Map类型。省去的javabean在各个类传递的麻烦
	 * */
	public static Map<String, String> user = new HashMap<String, String>();

	/**
	 * 判断是否在线
	 * 
	 * */
	public static boolean isOnline() {

		if (user != null && user.get("userid") != null)			
			return true;
		else
			return false;
	}

	public static void setUserid(String userid) {
		user.put("userid", userid);

	}

	public static String getUserid() {
		return user.get("userid");
	}

	public static void setSendTime(String sendtime) {
		user.put("sendtime", sendtime);
	}

	public static String getSendTime() {
		return user.get("sendtime");
	}

	public static void setDeviceId(String deviceId) {
		user.put("deviceId", deviceId);

	}

	public static String getDeviceId() {
		return user.get("deviceId");
	}

}
