package com.task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import ser.ds.util.NetTool;
import ser.ds.util.Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.push.example.R;
import com.ds.home.MainActivity;
import com.ds.home.dutyFragment;
import com.ds.home.scheduleFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 发现Fragment的界面
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 * @author zhaoyingqun
 */
public class ScheduleDetailActivity extends FragmentActivity {
	private Dialog builder3;
	private Button dl3_bt1, dl3_bt2,schedule_detail_back;
	private Spinner d_name;
	private String DBname="",id,ReplaceId;
	private String StartTime="",EndTime="";
	private SimpleAdapter simperAdapter;
	public static int flag=0;
	private int posion=0;
	private TextView x_starttime, x_endtime,x_worklog,x_checkstate,x_reason,x_dwork,x_nodwork;// 起始时间，截止时间
	private boolean threadContinue=true,threadContinuetwo=true,threadContinuethree=true,threadContinuefour=true;
	public static List<HashMap<String, Object>> dingbanList=new ArrayList<HashMap<String,Object>>();
	/**为Fragment加载布局时调用 **/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_detail);
		//posion=Util.Position;
		InitView();
	}
	/**
	 * 初始化视图
	 * */
	private void InitView() {
		schedule_detail_back=(Button)this.findViewById(R.id.schedule_detail_back);
		schedule_detail_back.setOnClickListener(mBtnClick);
//		String time=scheduleFragment.schedulList.get(position).get("starttime").toString();
//        System.out.println(time);
				((TextView) this.findViewById(R.id.x_starttime))
		.setText(scheduleFragment.schedulList.get(Util.Position).get("starttime").toString());
				
		StartTime=scheduleFragment.schedulList.get(Util.Position).get("starttime").toString();
		
		((TextView) this.findViewById(R.id.x_endtime))
		.setText(scheduleFragment.schedulList.get(Util.Position).get("endtime").toString());
		
		EndTime=scheduleFragment.schedulList.get(Util.Position).get("endtime").toString();
		
       ((TextView) this.findViewById(R.id.x_worklog))
		.setText(scheduleFragment.schedulList.get(Util.Position).get("workloc").toString());

        ((TextView) this.findViewById(R.id.x_checkstate))
		.setText(scheduleFragment.schedulList.get(Util.Position).get("idchecked").toString());
        x_nodwork=(TextView)this.findViewById(R.id.x_nodwork);
        x_nodwork.setOnClickListener(mBtnClick);//拒绝顶班
        x_dwork=(TextView)this.findViewById(R.id.x_dwork);
        x_dwork.setOnClickListener(mBtnClick);//申请顶班  
//        ((TextView) this.findViewById(R.id.x_reason))
//		.setText(scheduleFragment.schedulList.get(position).get("reason").toString());
        ((TextView) this.findViewById(R.id.x_reason)).setText("无");
        id=scheduleFragment.schedulList.get(Util.Position).get("ID").toString();
	   //顶班设置中的信息
	   if(scheduleFragment.schedulList.get(Util.Position).get("IsAgree").equals("0")){
		    x_dwork.setVisibility(View.VISIBLE);//正常上班
		    
       }else if(scheduleFragment.schedulList.get(Util.Position).get("IsAgree").equals("1")){
           if(!scheduleFragment.schedulList.get(Util.Position).get("Replace").equals(dutyFragment.deviceid)){
        	   x_dwork.setVisibility(View.GONE);//顶班申请中
           }else{
        	   x_dwork.setVisibility(View.VISIBLE);
        	   x_dwork.setText("同意申请");//申请待接受
        	   x_nodwork.setVisibility(View.VISIBLE);
        	   x_nodwork.setText("拒绝申请");//申请待接受
           }
       }else{
           if(scheduleFragment.schedulList.get(Util.Position).get("Replace").equals(dutyFragment.deviceid)){
        	   x_dwork.setVisibility(View.GONE);//申请成功
           }else{
        	   x_dwork.setVisibility(View.GONE);//顶班接受
           }
       }
        dingbanList.clear();
        GetDName();//得到顶班人的姓名
	}
	private  Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Util.DOWNLOAD_START:
				threadContinuethree=true;
				threadContinue=true;
				threadContinuetwo=true;
				threadContinuefour=true;
				break;
			case Util.DOWNLOAD_OK:
				threadContinue=false;//结束线程
				//Toast.makeText(ScheduleDetailActivity.this, "查询顶班人员成功", Toast.LENGTH_LONG).show();	
				break;
			case Util.DOWNLOAD_twoOK:
				threadContinuetwo=false;//结束线程
				Toast.makeText(ScheduleDetailActivity.this, "申请顶班成功", Toast.LENGTH_LONG).show();	
//				flag=1;
//				MainActivity.flagposition=1;
				finish();
				break;
			case Util.DOWNLOAD_threeOK:
				threadContinuethree=false;//结束线程
				Toast.makeText(ScheduleDetailActivity.this, "操作成功", Toast.LENGTH_LONG).show();	
				finish();
				break;
			case Util.DOWNLOAD_fourOK:
				threadContinuefour=false;//结束线程
				Toast.makeText(ScheduleDetailActivity.this, "您已经拒绝接受申请", Toast.LENGTH_LONG).show();	
				finish();
				break;
			case Util.DOWNLOAD_DISCONNECT:
				Toast.makeText(ScheduleDetailActivity.this, "数据加载失败", Toast.LENGTH_LONG).show();
				break;
			case Util.DOWNLOAD_ERROR:
				Toast.makeText(ScheduleDetailActivity.this, "无数据", Toast.LENGTH_LONG).show();
				//在登录失败的时候需要显示登录失误原因的提醒				
				break;
			}
			super.handleMessage(msg);
		}

	};
	private void SetDataUI(){
		simperAdapter = new SimpleAdapter(ScheduleDetailActivity.this, dingbanList,		
				R.layout.dingbanname_layout_listitem, new String[] {"staffname"},
				new int[] { R.id.dingbanname});		
		d_name.setAdapter(simperAdapter);
	}
	OnClickListener mBtnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.x_dwork:
				if(x_dwork.getText().toString().equals("同意申请")){
					GetAgreeInfo();
				}else{
					// 页面跳转
					LayoutInflater inflater = ScheduleDetailActivity.this
							.getLayoutInflater();
					View linearlayout = inflater.inflate(R.layout.dengban, null);
					
					d_name = (Spinner) linearlayout.findViewById(R.id.d_name);//顶班人员名字
					d_name.setOnItemSelectedListener(mItemSelectedopenpoint);
					dl3_bt1 = (Button) linearlayout
							.findViewById(R.id.dl3_bt1);// 确定
					dl3_bt2 = (Button) linearlayout
							.findViewById(R.id.dl3_bt2);// 取消
					
					dl3_bt1.setOnClickListener(mBtnClick);// 确定
					dl3_bt2.setOnClickListener(mBtnClick);// 取消
					SetDataUI();
					builder3 = new AlertDialog.Builder(ScheduleDetailActivity.this)
							.setTitle("顶班信息")
							.setView(linearlayout).show();
				}
				break;
			case R.id.dl3_bt1:// 确定
				SendInfo();
				dingbanList.clear();
				builder3.dismiss();
				//给服务传顶班人员信息
				break;
			case R.id.dl3_bt2:// 取消
				builder3.dismiss();
				break;
			case R.id.schedule_detail_back:// 取消
				dingbanList.clear();
				finish();
				break;
			case R.id.x_nodwork:// 拒绝接受申请
				DisAgreeInfo();
				break;
			}
		}
	};
private void DisAgreeInfo() {
		
	DisAgreeInfoThread t = new DisAgreeInfoThread();
		// 启动登录线程
		t.start();
	}
	@SuppressWarnings("unchecked")
	public class DisAgreeInfoThread extends Thread {
		/**
		 * 开始查询排班信息
		 * */
		@Override
		public void run() {
			sendMessage(Util.DOWNLOAD_START, "");
			long time = System.currentTimeMillis();			
			try
			{
		        NameValuePair NameValuePair1  = new BasicNameValuePair("i",scheduleFragment.schedulList.get(Util.Position).get("ID").toString());
		        NameValuePair NameValuePair2  = new BasicNameValuePair("d",scheduleFragment.schedulList.get(Util.Position).get("Replace").toString());
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(NameValuePair1);
		        params.add(NameValuePair2);  
		        String urlName = Util.getServerAddress(ScheduleDetailActivity.this)+ "DisagreeShiftsReplace?" + URLEncodedUtils.format(params, "UTF-8");
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
			if (!threadContinuefour) {
				this.interrupt();
				return;
			}
			if (Util.Result.equals("success")) {//假设返回的用户名不为空，表示已经有返回的数据了
				//保存用户名、设备Id，部门信息
			    sendMessage(Util.DOWNLOAD_fourOK, "");
			} else 			
				sendMessage(Util.DOWNLOAD_ERROR, "");
		}
	}
	
	private void GetDName() {
		GetDNameThread t = new GetDNameThread();
		// 启动登录线程
		t.start();
	}
	@SuppressWarnings("unchecked")
	public class GetDNameThread extends Thread {
		/**
		 * 开始查询排班信息
		 * */
		@Override
		public void run() {
			sendMessage(Util.DOWNLOAD_START, "");
			long time = System.currentTimeMillis();			
			try
			{
		        NameValuePair NameValuePair1  = new BasicNameValuePair("d",Util.DeviceId);
		        NameValuePair NameValuePair2  = new BasicNameValuePair("s",StartTime);
		        NameValuePair NameValuePair3  = new BasicNameValuePair("e",EndTime);
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(NameValuePair1);
		        params.add(NameValuePair2);  
		        params.add(NameValuePair3);  
		        String urlName = Util.getServerAddress(ScheduleDetailActivity.this)+ "GetStaffList?" + URLEncodedUtils.format(params, "UTF-8");
                String result = NetTool.sendTxt(urlName,"", "UTF-8");
                com.alibaba.fastjson.JSONArray json = JSONArray.parseArray(result);	//这是一个数组。我这只有第一个。你可以循环的明白了
                   if(json.size()>0){
                	   for (int i = 0; i < json.size(); i++) {
           				JSONObject obj = (JSONObject)json.get(i);
           			 HashMap<String, Object> map =new HashMap<String, Object>();
            				map.put("deviceid", obj.get("_deviceId"));
            				map.put("staffname", obj.get("_staffName"));
            				dingbanList.add(map);
           		         }   
                 }
         
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
			if (dingbanList.size()>0) {//假设返回的用户名不为空，表示已经有返回的数据了
				//保存用户名、设备Id，部门信息
			    sendMessage(Util.DOWNLOAD_OK, "");
			} else 			
				sendMessage(Util.DOWNLOAD_ERROR, "");
		}
	}
	private void GetAgreeInfo() {
		GetAgreeInfoThread t = new GetAgreeInfoThread();
		// 启动登录线程
		t.start();
	}
	@SuppressWarnings("unchecked")
	public class GetAgreeInfoThread extends Thread {
		/**
		 * 开始查询排班信息
		 * */
		@Override
		public void run() {
			sendMessage(Util.DOWNLOAD_START, "");
			long time = System.currentTimeMillis();			
			try
			{
		        NameValuePair NameValuePair1  = new BasicNameValuePair("i",scheduleFragment.schedulList.get(Util.Position).get("ID").toString());
		        NameValuePair NameValuePair2  = new BasicNameValuePair("d",scheduleFragment.schedulList.get(Util.Position).get("Replace").toString());
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(NameValuePair1);
		        params.add(NameValuePair2);  
		        String urlName = Util.getServerAddress(ScheduleDetailActivity.this)+ "AgreeShiftsReplace?" + URLEncodedUtils.format(params, "UTF-8");
                String result = NetTool.sendTxt(urlName,"", "UTF-8");
                com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(result);
    			Util.Result=jsonObj.getString("Result");
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
			if (!threadContinuethree) {
				this.interrupt();
				return;
			}
			if (Util.Result.equals("success")) {//假设返回的用户名不为空，表示已经有返回的数据了
				//保存用户名、设备Id，部门信息
			    sendMessage(Util.DOWNLOAD_threeOK, "");
			} else 			
				sendMessage(Util.DOWNLOAD_ERROR, "");
		}
	}
	private void SendInfo() {
		
		SendInfoThread t = new SendInfoThread();
		// 启动登录线程
		t.start();
	}
	@SuppressWarnings("unchecked")
	public class SendInfoThread extends Thread {
		/**
		 * 开始查询排班信息
		 * */
		@Override
		public void run() {
			sendMessage(Util.DOWNLOAD_START, "");
			long time = System.currentTimeMillis();			
			try
			{
		        NameValuePair NameValuePair1  = new BasicNameValuePair("i",id);
		        NameValuePair NameValuePair2  = new BasicNameValuePair("d",ReplaceId);
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(NameValuePair1);
		        params.add(NameValuePair2);  
		        String urlName = Util.getServerAddress(ScheduleDetailActivity.this)+ "SetShiftsReplace?" + URLEncodedUtils.format(params, "UTF-8");
                String result = NetTool.sendTxt(urlName,"", "UTF-8");
               // com.alibaba.fastjson.JSONArray json = JSONArray.parseArray(result);	//这是一个数组。我这只有第一个。你可以循环的明白了
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
			if (Util.Result.equals("success")) {//假设返回的用户名不为空，表示已经有返回的数据了
				//保存用户名、设备Id，部门信息
			    sendMessage(Util.DOWNLOAD_twoOK, "");
			} else 			
				sendMessage(Util.DOWNLOAD_ERROR, "");
		}
	}
	
	private void sendMessage(int what, String text)

	{
		Message m = new Message();
		m.what = what;
		Bundle data = new Bundle();
		data.putString("text", text);
		m.setData(data);
		handler.sendMessage(m);
	}	
	private OnItemSelectedListener mItemSelectedopenpoint=new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {	
			ReplaceId=dingbanList.get(position).get("deviceid").toString();	
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}			
	};	
}
