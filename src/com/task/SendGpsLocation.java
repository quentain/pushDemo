package com.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.baidu.push.example.R;
import com.ds.lib.LoadingDialog;
import com.ds.login.LoginActivityNew;
import com.ds.service.RunTest;

import ser.ds.util.NetTool;
import ser.ds.util.Util;
import ser.ds.util.WriteLog;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class SendGpsLocation extends Activity implements AMapLocationListener{
	private   Timer timer;
	private   int pushtime;
	private   String g_latLongString;
	private static   boolean threadContinuetwo=true;
	private LocationManagerProxy mAMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	public   Handler HandlerListen=null;
	
	  public   void GpsLocation(){
		  if(!Util.sendtime.equals("")){
				pushtime=Integer.parseInt(Util.sendtime)*60000;
			}
		    TimerTask task = new TimerTask() {// 任务
				@Override
				public void run() {
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				}
			};		
			//Timer和TimerTask两个类结合使用，可以实现执行一次定时任务，或者定期重复执行任务
			timer = new Timer(true);
			timer.schedule(task, 0, pushtime); // 延时5000ms后执行，5000ms执行一次		
			
			//声明LocationManager对象
	        LocationManager loctionManager;
	        String contextService=Context.LOCATION_SERVICE;
	        //通过系统服务，取得LocationManager对象
	        loctionManager=(LocationManager)this.getSystemService(contextService);
	             
	        //使用标准集合，让系统自动选择可用的最佳位置提供器，提供位置
	          //Criteria精确度        
	        Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度位置解析的精度，高或低，参数： Criteria. ACCURACY_FINE，精确模式； 
	        criteria.setAltitudeRequired(false);//不要求海拔
	        criteria.setBearingRequired(false);//不要求方位
	        criteria.setCostAllowed(true);//允许有花费
	        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
	        
	        //从可用的位置提供器中，匹配以上标准的最佳提供器
	        String provider = loctionManager.getBestProvider(criteria, true);
	        
	        //获得最后一次变化的位置
	        Location location = loctionManager.getLastKnownLocation(provider);
	       
	        //显示在TextView中
	        updateWithNewLocation(location);       	           
	        //监听位置变化，5秒一次，距离10米以上
	        loctionManager.requestLocationUpdates(provider, pushtime, 2, locationListener);

			handler = new Handler() {// 委托
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 1:
						try {
							Toast.makeText(SendGpsLocation.this, "上报地理位置成功！", Toast.LENGTH_LONG).show();
							SendLocationInfo();
				            WriteLog.write(pushtime+"秒定时发送的位置分别是\n"+g_latLongString);	
						} catch (Exception e) {
							Toast.makeText(SendGpsLocation.this.getApplicationContext(), e.toString(),
									Toast.LENGTH_SHORT).show();
						}
						break;
					}
				}
			};
			
		}
	//位置监听器
	    private  final LocationListener locationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
			@Override
			public void onProviderEnabled(String provider) {
			}
			@Override
			public void onProviderDisabled(String provider) {
				updateWithNewLocation(null);
			}
			//当位置变化时触发
			@Override
			public void onLocationChanged(Location location) {
				//使用新的location更新TextView显示
				updateWithNewLocation(location);
			}
		};  
	// 将位置信息显示在TextView中
		private void updateWithNewLocation(Location location) {
			String latLongString;
			TextView myLoctionText;
			//myLoctionText = (TextView)this.findViewById(R.id.top);
			if (location != null) {
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				latLongString = "Lat(纬度): " + lat + "\nLong(经度): " + lng;
				Util.lat=String.valueOf(lat);
				Util.lng=String.valueOf(lng);
				g_latLongString = latLongString;
				//myLoctionText.setText(g_latLongString);
			} else {
				latLongString = "没找到位置";
			}
			//myLoctionText.setText( "您当前的位置是:\n" + latLongString);
		}
		
		 private static  Handler handler = new Handler() {	
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case Util.DOWNLOAD_START:
						threadContinuetwo=true;
						break;				
					case Util.DOWNLOAD_twoOK:
						threadContinuetwo=false;
						break;
					
					case Util.DOWNLOAD_DISCONNECT:
						break;
					case Util.DOWNLOAD_ERROR:
						//在登录失败的时候需要显示登录失误原因的提醒				
						break;
					}
					super.handleMessage(msg);
				}

			};
			
			public  void SendLocationInfo(){
				Timer timer=new Timer();
			      //五秒后调用RunTest()这个类，并执行run()方法
			    timer.schedule(new RunTest(),pushtime);
				//开启一个发送位置线程
				sendlocationThread t = new sendlocationThread();
				//progressdialog.SetMessage("正在上报位置信息,请稍等.....");
				// 启动上报位置信息线程
				t.start();		
			}
			@SuppressWarnings("unchecked")
			public  class sendlocationThread extends Thread {		
				/**
				 * 开始上报
				 * */
				@Override
				public void run() {
					sendMessage(Util.DOWNLOAD_START, "");
					long time = System.currentTimeMillis();						
					try{
						
				        NameValuePair NameValuePair1  = new BasicNameValuePair("d",Util.DeviceId);
				        NameValuePair NameValuePair2  = new BasicNameValuePair("l",Util.lng);
				        NameValuePair NameValuePair3  = new BasicNameValuePair("t",Util.lat);
				        List<NameValuePair> params = new ArrayList<NameValuePair>();
				        params.add(NameValuePair1);
				        params.add(NameValuePair2);  
				        params.add(NameValuePair3);  
				        String urlName = Util.getServerAddress(SendGpsLocation.this)+ "PostLocation?" + URLEncodedUtils.format(params, "UTF-8");
		                String result = NetTool.sendTxt(urlName,"", "UTF-8");

		                com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(result);
		                Util.Result=jsonObj.getString("Result");
		    			Util.CurrentMessage=jsonObj.getString("Message");
					}
					catch (Exception e)
					{				
						e.printStackTrace();
					}
				
					if ((System.currentTimeMillis() - time) < 1000) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (!threadContinuetwo) {
						this.interrupt();
						return;
					}
					//判断返回是否有数据
					if ("success".equals(Util.Result)) {			
						sendMessage(Util.DOWNLOAD_twoOK, "");
						//Toast.makeText(getActivity(), "地理位置上报成功！", Toast.LENGTH_LONG).show();
					} else 			
					    sendMessage(Util.DOWNLOAD_ERROR, "");
				}
			}
			/**
			 * 返回按键监听主界面直接退出软件
			 * */
		//
			private  void sendMessage(int what, String text)
			{
				Message m = new Message();
				m.what = what;
				Bundle data = new Bundle();
				data.putString("text", text);
				m.setData(data);
				handler.sendMessage(m);
			}
			@Override
			public void onLocationChanged(Location arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onLocationChanged(AMapLocation location) {
				if (location != null) {
					this.aMapLocation = location;//此为定位的线程
					Double geoLat = location.getLatitude();
					Double geoLng = location.getLongitude();
					//Toast.makeText(getActivity(), "定位方式："+location.getProvider(), 2000).show();
					if(geoLat!=0.0&&geoLng!=0.0)
					{
						destoryLocation();
					}
					Util.infoupload.setLatitude(geoLat);
					Util.infoupload.setLongitude(geoLng);
					//top.setText("辛苦您已经执勤：\n经度"+String.valueOf(Util.infoupload.getLongitude())+"   纬度"+String.valueOf(Util.infoupload.getLatitude())+"\n设备ID是   "+LoginActivityNew.DeviceId);
					//定位成功以后把自己的数据显示到控件上					
					//WriteLog.write("辛苦您已经执勤：\n经度"+String.valueOf(Util.infoupload.getLongitude())+"   纬度"+String.valueOf(Util.infoupload.getLatitude())+"\n设备ID是   "+LoginActivityNew.DeviceId);
					//上报位置信息
					//SendLocationInfo();
				}	
			}	

			/**
			 * 销毁定位
			 */
			private void destoryLocation() {
				if (mAMapLocManager != null) {
					mAMapLocManager.removeUpdates((AMapLocationListener)this);
					mAMapLocManager.destory();
				}
				mAMapLocManager = null;
			}
			private void StartLocation() {
				mAMapLocManager = LocationManagerProxy.getInstance(this);
				/*
				 * mAMapLocManager.setGpsEnable(false);//
				 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
				 * API定位采用GPS和网络混合定位方式
				 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
				 */
				mAMapLocManager.requestLocationUpdates(
						LocationProviderProxy.AMapNetwork, 5000, 10, this);
			}
}
