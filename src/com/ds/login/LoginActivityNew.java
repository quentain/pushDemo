package com.ds.login;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import ser.ds.util.NetTool;
import ser.ds.util.Util;
import ser.ds.util.WriteLog;

import com.alibaba.fastjson.JSON;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.push.example.R;
import com.baidu.push.example.Utils;
import com.ds.PagerSlidingTabStrip;
import com.ds.home.MainActivity;
import com.ds.home.MainActivity.MyPagerAdapter;
import com.ds.lib.CustomProgressDialog;
import com.ds.lib.LoadingDialog;
import com.ds.model.NewUserData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class LoginActivityNew extends Activity implements View.OnClickListener {
	private LinearLayout loginmain;
	private EditText username_edit;
	private Spinner department;
	private LinearLayout login_button;	
	private CustomProgressDialog dialog;
	private boolean threadContinue=true;// 线程是否继续运行的标识
	private String Deparment;
	public static String DeviceId="";
	private String UserName="";
    private int akBtnId = 0;
    TextView logText = null;
    ScrollView scrollView = null;
    public static int initialCnt = 0;
	private static final String TAG = LoginActivityNew.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 			
         //唯一的设备ID： 
		DeviceId=tm.getDeviceId();	
		//连接推送服务
		// checkApikey();
        Utils.logStringCache = Utils.getLogText(getApplicationContext());

        Resources resource = this.getResources();
        String pkgName = this.getPackageName();

        setContentView(resource.getIdentifier("login", "layout", pkgName));
        akBtnId = resource.getIdentifier("btn_initAK", "id", pkgName);

        logText = (TextView) findViewById(resource.getIdentifier("text_log",
                "id", pkgName));
        scrollView = (ScrollView) findViewById(resource.getIdentifier(
                "stroll_text", "id", pkgName));


        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
        // "api_key")
       // ！！ 请将AndroidManifest.xml 128 api_key 字段值修改为自己的 api_key 方可使用 ！！
      //  ！！ ATTENTION：You need to modify the value of api_key to your own at row 128 in AndroidManifest.xml to use this Demo !!
        
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(LoginActivityNew.this, "api_key"));
        
        // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
        // PushManager.enableLbs(getApplicationContext());

        // Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段 代码
        // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
        // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                resource.getIdentifier(
                        "notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier(
                "simple_notification_icon", "drawable", pkgName));
        cBuilder.setNotificationSound(Uri.withAppendedPath(
                Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
        // 推送高级设置，通知栏样式设置为下面的ID
        PushManager.setNotificationBuilder(this, 1, cBuilder);
        InitView();
	}
	private void InitView() {
		loginmain=(LinearLayout)this.findViewById(R.id.loginmain);
		loginmain.setVisibility(View.VISIBLE);
		//实现自动登录
		username_edit=(EditText)this.findViewById(R.id.username_edit);//用户名
		username_edit.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
			public void onFocusChange(View v, boolean hasFocus) {
		        EditText et=(EditText)v;
		        if (!hasFocus) {// 失去焦点
		        	et.setHint(et.getTag().toString());	
		        	UserName=username_edit.getText().toString();
		        } else {					        	
		        	 String hint=et.getHint().toString();
			            et.setTag(hint);//保存预设字
			            et.setHint(null);				          
		        }
		    }
		});	
		department=(Spinner)this.findViewById(R.id.department);//部门
		department.setOnItemSelectedListener(mItemSelectedopenpoint);
		login_button=(LinearLayout)this.findViewById(R.id.login_button);
		login_button.setOnTouchListener(mTouch);	
	}
	private  Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Util.DOWNLOAD_START:
				threadContinue=true;
				dialog.show();
				break;
			case Util.DOWNLOAD_OK:
				threadContinue=false;//结束线程
				dialog.dismiss();
				WriteLog.write("登录成功");
				// 启动Main Activity
				Intent intent2=new Intent(LoginActivityNew.this,MainActivity.class);
				startActivity(intent2);
				finish();
				Toast.makeText(LoginActivityNew.this, "登录成功", Toast.LENGTH_LONG).show();
				// 结束该Activity
				finish();				
				break;
			case Util.DOWNLOAD_DISCONNECT:
				dialog.dismiss();
				Toast.makeText(LoginActivityNew.this, "登录失败", Toast.LENGTH_LONG).show();
				break;
			case Util.DOWNLOAD_ERROR:
				dialog.dismiss();
				Toast.makeText(LoginActivityNew.this, "登录失败", Toast.LENGTH_LONG).show();
				//在登录失败的时候需要显示登录失误原因的提醒				
				break;
			}
			super.handleMessage(msg);
		}
	};
	/**
	 * 开始登录服务器
	 * **/
	private void startLogin() {
		loginThread t = new loginThread();
		dialog =new CustomProgressDialog(this, "正在登录···",R.anim.frame);
		dialog.show();
		// 启动登录线程
		t.start();
	}
	@SuppressWarnings("unchecked")
	public class loginThread extends Thread {
		/**
		 * 开始登陆
		 * */
		@Override
		public void run() {
			UserName=username_edit.getText().toString();
			sendMessage(Util.DOWNLOAD_START, "");
			long time = System.currentTimeMillis();			
			try
			{
				NameValuePair NameValuePair1  = new BasicNameValuePair("n",UserName);
		        NameValuePair NameValuePair2  = new BasicNameValuePair("d",DeviceId);
		        NameValuePair NameValuePair3  = new BasicNameValuePair("u",Util.puserID);
		        NameValuePair NameValuePair4  = new BasicNameValuePair("c",Util.pchannelID);
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(NameValuePair1);
		        params.add(NameValuePair2); 
		        params.add(NameValuePair3); 
		        params.add(NameValuePair4); 
		        String urlName = Util.getServerAddress(LoginActivityNew.this)+ "Login?" + URLEncodedUtils.format(params, "UTF-8");
                String result = NetTool.sendTxt(urlName,"", "UTF-8");
                com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(result);
    			Util.Result=jsonObj.getString("Result");
    			Util.sendtime=jsonObj.getString("Message");
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
			if (!threadContinue) {
				this.interrupt();
				return;
			}
			if (!Util.sendtime.equals("nouser")) {//假设返回的用户名不为空，表示已经有返回的数据了
			    sendMessage(Util.DOWNLOAD_OK, "");
			    Util.DeviceId=DeviceId;
			    NewUserData.setUserid(username_edit.getText().toString());
			    NewUserData.setDeviceId(DeviceId);
			    NewUserData.setSendTime(Util.sendtime);
			    Util.UserNAME=UserName;
			    //保存
			    Util.SaveUserDataToLocal(LoginActivityNew.this,UserName, Deparment,DeviceId,Util.sendtime);
			} else 			
				sendMessage(Util.DOWNLOAD_ERROR, "");
		}
	}
	
	
	/**
	 * 处理消息(主线程),更新适配器，如果在Thread里处理可能会出错
	 * */
	private OnItemSelectedListener mItemSelectedopenpoint=new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {	
			
			Deparment=department.getSelectedItem().toString();		
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}			
	};	
	private OnTouchListener mTouch=new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				switch(v.getId())
				{
				case R.id.login_button:
					// 执行输入校验
					if (validate())  //①
					{
						startLogin();
					}	
					break;				
				}
			}
			return true;
		}
	};

	// 对用户输入的用户名、密码进行校验
	private boolean validate()
	{
		String username = username_edit.getText().toString().trim();
		if (username.equals(""))
		{
			Toast.makeText(LoginActivityNew.this,"请输入用户名！", Toast.LENGTH_LONG).show();
			return false;
		}
		if (Deparment.equals("请选择:"))
		{
			Toast.makeText(LoginActivityNew.this,"请选择部门", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
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
	@Override
    public void onClick(View v) {
        if (v.getId() == akBtnId) {
            initWithApiKey();
        } else {

        }

    }


    // 以apikey的方式绑定
    private void initWithApiKey() {
        // Push: 无账号初始化，用api key绑定
        // checkApikey();
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(LoginActivityNew.this, "api_key"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "关于").setIcon(
                android.R.drawable.ic_menu_info_details);

        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "帮助").setIcon(
                android.R.drawable.ic_menu_help);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (Menu.FIRST + 1 == item.getItemId()) {
            showAbout();
            return true;
        }
        if (Menu.FIRST + 2 == item.getItemId()) {
            showHelp();
            return true;
        }

        return false;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return true;
    }

    // 关于
    private void showAbout() {
        Dialog alertDialog = new AlertDialog.Builder(LoginActivityNew.this)
                .setTitle("关于").setMessage(R.string.text_about)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }

                }).create();
        alertDialog.show();
    }

    // 帮助
    private void showHelp() {
        Dialog alertDialog = new AlertDialog.Builder(LoginActivityNew.this)
                .setTitle("帮助").setMessage(R.string.text_help)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }

                }).create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        updateDisplay();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();

        if (Utils.ACTION_LOGIN.equals(action)) {
            // Push: 百度账号初始化，用access token绑定
            String accessToken = intent
                    .getStringExtra(Utils.EXTRA_ACCESS_TOKEN);
            PushManager.startWork(getApplicationContext(),
                    PushConstants.LOGIN_TYPE_ACCESS_TOKEN, accessToken);
            //initButton.setText("更换百度账号");
        }

        updateDisplay();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Utils.setLogText(getApplicationContext(), Utils.logStringCache);
        super.onDestroy();
    }

    // 更新界面显示内容
    private void updateDisplay() {
        Log.d(TAG, "updateDisplay, logText:" + logText + " cache: "
                + Utils.logStringCache);
        if (logText != null) {
            logText.setText(Utils.logStringCache);
        }
        if (scrollView != null) {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
	