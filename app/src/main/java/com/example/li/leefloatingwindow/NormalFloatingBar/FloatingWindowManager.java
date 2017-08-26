package com.example.li.leefloatingwindow.NormalFloatingBar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.example.li.leefloatingwindow.R;

public class FloatingWindowManager
{
	/**
	 * 悬浮窗View的实例
	 */
	private static FloatingWindowSmallView mSmallWindow;
	
	/**
	 * 悬浮窗View的参数
	 */
	private static LayoutParams mSmallWindowParams;
	
	/**
	 * 用于控制在屏幕上添加或移除悬浮窗
	 */
	private static WindowManager mWindowManager;

	/**
	 * 创建一个悬浮窗。初始位置为屏幕的右部中间位置。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createFloatingWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (mSmallWindow == null) {
			mSmallWindow = new FloatingWindowSmallView(context);
			if (mSmallWindowParams == null) {
				mSmallWindowParams = new LayoutParams();
				mSmallWindowParams.type = LayoutParams.TYPE_PHONE;
				mSmallWindowParams.format = PixelFormat.RGBA_8888;
				mSmallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
				mSmallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				mSmallWindowParams.width = FloatingWindowSmallView.viewWidth;
				mSmallWindowParams.height = FloatingWindowSmallView.viewHeight;
				mSmallWindowParams.x = screenWidth;
				mSmallWindowParams.y = screenHeight / 2;
			}
			mSmallWindow.setParams(mSmallWindowParams);
			windowManager.addView(mSmallWindow, mSmallWindowParams);
		}
	}

	/**
	 * 将悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeFloatingWindow(Context context) {
		if (mSmallWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(mSmallWindow);
			mSmallWindow = null;
		}
	}
	
	/**
	 * 更新悬浮窗的TextView上的数据，显示内存使用的百分比。
	 * 
	 * @param strTip 提示语
	 */
	public static void updateTip(String strTip) {
		if (mSmallWindow != null) {
			TextView percentView = (TextView) mSmallWindow.findViewById(R.id.id_text_tip);
			percentView.setText(strTip);
		}
	}

	/**
	 * 是否有悬浮窗显示在屏幕上。
	 * 
	 * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
	 */
	public static boolean isFloatingWindowShowing() {
		return mSmallWindow != null;
	}

	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}
	

	/**
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 以字符串形式返回。
	 */
	public static String getDefaultValue(Context context) {
		return "悬浮窗";
	}
	
}
