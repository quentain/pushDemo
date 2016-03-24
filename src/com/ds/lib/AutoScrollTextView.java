package com.ds.lib;

import java.io.File;

import ser.ds.util.Util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 
 * TODO �����ı�����ƿؼ�?
 * 
 * @author tianlu
 * @version 1.0 Create At : 2010-2-16 ����09:35:03
 */
public class AutoScrollTextView extends TextView {
	public final static String TAG = AutoScrollTextView.class.getSimpleName();

	/** Called when the activity is first created. */
	private static Handler handler;
	private float textLength = 0f;// �ı�����
	private float viewWidth = 0f;
	private float step = 0f;// ���ֵĺ����?
	private float y = 0f;// ���ֵ������?
	private float temp_view_plus_text_length = 0.0f;// ���ڼ������ʱ����?
	private float temp_view_plus_two_text_length = 0.0f;// ���ڼ������ʱ����?
	public boolean isStarting = false;// �Ƿ�ʼ����
	private Paint paint = null;// ��ͼ��ʽ
	private String text = "";// �ı�����
	private int Speed = 2;// �����ٶ�
	private WindowManager _windowManager;
	// RectF src = new RectF(10f, 0f, 100f, 20f); ;
	String str1, str2, str3;
	private static Bitmap mBitmap = null;

	public AutoScrollTextView(Context context) {
		super(context);
		initView();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		// setOnClickListener(this);
	}

	/**
	 * �ı���ʼ����ÿ�θ���ı����ݻ����ı�Ч���֮����Ҫ���³�ʼ��һ��
	 */
	public void init(WindowManager windowManager) {
		paint = getPaint();
		paint.setColor(this.getTextColors().getDefaultColor());
		paint.setTextSize(this.getTextSize());
		_windowManager = windowManager;

		handler = new Handler() {// ����һ��handler���� �����ڼ������̷߳��͵���Ϣ
			@SuppressWarnings("deprecation")
			@Override
			public void handleMessage(Message msg)// ������Ϣ�ķ���
			{
				step = 0f;// ���ֵĺ����?
				temp_view_plus_text_length = 0.0f;// ���ڼ������ʱ����?
				temp_view_plus_two_text_length = 0.0f;// ���ڼ������ʱ����?
				setText(Util.getWarningInfo());
				text = getText().toString();
				textLength = paint.measureText(text);
				viewWidth = getWidth();
				if (viewWidth == 0) {
					if (_windowManager != null) {
						Display display = _windowManager.getDefaultDisplay();
						viewWidth = display.getWidth();
					}
				}
				step = textLength;
				temp_view_plus_text_length = viewWidth + textLength;
				temp_view_plus_two_text_length = viewWidth + textLength * 2;
				y = (getTextSize() + getHeight()) / 2 - 5;
				// y = getTextSize()+getPaddingTop();
				invalidate();
			}
		};
		ReBindText();
	}

	public static void ReBindText() {
		// Bitmap bitmap=BitmapFactory.decodeFile(imgPath);
		mBitmap = null;
		// ֪ͨϵͳ���½���,�൱�ڵ�����onDraw����
		Message msg = new Message();// ������Ϣ��
		msg.obj = "change";// ��Ϣ������д������?
		if (handler == null)
			return;
		handler.sendMessage(msg);// ͨ��handler��������Ϣ
	}

	/**
	 * ���°��ı�
	 * 
	 * @param
	 * @return
	 */
	public static void ReBindText(String imgPath) {
		File file=new File(imgPath);
		if(file.isFile()==true)
		{
			Bitmap bm = BitmapFactory.decodeFile(imgPath);
			if (bm == null)
				mBitmap = null;
			else {
				//mBitmap = BitmapUtil.BitmapScale(bm, 50, 30);
				
			}
		}
		// ֪ͨϵͳ���½���,�൱�ڵ�����onDraw����
		Message msg = new Message();// ������Ϣ��
		msg.obj = "change";// ��Ϣ������д������?
		if (handler == null)
			return;
		handler.sendMessage(msg);// ͨ��handler��������Ϣ
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);

		ss.step = step;
		ss.isStarting = isStarting;

		return ss;

	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		step = ss.step;
		isStarting = ss.isStarting;

	}

	public static class SavedState extends BaseSavedState {
		public boolean isStarting = false;
		public float step = 0.0f;

		SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeBooleanArray(new boolean[] { isStarting });
			out.writeFloat(step);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}

			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
		};

		@SuppressWarnings("unused")
		private SavedState(Parcel in) {
			super(in);
			boolean[] b = null;
			in.readBooleanArray(b);
			if (b != null && b.length > 0)
				isStarting = b[0];
			step = in.readFloat();
		}
	}

	/**
	 * ��ʼ����
	 */
	public void startScroll() {
		isStarting = true;
		invalidate();
	}

	/**
	 * ֹͣ����
	 */
	public void stopScroll() {
		isStarting = false;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isStarting) {
			return;
		}
		canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, temp_view_plus_text_length - step - 50,
					y - 25, paint);
		}
		step += Speed;
		if (step > temp_view_plus_two_text_length)
			step = textLength;
		invalidate();
	}

	public void onClick(View v) {
		if (isStarting)
			stopScroll();
		else
			startScroll();

	}

}