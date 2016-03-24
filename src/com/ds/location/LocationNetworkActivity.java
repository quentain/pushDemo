package com.ds.location;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.ds.home.dutyFragment;

/**
 * AMapV2地图中简单介绍混合定位
 */
public class LocationNetworkActivity  implements
		AMapLocationListener, Runnable {
	private static LocationManagerProxy mAMapLocManager = null;
	private TextView myLocation;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private static Handler handler = new Handler();

    public static void  StartNetwork(){
		mAMapLocManager = LocationManagerProxy.getInstance(dutyFragment.context);
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10,(AMapLocationListener) dutyFragment.context);
 }
	
		//stopLocation();// 停止定位


	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * 混合定位回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			this.aMapLocation = location;// 判断超时机制
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			System.out.println(geoLat+geoLng);
		}
	}


	public void run() {
		if (aMapLocation == null) {
			myLocation.setText("12秒内还没有定位成功，停止定位");
			stopLocation();// 销毁掉定位
		}
		handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
	}
}
