package com.example.li.leefloatingwindow.StatusBarFloatingWindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.li.leefloatingwindow.NormalFloatingBar.FloatingWindowManager;
import com.example.li.leefloatingwindow.NormalFloatingBar.FloatingWindowService;
import com.example.li.leefloatingwindow.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FloatingWindowOnStatusBar extends LinearLayout
{
	private Context mContext;
	private WindowManager.LayoutParams mLayoutParams;
	private TextView mStatusBarTextView;
	private View mLayoutRoot;

	private GestureDetector mGestureDetector;

	/**
	 * 记录系统状态栏的高度
	 */
	 private static int mStatusBarHeight;


	//分别用于记录按下，移动、抬起时相应的x、y坐标
	private int startX, startY, moveX, moveY, stopX, stopY;
	private int offsetX, offsetY;

	// 当在任务栏向下滑动这么多距离时候，展开任务栏
	private static final int MAX_MOVE_OFFSET = 8;

	//用于标记悬浮窗是否有移动，当移动时候，需要展开系统的“状态栏”
	private boolean isMove;

	/**
	 * 窗口管理器
	 */
	private WindowManager mWindowManager;

	/**
	 * @param context 应用程序的Context
	 */
	public FloatingWindowOnStatusBar(Context context) {
		super(context);
		mContext = context;
		mWindowManager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
		//设置悬浮窗的参数
		mLayoutParams = initParams();
		//加载要悬浮的布局
		mLayoutRoot = LayoutInflater.from(mContext).inflate(R.layout.view_window, null);

		//获取子控件
		mStatusBarTextView = (TextView) mLayoutRoot.findViewById(R.id.id_status_bar_tip);

		//动态将子控件的高度设置成状态栏的高度
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mStatusBarTextView.getLayoutParams();
		layoutParams.height = getStatusBarHeight(mContext);
		mStatusBarTextView.setLayoutParams(layoutParams);

		//添加悬浮窗的视图
		mWindowManager.addView(mLayoutRoot, mLayoutParams);

		initEvent();
	}


	public void removeFloatingWindow(){
		if(mLayoutRoot != null){
			//添加悬浮窗的视图
			mWindowManager.removeView(mLayoutRoot);
			mLayoutRoot = null;
		}
	}

	public void setTextTip(String strTip){
		if(mStatusBarTextView != null){
			mStatusBarTextView.setText(strTip);
		}
	}

	/**
	 * 初始化事件
	 */
	private void initEvent() {
		mGestureDetector = new GestureDetector(mContext, new CustomOnGustureListener());
		mStatusBarTextView.setOnTouchListener(new OnFloatingListener());
	}

	/**
	 * 自定义手势监听器
	 */
	private class CustomOnGustureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e)
		{
			//如果isMove不为true表示是点击事件
			if (!isMove) {
				Toast.makeText(mContext, "你点击了悬浮窗", Toast.LENGTH_SHORT).show();
				Log.i("leeTest-------->", "onClick");
				
			}
			return super.onDown(e);
		}
	}


	/**
	 * 由于悬浮窗是位于状态栏之上且覆盖状态栏的焦点以至于状态栏的相应事件失效，如：下拉出通知
	 * 因此需要通过监听悬浮窗在不同状态下触发相应的事件
	 */
	private class OnFloatingListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
				case MotionEvent.ACTION_DOWN:
					isMove = false;
					startX = (int) event.getX();
					startY = (int) event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					moveX = (int) event.getX();
					moveY = (int) event.getY();
					offsetX = Math.abs(startX - moveX);
					offsetY = Math.abs(startY - moveY);
//                    Log.i("leeTest-------->", "ACTION_MOVE startX==" + startX + " startY==" + startY);
//                    Log.i("leeTest-------->", "ACTION_MOVE moveX==" + moveX + " moveY==" + moveY);
					Log.i("leeTest-------->", "ACTION_MOVE offsetX==" + offsetX + " offsetY==" + offsetY);
					//当移动距离大于某个值时，表示是在下拉状态栏，此时展开状态栏
					if (Math.abs(offsetY) >= MAX_MOVE_OFFSET) {
						expandStatusBar(mContext);
					}
					break;
				case MotionEvent.ACTION_UP:
					stopX = (int) event.getX();
					stopY = (int) event.getY();
					offsetX = Math.abs(startX - stopX);
					offsetY = Math.abs(startY - stopY);
//                    Log.i("leeTest-------->", "ACTION_UP startX==" + startX + " startY==" + startY);
//                    Log.i("leeTest-------->", "ACTION_UP stopX==" + stopX + " stopY==" + stopY);
					Log.i("leeTest-------->", "ACTION_UP offsetX==" + offsetX + " offsetY==" + offsetY);
					//如果手抬起时移动的距离大于某个值，表示是处于下拉操作
					if (Math.abs(offsetY) >= MAX_MOVE_OFFSET) {
						isMove = true;
					}
					break;
			}
			return mGestureDetector.onTouchEvent(event);//将onTouchEvent交给GestureDetector处理
		}
	}


	/**
	 * 展开状态栏
	 * @param context
	 */
	public static void expandStatusBar(Context context) {
		Object sbservice = context.getSystemService("statusbar");
		try {
			Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
			Method expand;
			if (Build.VERSION.SDK_INT >= 17) {
				expand = statusBarManager.getMethod("expandNotificationsPanel");
			} else {
				expand = statusBarManager.getMethod("expand");
			}
			expand.setAccessible(true);
			
			// // TODO: 2017/8/26 需要申请权限 <uses-permission android:name="android.permission.EXPAND_STATUS_BAR"/>
			expand.invoke(sbservice);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得状态栏高度
	 */
	public static int getStatusBarHeight(Context context) {
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		return context.getResources().getDimensionPixelSize(resourceId);
	}

	/**
	 * 设置悬浮窗的参数
	 *
	 * @return
	 */
	private WindowManager.LayoutParams initParams() {
		mLayoutParams = new WindowManager.LayoutParams();
		//设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		//设置图片格式，效果为背景透明
		mLayoutParams.format = PixelFormat.RGBA_8888;
//        mLayoutParams.format = PixelFormat.RGBA_5551;
		//设置可以显示在状态栏上,flags值须大于1280时，悬浮窗才会在状态栏之上
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		//设置悬浮窗口长宽数据
		mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		// 悬浮窗默认显示以左上角为起始坐标
		mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		return mLayoutParams;
	}

	/**
	 * 点击浮窗后的处理
	 */
	private void onClickFloatingWindow() {
		Log.i("leeTest----->", "onClickFloatingWindow");
		FloatingWindowManager.removeFloatingWindow(getContext());

		if(mContext != null){
			Intent intent = new Intent(mContext, FloatingWindowService.class);
			mContext.startService(intent);
		}
	}

	/**
	 * 用于获取状态栏的高度。
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	private int getStatusBarHeight() {
		if (mStatusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				mStatusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mStatusBarHeight;
	}

}
