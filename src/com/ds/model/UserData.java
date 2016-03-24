package com.ds.model;

import java.util.HashMap;
import java.util.Map;

public class UserData {
	/**
	 * �û����.��Ϊ�����Ŀ��Ҫ�õ����û����ģ�Ͷ���Ϊ��̬��Map���͡�ʡȥ��javabean�ڸ����ഫ�ݵ��鷳
	 * */
	public static Map<String, String> user = new HashMap<String, String>();

	/**
	 * �ж��Ƿ�����
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

	public static void setPassword(String password) {
		user.put("password", password);
	}

	public static String getPassword() {
		return user.get("password");
	}

	public static void setDeviceIdentify(String deviceIdentify) {
		user.put("deviceIdentify", deviceIdentify);

	}

	public static String getDeviceIdentify() {
		return user.get("deviceIdentify");
	}
//��½����
	public static void setLoginType(String loginType) {
		user.put("loginType", loginType);

	}

	public static String getLoginType() {
		return user.get("loginType");
	}
//Ա������
	public static void setMemberName(String MemberName) {
		user.put("MemberName", MemberName);

	}

	public static String getMemberName() {
		return user.get("MemberName");
	}
//Ա������
	public static void setMemberSex(String MemberSex) {
		user.put("MemberSex", MemberSex);

	}

	public static String getMemberSex() {
		return user.get("MemberSex");
	}
//Ա�����
	public static void setMemberIdentify(String MemberIdentify) {
		user.put("MemberIdentify", MemberIdentify);

	}

	public static String getMemberIdentify() {
		return user.get("MemberIdentify");
	}
//Ա��Ȩ��
	public static void setMemberPlace(String MemberPlace) {
		user.put("MemberPlace", MemberPlace);

	}
	public static String getMemberPlace() {
		return user.get("MemberPlace");
	}
}
