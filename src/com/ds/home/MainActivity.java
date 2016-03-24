package com.ds.home;

import java.lang.reflect.Field;

import ser.ds.util.Util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.provider.MediaStore.Audio;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;




import com.baidu.push.example.R;
import com.ds.PagerSlidingTabStrip;


public class MainActivity extends FragmentActivity {
	/**
	 * 主界面的Fragment
	 */
	private  dutyFragment dutyFragmentinstance;

	/**
	 * 排班界面的Fragment
	 */
	private  scheduleFragment scheduleFragmentinstance;

	/**
	 * 公告界面的Fragment
	 */
	private  static noticeFragment noticeFragmentinstance;

	/**
	 * PagerSlidingTabStrip的实例
	 */
	private PagerSlidingTabStrip tabs;

	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;
	public static int flagposition=0;
	private MyPagerAdapter adapter=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Utils.logStringCache = Utils.getLogText(this.getApplicationContext());
		setContentView(R.layout.activity_main);
		 InitView();
	}
	private void InitView()
	{
		setOverflowShowingAlways();//设置水平滑动方式
		dm = getResources().getDisplayMetrics();//获取资源的分辨率也就是像素
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);//自定义滑动控件
		adapter=new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		setTabsValue();
	}
//	@Override
//	public void onResume()
//	{
//			InitView();
//		super.onResume();
//	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 23, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#ffffff"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#ffffff"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}
    public  static noticeFragment getInstance()
    {
	    return noticeFragmentinstance;
    }
	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "执勤", "排班", "公告" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}
		@Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }
		@Override
		public Fragment getItem(int position) {
			if(flagposition!=0)
			{
				position=flagposition;
			}
			switch (position) {
			case 0:
				if (dutyFragmentinstance == null) {
					dutyFragmentinstance = new dutyFragment();
				}
				return dutyFragmentinstance;
			case 1:
				if (scheduleFragmentinstance == null) {
					scheduleFragmentinstance = new scheduleFragment();
				}
				return scheduleFragmentinstance;
			case 2:
				if (noticeFragmentinstance == null) {
					noticeFragmentinstance = new noticeFragment();
				}
				return noticeFragmentinstance;
			default:
				return null;
			}
		}

	}
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);//滑动参数设置类
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            moveTaskToBack(false);  
            return true;  
        }  
        return super.onKeyDown(keyCode, event);  
    }  

}