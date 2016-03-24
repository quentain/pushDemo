package com.ds.home;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import ser.ds.util.NetTool;
import ser.ds.util.Util;
import ser.ds.util.WriteLog;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.push.example.R;
import com.baidu.push.example.Utils;
import com.ds.lib.CustomDialog;
import com.ds.lib.LoadingDialog;
import com.ds.login.LoginActivityNew;
import com.ds.model.NewUserData;
import com.ds.service.RunTest;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.provider.MediaStore.Audio;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 聊天Fragment的界面
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 * @author guolin
 */
public class dutyFragment extends Fragment {
	private View view;
	private TextView welcome;
	private  TextView top;
	private   LocationManager loctionManager;
	private TextView midshow;
	private TextView recordLoctionText;
	private LinearLayout startwork,finishwork;
	private LocationManagerProxy mAMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler stepTimeHandler;
    private Runnable mTicker;
    private long startTime = 0;
    private LoadingDialog progressdialog;// 进度对话框    
    private String content;
    private TextView exit;
	private  Timer timer;
	public   Handler HandlerListen=null;
	private  String g_latLongString;
	private  boolean threadContinuetwo=true;
	private int pushtime;
	public static Context context;
	public static String savename="",sendtime="",deviceid="",department="";
	private int flag=0,buttonflag=0;
	private int akBtnId = 0;
	private TextView logText = null;
	private ScrollView scrollView = null;
    private ImageView infoOperatingIV;
    private Animation operatingAnim;
    private ImageView stopimage,startimage;
    public static boolean animationstate=false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.home, container, false);
		//访问SharedPreferences数据中的实现过程！
	SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.defenceservice", Context.MODE_PRIVATE);
	     //getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		 savename = sharedPreferences.getString("saveuserid","");
		 department = sharedPreferences.getString("savedeparment","");
		 deviceid = sharedPreferences.getString("savedeviceid","");
		 sendtime = sharedPreferences.getString("sendtime","");
		 if(savename.equals("")&&department.equals("")&&deviceid.equals("")&&sendtime.equals("")){
			 flag=1;
			 Intent intent=new Intent(getActivity(),LoginActivityNew.class);
			 startActivity(intent);
			 getActivity().finish();
		 }else{
			 InitView();	
	 		 flag=0;
		 }    
		return view;
	}
	private void InitView() {
		Utils.logStringCache = Utils.getLogText(getActivity().getApplicationContext());
		context=getActivity().getApplicationContext();
		//登陆成功之后得到的发送时间
		if(!sendtime.equals("")){
			pushtime=Integer.parseInt(sendtime)*60000;
		}else{
			pushtime=30000;
		}
		progressdialog = new LoadingDialog(getActivity());
		//recordLoctionText = (TextView)view.findViewById(R.id.recordLoctionText);
		welcome=(TextView)view.findViewById(R.id.welcome);
		exit=(TextView)view.findViewById(R.id.exit);
		stopimage=(ImageView)view.findViewById(R.id.stopimage);
		startimage=(ImageView)view.findViewById(R.id.startimage);
		exit.setOnClickListener(mClick);//近期任务
		if(flag==1){
			welcome.setText("欢迎"+Util.UserNAME+"登录");
		}else{
			welcome.setText("欢迎"+savename+"登录");
		}
		 infoOperatingIV = (ImageView)view.findViewById(R.id.op);
		 operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.tip);  
	     AccelerateDecelerateInterpolator lin = new AccelerateDecelerateInterpolator();  
	     operatingAnim.setInterpolator(lin);  		
		//top=(TextView)view.findViewById(R.id.top);
		midshow=(TextView)view.findViewById(R.id.midshow);//显示执勤之间的地方
		//下面四个按钮分别有点击事件
		startwork=(LinearLayout)view.findViewById(R.id.startwork);
		startwork.setOnClickListener(mClick);//开始执勤
		finishwork=(LinearLayout)view.findViewById(R.id.finishwork);
		finishwork.setOnClickListener(mClick);//结束执勤
	}
	@Override
	public void onResume()
	{
		if(animationstate)
		infoOperatingIV.startAnimation(operatingAnim); 
		super.onResume();
	}
	//按钮的点击监听函数
	private OnClickListener mClick=new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			int op = -1;
			Intent intent = new Intent("com.ds.service.SendInfoService");
			intent.setAction("com.ds.service.SendInfoService");//你定义的service的action
			intent.setPackage("com.baidu.push.example");
			switch(v.getId())
			{
			case R.id.startwork:
				if(animationstate) return;
				animationstate=true;
			    if (animationstate) {  
			        infoOperatingIV.startAnimation(operatingAnim);  
			    } 
				op=1;//传参数设置开启服务
				GpsLocation();//开启gps定位函数
				//开始计时
				StartCountTime();//开始计时
				break;
			case R.id.finishwork://结束执勤
				animationstate=false;
				infoOperatingIV.clearAnimation(); 
				 if(buttonflag==0){
				 getActivity().stopService(intent);
				 threadContinuetwo=false;
				 timer.cancel();//无网络情况的结束
				 stepTimeHandler.removeCallbacks(mTicker);//有网络情况的结束
				 WriteLog.write("您已经点击了结束执勤！执勤时间："+content);
				 buttonflag=1;
				 }else{
					 timer.cancel();//无网络情况的结束
					 stepTimeHandler.removeCallbacks(mTicker);//有网络情况的结束
					 Toast.makeText(getActivity(), "您已经点了结束执勤！", Toast.LENGTH_LONG).show();
					 return;
				 }
				break;
            case R.id.exit:
            	exit();
				break;
			}
			Bundle bundle  = new Bundle();
			bundle.putInt("op", op);
			intent.putExtras(bundle);
			getActivity().startService(intent);
		}		
	};
	 private  Handler handler = new Handler() {	
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Util.DOWNLOAD_START:
					threadContinuetwo=true;
					progressdialog.show();
					break;				
				case Util.DOWNLOAD_twoOK:
					threadContinuetwo=false;
					//progressdialog.dismiss();
					Toast.makeText(getActivity(), "地理位置上报成功！", Toast.LENGTH_LONG).show();
					break;
				
				case Util.DOWNLOAD_DISCONNECT:
					progressdialog.dismiss();
					Toast.makeText(getActivity(), Util.CurrentMessage, Toast.LENGTH_LONG).show();
					break;
				case Util.DOWNLOAD_ERROR:
					progressdialog.dismiss();
					Toast.makeText(getActivity(), Util.CurrentMessage, Toast.LENGTH_LONG).show();
					//在登录失败的时候需要显示登录失误原因的提醒				
					break;
				}
				super.handleMessage(msg);
			}

		};
	
	
  public  void GpsLocation(){
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
        loctionManager=(LocationManager)getActivity(). getSystemService(contextService);
             
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
						//Toast.makeText(getActivity(), "上报地理位置成功！", Toast.LENGTH_LONG).show();
						SendLocationInfo();
			            WriteLog.write(pushtime+"秒定时发送的位置分别是\n"+g_latLongString);	
					} catch (Exception e) {
						Toast.makeText(getActivity().getApplicationContext(), e.toString(),
								Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		};
		
	}
	public void cancel() {
		this.cancel();
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
	private void StartCountTime(){
		//开启线程
		stepTimeHandler = new Handler();
        startTime = System.currentTimeMillis();
        mTicker = new Runnable() {
            @Override
			public void run() {
                content = showTimeCount(System.currentTimeMillis() - startTime);
                midshow.setText(content);
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                stepTimeHandler.postAtTime(mTicker, next);                      
            }
        };
        //启动计时线程，定时更新
         mTicker.run();
	}
	// 将位置信息显示在TextView中
	private void updateWithNewLocation(Location location) {
		String latLongString;
		TextView myLoctionText;
		//myLoctionText = (TextView)getActivity().findViewById(R.id.top);
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
	
	private  void sendMessage(int what, String text)
	{
		Message m = new Message();
		m.what = what;
		Bundle data = new Bundle();
		data.putString("text", text);
		m.setData(data);
		handler.sendMessage(m);
	}
	public void exit() {
		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setMessage("是否退出软件?");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent= new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);							
				android.os.Process.killProcess(Process.myPid());
				getActivity().finish();
			 }
		});
		
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}
	//时间计数器，最多只能到99小时，如需要更大小时数需要改改方法
    private String showTimeCount(long time) {
    	//时间计数器，最多只能到99小时，如需要更大小时数需要改改方法
        if(time >= 360000000){
            return "00:00:00";
        }
        String timeCount = "";
        long hourc = time/3600000;
        String hour = "0" + hourc;
        hour = hour.substring(hour.length()-2, hour.length());

        long minuec = (time-hourc*3600000)/(60000);
        String minue = "0" + minuec;
        minue = minue.substring(minue.length()-2, minue.length());

        long secc = (time-hourc*3600000-minuec*60000)/1000;
        String sec = "0" + secc;
        sec = sec.substring(sec.length()-2, sec.length());
        timeCount = hour + ":" + minue + ":" + sec;
        return timeCount;
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
		        String urlName = Util.getServerAddress(getActivity())+ "PostLocation?" + URLEncodedUtils.format(params, "UTF-8");
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
}
