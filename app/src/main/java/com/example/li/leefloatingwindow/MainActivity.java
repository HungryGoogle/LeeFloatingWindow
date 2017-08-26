package com.example.li.leefloatingwindow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.li.leefloatingwindow.NormalFloatingBar.FloatingWindowManager;
import com.example.li.leefloatingwindow.StatusBarFloatingWindow.FloatingWindowOnStatusBarManager;

public class MainActivity extends Activity
{
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView)findViewById(R.id.id_hellowrold);

        findViewById(R.id.id_ButtonTest).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Intent intent = new Intent(MainActivity.this, FloatingWindowService.class);
//                startService(intent);
//                finish();

                FloatingWindowOnStatusBarManager.createFloatingWindow(MainActivity.this);
                FloatingWindowOnStatusBarManager.updateTip("...我的测试正在进行中...");

                FloatingWindowManager.createFloatingWindow(getApplicationContext());
                FloatingWindowManager.updateTip("Test: 60%");

                finish();
            }
        });
        findViewById(R.id.id_ButtonTest2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FloatingWindowOnStatusBarManager.removeFloatingWindow();
                FloatingWindowManager.removeFloatingWindow(getApplicationContext());
            }
        });
    }
}
