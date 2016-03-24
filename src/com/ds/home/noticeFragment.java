package com.ds.home;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ser.ds.util.Util;
import ser.ds.util.WriteNoticeInfo;

import com.baidu.push.example.R;
import com.baidu.push.example.Utils;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 通讯录Fragment的界面
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 * @author zhao
 */
public class noticeFragment extends Fragment {
	private  View view;
	private  TextView logText = null;
	private ScrollView scrollView = null;
	private ImageView text_log_history;
	private String txtcontent;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	   	getActivity().moveTaskToBack(true);
		 view = inflater.inflate(R.layout.notice_content, container, false);
		 Utils.logStringCache = Utils.getLogText(getActivity().getApplicationContext());
	     Resources resource = this.getResources();
	     String pkgName = getActivity().getPackageName();
		 logText = (TextView) view.findViewById(resource.getIdentifier("text_log","id", pkgName));
	     scrollView = (ScrollView) view.findViewById(resource.getIdentifier(
	                "stroll_text", "id", pkgName));
	     text_log_history=(ImageView)view.findViewById(R.id.text_log_history);
	     text_log_history.setOnClickListener(mBtnClick);
	     UpdateView();
		 return view;
	}
	OnClickListener mBtnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.text_log_history:
				File sdCardDir = Environment.getExternalStorageDirectory();  
                String directory="DefenseService/noticeinfo";
                String fileName="noticeinfo.txt";
                try {
					 String file_full_path = sdCardDir.getCanonicalPath()+"/"+directory+"/"+fileName;
					 File targetFile = new File(file_full_path); 
					 try {
						txtcontent=WriteNoticeInfo.readTxtFile(targetFile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if(txtcontent==null){
                	  Toast.makeText(getActivity(), "没有历史公告内容！", 3000).show();
                }else{
                	 logText.setText(txtcontent.substring(4));
                }
				break;
			}
		}
	};

	@Override
	public void onStart()
	{
		UpdateView();
		super.onStart();
	}
	@Override
	public void onResume()
	{
		UpdateView();
		super.onResume();
	}
	public void UpdateView()
	{
		if(Util.noticecontent==""||logText==null)return;
		logText.setText(Util.noticecontent);
	
	}
}
