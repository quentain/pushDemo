

package com.ds.lib;

import java.util.Timer;
import java.util.TimerTask;

import com.baidu.push.example.R;





import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @ClassName: com.example.animationloading.LoadingDialog
 * @Description: 动画加载Dialog
 * @author zhaokaiqiang
 * @date 2014-10-27 下午4:42:52
 * 
 */
public class LoadingDialog extends Dialog {

	protected static final String TAG = "LoadingDialog";
	
	private static final int DURATION = 800;
	
	private ImageView img_front;
	
	private Timer animationTimer;
	
	private RotateAnimation animationL2R;

	private Animation animation;

	private Context context;
	private static TextView message;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			img_front.setAnimation(animationL2R);
			animationL2R.start();
		
		};

	};

	public LoadingDialog(Context context) {
		super(context, R.style.dialog);
		this.context = context;
	}
	public  void  SetMessage(String Msg)
	{
		message.setText(Msg);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		message=(TextView) findViewById(R.id.message);
		img_front = (ImageView) findViewById(R.id.img_front);
		animationTimer = new Timer();

		
		animationL2R = new RotateAnimation(0f, -90f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
			
		animationL2R.setDuration(DURATION);
		
		animationL2R.setFillAfter(true);
	
		animationL2R.setRepeatCount(1);
		
		animationL2R.setRepeatMode(Animation.REVERSE);

	
		animationTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(1);
			}
		}, 0, DURATION * 2);

	}

	@Override
	protected void onStop() {
		super.onStop();
		animationTimer.cancel();
	}

}