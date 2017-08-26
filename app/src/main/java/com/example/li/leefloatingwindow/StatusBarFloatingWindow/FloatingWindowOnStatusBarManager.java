package com.example.li.leefloatingwindow.StatusBarFloatingWindow;

import android.content.Context;

/**
 * Created by Li on 2017/8/26.
 */

public class FloatingWindowOnStatusBarManager
{
    /**
     * 悬浮窗View的实例
     */
    private static FloatingWindowOnStatusBar mFloatingWindow;

    /**
     * 创建一个悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void createFloatingWindow(Context context) {
        if(mFloatingWindow == null){
            mFloatingWindow = new FloatingWindowOnStatusBar(context);
        }
    }

    /**
     * 将悬浮窗从屏幕上移除。
     */
    public static void removeFloatingWindow() {
        if(mFloatingWindow != null){
            mFloatingWindow.removeFloatingWindow();
            mFloatingWindow = null;
        }
    }

    /**
     * 更新悬浮窗的TextView上的数据，显示内存使用的百分比。
     *
     * @param strTip
     *            可传入应用程序上下文。
     */
    public static void updateTip(String strTip) {
        if(mFloatingWindow != null){
            mFloatingWindow.setTextTip(strTip);
        }

    }
}
