package com.ds.home;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import ser.ds.util.NetTool;
import ser.ds.util.Util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.push.example.R;
import com.ds.lib.CustomProgressDialog;
import com.ds.lib.ListViewOnNet;
import com.ds.lib.ListViewOnNet.OnRefreshLoadingMoreListener;
import com.task.ScheduleDetailActivity;
import com.task.TaskListAdapter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 发现Fragment的界面
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 * @author guolin
 */
public class scheduleFragment extends Fragment {
	private View view;
	private LinearLayout searchshedule;//输入查询
	//private ListView tsak_list;//通知信息列表
	private TaskListAdapter madapter;
	private TextView s_date, e_date;// 起始时间，截止时间
	private Calendar c;// 日历
	private int year, month, day;// 年月日
	public static String StartTime="";
	public static String EndTime="";
	private CustomProgressDialog dialog;
	private ListViewOnNet listView;
	private int flag=0;
	private int selectDate = 0;// 时间选择标志
	private Boolean threadContinue = true,threadContinuetwo=true;// 线程是否继续运行的标识
	public static List<HashMap<String, Object>> schedulList=new ArrayList<HashMap<String,Object>>();
	
	/**为Fragment加载布局时调用 **/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.task_list, container, false);
		InitView();
		return view;
	}
	/**
	 * 初始化视图
	 * */
	private void InitView() {
			searchshedule=(LinearLayout)view.findViewById(R.id.searchshedule);
			searchshedule.setOnClickListener(mBtnClick);//查询
			s_date = (TextView) view.findViewById(R.id.s_date);
			e_date = (TextView) view.findViewById(R.id.e_date);
			s_date.setOnClickListener(mBtnClick);
			e_date.setOnClickListener(mBtnClick);
			listView = (ListViewOnNet)view.findViewById(R.id.schedulelist);
			listView.setOnItemClickListener(itemListener);
			listView.setOnRefreshListener(new OnRefreshLoadingMoreListener() {
					@Override
					public void onRefresh() {
						 schedulList.clear();
						    GetSchedulData();
						    flag=1;
					}
				});
			//时间选项
			c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			selectDate = 0;
			updateDateDisplay();
			selectDate = 1;
			updateDateDisplay();
			schedulList.clear();
	}
	private  Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Util.DOWNLOAD_twoOK:
				threadContinue=false;//结束线程
				dialog.dismiss();
				SetDataUI();
				listView.onRefreshComplete();
				 flag=0;
				break;
			case Util.DOWNLOAD_START:
				threadContinue=true;
				break;
			case Util.DOWNLOAD_OK:
				threadContinue=false;//结束线程
				dialog.dismiss();
				SetDataUI();
				break;
			case Util.DOWNLOAD_DISCONNECT:
				dialog.dismiss();
				Toast.makeText(getActivity(), "排班数据加载失败", Toast.LENGTH_LONG).show();
				break;
			case Util.DOWNLOAD_ERROR:
				dialog.dismiss();
				Toast.makeText(getActivity(), "排班无数据", Toast.LENGTH_LONG).show();
				//在登录失败的时候需要显示登录失误原因的提醒				
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
	private void SetDataUI(){
		madapter = new TaskListAdapter(getActivity(),schedulList);
		listView.setAdapter(madapter);
	}
	/**
	 * 开始登录服务器
	 * **/
	private void GetSchedulData() {
		dialog =new CustomProgressDialog(getActivity(), "正在加载中",R.anim.frame);
		dialog.show();
		GetSchedulDataThread t = new GetSchedulDataThread();
		// 启动登录线程
		t.start();
	}
	@SuppressWarnings("unchecked")
	public class GetSchedulDataThread extends Thread {
		/**
		 * 开始查询排班信息
		 * */
		@Override
		public void run() {
			sendMessage(Util.DOWNLOAD_START, "");
			long time = System.currentTimeMillis();			
			try
			{
		        NameValuePair NameValuePair1  = new BasicNameValuePair("d",dutyFragment.deviceid);
		        NameValuePair NameValuePair2  = new BasicNameValuePair("s",s_date.getText().toString());
		        NameValuePair NameValuePair3  = new BasicNameValuePair("e",e_date.getText().toString());
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(NameValuePair1);
		        params.add(NameValuePair2);  
		        params.add(NameValuePair3);  
		        String urlName = Util.getServerAddress(getActivity())+ "GetShiftsDetails?" + URLEncodedUtils.format(params, "UTF-8");
                String result = NetTool.sendTxt(urlName,"", "UTF-8");
                com.alibaba.fastjson.JSONArray json = JSONArray.parseArray(result);	//这是一个数组。我这只有第一个。你可以循环的明白了
                   if(json.size()>0){
                	   for (int i = 0; i < json.size(); i++) {
           				JSONObject obj = (JSONObject)json.get(i);
           			 HashMap<String, Object> map =new HashMap<String, Object>();
            				map.put("deviceid", obj.get("_DeviceId"));
            				map.put("endtime", obj.get("_EndTime"));
            				map.put("idchecked", obj.get("_IsChecked"));
            				map.put("reason", obj.get("_Reason"));
            				map.put("shifts", obj.get("_ShiftsExtendId"));
            				map.put("starttime", obj.get("_StartTime"));
            				map.put("workloc", obj.get("_WorkLoc"));
            				map.put("ID", obj.get("_Id"));
            				map.put("Replace", obj.get("_ReplaceId"));
            				map.put("IsAgree", obj.get("_IsAgree"));
            				 schedulList.add(map);
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
			if (!schedulList.isEmpty()) {//假设返回的用户名不为空，表示已经有返回的数据了
				//保存用户名、设备Id，部门信息
				if(flag==1){
					 sendMessage(Util.DOWNLOAD_twoOK, "");	
				}else{
					 sendMessage(Util.DOWNLOAD_OK, "");
				}
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
	/**
	 * 按钮的点击事件
	 * */
	OnClickListener mBtnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.searchshedule://查询排班
		    schedulList.clear();
		    GetSchedulData();	
				break;
			case R.id.s_date:
				new DatePickerDialog(getActivity(),
						// 绑定监听器
								new DatePickerDialog.OnDateSetListener() {
									@Override
									public void onDateSet(DatePicker view, int year,
											int monthOfYear, int dayOfMonth) {
										StartTime = String.valueOf(year)+"-"+formatString(String.valueOf(monthOfYear+1))+"-"+ formatString(String
														.valueOf(dayOfMonth));
										s_date.setText(StartTime);								           
									}
								},
								// 设置初始日期
								c.get(Calendar.YEAR),
								c.get(Calendar.MONTH), 
								c.get(Calendar.DAY_OF_MONTH)).show();	
				selectDate = 0;
				break;
			case R.id.e_date:
				new DatePickerDialog(getActivity(),
						// 绑定监听器
								new DatePickerDialog.OnDateSetListener() {
									@Override
									public void onDateSet(DatePicker view, int year,
											int monthOfYear, int dayOfMonth) {
										EndTime = String.valueOf(year)+"-"+formatString(String.valueOf(monthOfYear+1))+"-"+ formatString(String
														.valueOf(dayOfMonth));
										e_date.setText(EndTime);
										 
									}
								},
								// 设置初始日期
								c.get(Calendar.YEAR),
								c.get(Calendar.MONTH), 
								c.get(Calendar.DAY_OF_MONTH)).show();	
				selectDate = 1;
				break;
			}
		}
	};


	/**
	 * 选项的点击事件
	 * 
	 * **/
	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {	
			//传参
			Util.Position=position-1;	
//			// 启动Main Activity
			Intent intent=new Intent(getActivity(),ScheduleDetailActivity.class);
			startActivity(intent);
		}
	};
	public String formatString(String str) {
		String s = "" + str;
		if (s.length() == 1)
			s = "0" + str;
		return s;
	}
	/**
	 * 
	 * 更新日期显示
	 */

	private boolean updateDateDisplay() {
		c = Calendar.getInstance();
		StringBuilder dateString = new StringBuilder().append(year).append("-")
				.append((month + 1) < 10 ? "0" + (month + 1) : (month + 1))
				.append("-").append((day < 10) ? "0" + day : day);
		if (selectDate == 0)
			s_date.setText(dateString.toString());
		else {
			e_date.setText(dateString.toString());
			checkDate();
		}
		return true;
	}
	/**
	 * 检查日期
	 * */
	private boolean checkDate() {
		if (!CompareDate(s_date.getText().toString(), e_date.getText()
				.toString())) {
			Toast.makeText(getActivity(), "开始时间不能大于结束时间!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	/**
	 * 比较时间的大小
	 * */
	private boolean CompareDate(String time1, String time2) {
		SimpleDateFormat sd = null;
		sd = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(sd.parse(time1));
			c2.setTime(sd.parse(time2));
			int result = c1.compareTo(c2);
			if (result < 0 || result == 0)
				return true;

		} catch (ParseException e) {
			System.out.println("输入的日期格式有误！");
		}
		return false;
	}
	

}
