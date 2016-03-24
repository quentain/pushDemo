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
	private boolean threadContinue=true;// �߳��Ƿ�������еı�ʶ
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
         //Ψһ���豸ID�� 
		DeviceId=tm.getDeviceId();	
		//�������ͷ���
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


        // Push: ��apikey�ķ�ʽ��¼��һ�������Activity��onCreate�С�
        // �����apikey�����manifest�ļ��У�ֻ��һ�ִ�ŷ�ʽ��
        // ���������Զ��峣����������ʽʵ�֣����滻�����е�Utils.getMetaValue(PushDemoActivity.this,
        // "api_key")
       // ���� �뽫AndroidManifest.xml 128 api_key �ֶ�ֵ�޸�Ϊ�Լ��� api_key ����ʹ�� ����
      //  ���� ATTENTION��You need to modify the value of api_key to your own at row 128 in AndroidManifest.xml to use this Demo !!
        
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(LoginActivityNew.this, "api_key"));
        
        // Push: �������ڵ���λ�����ͣ����Դ�֧�ֵ���λ�õ����͵Ŀ���
        // PushManager.enableLbs(getApplicationContext());

        // Push: �����Զ����֪ͨ��ʽ������API���ܼ��û��ֲᣬ�����ʹ��ϵͳĬ�ϵĿ��Բ������ ����
        // ����֪ͨ���ͽ����У��߼�����->֪ͨ����ʽ->�Զ�����ʽ��ѡ�в�����дֵ��1��
        // ���·������� PushManager.setNotificationBuilder(this, 1, cBuilder)�еĵڶ���������Ӧ
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
        // ���͸߼����ã�֪ͨ����ʽ����Ϊ�����ID
        PushManager.setNotificationBuilder(this, 1, cBuilder);
        InitView();
	}
	private void InitView() {
		loginmain=(LinearLayout)this.findViewById(R.id.loginmain);
		loginmain.setVisibility(View.VISIBLE);
		//ʵ���Զ���¼
		username_edit=(EditText)this.findViewById(R.id.username_edit);//�û���
		username_edit.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
			public void onFocusChange(View v, boolean hasFocus) {
		        EditText et=(EditText)v;
		        if (!hasFocus) {// ʧȥ����
		        	et.setHint(et.getTag().toString());	
		        	UserName=username_edit.getText().toString();
		        } else {					        	
		        	 String hint=et.getHint().toString();
			            et.setTag(hint);//����Ԥ����
			            et.setHint(null);				          
		        }
		    }
		});	
		department=(Spinner)this.findViewById(R.id.department);//����
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
				threadContinue=false;//�����߳�
				dialog.dismiss();
				WriteLog.write("��¼�ɹ�");
				// ����Main Activity
				Intent intent2=new Intent(LoginActivityNew.this,MainActivity.class);
				startActivity(intent2);
				finish();
				Toast.makeText(LoginActivityNew.this, "��¼�ɹ�", Toast.LENGTH_LONG).show();
				// ������Activity
				finish();				
				break;
			case Util.DOWNLOAD_DISCONNECT:
				dialog.dismiss();
				Toast.makeText(LoginActivityNew.this, "��¼ʧ��", Toast.LENGTH_LONG).show();
				break;
			case Util.DOWNLOAD_ERROR:
				dialog.dismiss();
				Toast.makeText(LoginActivityNew.this, "��¼ʧ��", Toast.LENGTH_LONG).show();
				//�ڵ�¼ʧ�ܵ�ʱ����Ҫ��ʾ��¼ʧ��ԭ�������				
				break;
			}
			super.handleMessage(msg);
		}
	};
	/**
	 * ��ʼ��¼������
	 * **/
	private void startLogin() {
		loginThread t = new loginThread();
		dialog =new CustomProgressDialog(this, "���ڵ�¼������",R.anim.frame);
		dialog.show();
		// ������¼�߳�
		t.start();
	}
	@SuppressWarnings("unchecked")
	public class loginThread extends Thread {
		/**
		 * ��ʼ��½
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
			if (!Util.sendtime.equals("nouser")) {//���践�ص��û�����Ϊ�գ���ʾ�Ѿ��з��ص�������
			    sendMessage(Util.DOWNLOAD_OK, "");
			    Util.DeviceId=DeviceId;
			    NewUserData.setUserid(username_edit.getText().toString());
			    NewUserData.setDeviceId(DeviceId);
			    NewUserData.setSendTime(Util.sendtime);
			    Util.UserNAME=UserName;
			    //����
			    Util.SaveUserDataToLocal(LoginActivityNew.this,UserName, Deparment,DeviceId,Util.sendtime);
			} else 			
				sendMessage(Util.DOWNLOAD_ERROR, "");
		}
	}
	
	
	/**
	 * ������Ϣ(���߳�),�����������������Thread�ﴦ����ܻ����
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
					// ִ������У��
					if (validate())  //��
					{
						startLogin();
					}	
					break;				
				}
			}
			return true;
		}
	};

	// ���û�������û������������У��
	private boolean validate()
	{
		String username = username_edit.getText().toString().trim();
		if (username.equals(""))
		{
			Toast.makeText(LoginActivityNew.this,"�������û�����", Toast.LENGTH_LONG).show();
			return false;
		}
		if (Deparment.equals("��ѡ��:"))
		{
			Toast.makeText(LoginActivityNew.this,"��ѡ����", Toast.LENGTH_LONG).show();
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


    // ��apikey�ķ�ʽ��
    private void initWithApiKey() {
        // Push: ���˺ų�ʼ������api key��
        // checkApikey();
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(LoginActivityNew.this, "api_key"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "����").setIcon(
                android.R.drawable.ic_menu_info_details);

        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "����").setIcon(
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

    // ����
    private void showAbout() {
        Dialog alertDialog = new AlertDialog.Builder(LoginActivityNew.this)
                .setTitle("����").setMessage(R.string.text_about)
                .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }

                }).create();
        alertDialog.show();
    }

    // ����
    private void showHelp() {
        Dialog alertDialog = new AlertDialog.Builder(LoginActivityNew.this)
                .setTitle("����").setMessage(R.string.text_help)
                .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

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
            // Push: �ٶ��˺ų�ʼ������access token��
            String accessToken = intent
                    .getStringExtra(Utils.EXTRA_ACCESS_TOKEN);
            PushManager.startWork(getApplicationContext(),
                    PushConstants.LOGIN_TYPE_ACCESS_TOKEN, accessToken);
            //initButton.setText("�����ٶ��˺�");
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

    // ���½�����ʾ����
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
	