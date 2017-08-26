package com.example.li.leefloatingwindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.li.leefloatingwindow.NormalFloatingBar.FloatingWindowManager;
import com.example.li.leefloatingwindow.NormalFloatingBar.FloatingWindowService;
import com.example.li.leefloatingwindow.StatusBarFloatingWindow.FloatingWindowOnStatusBarManager;

public class MainActivity extends Activity
{
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FloatingWindowManager.isFloatingWindowShowing()){
            FloatingWindowManager.removeFloatingWindow(MainActivity.this);
        }


        mTextView = (TextView)findViewById(R.id.id_hellowrold);

        findViewById(R.id.id_ButtonTest).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //测试1： 启动一般浮窗，当回到当前页面，浮窗要消失掉
                Intent intent = new Intent(MainActivity.this, FloatingWindowService.class);
                startService(intent);
                finish();

//                // 测试2： 直接调用一般浮窗，不使用Service定时启动
//                FloatingWindowManager.createFloatingWindow(getApplicationContext());
//                FloatingWindowManager.updateTip("Test: 60%");

                // 测试3： 启动任务栏浮窗，点击浮窗，回到当前页面，浮窗不消失，一直存在
                FloatingWindowOnStatusBarManager.createFloatingWindow(MainActivity.this);
                FloatingWindowOnStatusBarManager.updateTip("...我的测试正在进行中...");


                finish();
            }
        });

        findViewById(R.id.id_ButtonTest2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 关闭测试 1 浮窗
                Intent intent = new Intent(MainActivity.this, FloatingWindowService.class);
                stopService(intent);

//                // 关闭测试 2 浮窗
//                FloatingWindowManager.removeFloatingWindow(getApplicationContext());

                // 关闭测试 3 浮窗
                FloatingWindowOnStatusBarManager.removeFloatingWindow();

            }
        });
    }
}
