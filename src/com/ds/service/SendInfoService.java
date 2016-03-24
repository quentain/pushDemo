
package com.ds.service;

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
import com.baidu.push.example.R;
import com.ds.home.dutyFragment.sendlocationThread;
import com.ds.login.LoginActivityNew;

import ser.ds.util.NetTool;
import ser.ds.util.Util;
import ser.ds.util.WriteLog;
import ser.ds.util.WriteServiceLog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SendInfoService extends Service {
	private static final String TAG = "MyService";
	private int pushtime;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private  boolean threadContinuetwo=true;
	private  Timer timer;
	private   LocationManager loctionManager;
	private LocationManagerProxy mAMapLocManager = null;
	// 必须实现的方法
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		//Toast.makeText(this, "接入服务的开始", Toast.LENGTH_SHORT).show();
		WriteServiceLog.write("进入onCreate服务准备发送定位信息");
	}

	@Override
	public void onDestroy() {
		threadContinuetwo=false;
		loctionManager.removeUpdates(locationListener);
		Log.v(TAG, "onDestroy");
		Toast.makeText(this, "服务停止发送信息", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "onStart");
		//Toast.makeText(this, "服务开始检测intent信息", Toast.LENGTH_SHORT).show();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				int op = bundle.getInt("op");
				switch (op) {
				 case 1:
					 GpsLocation();	
					 WriteServiceLog.write("服务开始执行定位信息！");
					break;
				}

			}
		}

	}
	 public  void GpsLocation(){
		  SharedPreferences sharedPreferences = getSharedPreferences("com.defenceservice", Context.MODE_PRIVATE);
		     //getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
			 String sendtime = sharedPreferences.getString("sendtime","");
		 if(!sendtime.equals("")){
				pushtime=Integer.parseInt(sendtime)*60000;
			}else{
				pushtime=30000;
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
			timer.schedule(task, 0, pushtime); // 延时5000ms后执行，5000ms执行一次	pushtime	
			
			//声明LocationManager对象
	      
	        String contextService=Context.LOCATION_SERVICE;
	        //通过系统服务，取得LocationManager对象
	        loctionManager=(LocationManager)this. getSystemService(contextService);
	             
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
	        //监听位置变化，5秒一次，距离10米以上
	        loctionManager.requestLocationUpdates(provider, pushtime, 2, locationListener);

			handler = new Handler() {// 委托
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 1:
						try {
						 SendLocationInfo();
						 WriteServiceLog.write("服务开始发送数据了！");
						 Toast.makeText(SendInfoService.this, "服务中时间到了发送数据的时间了！", Toast.LENGTH_LONG).show();
						} catch (Exception e) {
						}
						break;
					}
				}
			};
		}
	 private  Handler handler = new Handler() {	
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Util.DOWNLOAD_START:
					threadContinuetwo=true;
					break;				
				case Util.DOWNLOAD_twoOK:
					threadContinuetwo=false;
					//progressdialog.dismiss();
					Toast.makeText(SendInfoService.this, "地理位置上报成功！", Toast.LENGTH_LONG).show();
					break;
				case Util.DOWNLOAD_DISCONNECT:
					Toast.makeText(SendInfoService.this, Util.CurrentMessage, Toast.LENGTH_LONG).show();
					break;
				case Util.DOWNLOAD_ERROR:
					Toast.makeText(SendInfoService.this, Util.CurrentMessage, Toast.LENGTH_LONG).show();
					//在登录失败的时候需要显示登录失误原因的提醒				
					break;
				}
				super.handleMessage(msg);
			}

		};
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
			if (location != null) {
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				latLongString = "Lat(纬度): " + lat + "\nLong(经度): " + lng;
				Util.lat=String.valueOf(lat);
				Util.lng=String.valueOf(lng);
			} else {
				latLongString = "没找到位置";
			}
		}
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
			        String urlName = Util.getServerAddress(SendInfoService.this)+ "PostLocation?" + URLEncodedUtils.format(params, "UTF-8");
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
				} else 			
				    sendMessage(Util.DOWNLOAD_ERROR, "");
			}
		}
		
		private  void sendMessage(int what, String text)
		{
			Message m = new Message();
			m.what = what;
			Bundle data = new Bundle();
			data.putString("text", text);
			m.setData(data);
			handler.sendMessage(m);
		}
}
