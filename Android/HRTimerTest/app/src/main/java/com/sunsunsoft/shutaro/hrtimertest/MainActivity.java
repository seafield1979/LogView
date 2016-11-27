package com.sunsunsoft.shutaro.hrtimertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

/**
 * 高精度タイマーのテスト
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {
    /**
     * Enums
     */

    /**
     * Consts
     */
    public static final String TAG = "MainActivity";

    private static int[] buttonIds = {
            R.id.buttonAdd,
            R.id.buttonShow,
            R.id.buttonClear
    };

    /**
     * Member Variables
     */
    LogStack mLog = new LogStack(100);

    /**
     * Get/Set
     */

    /**
     * Constructor
     */

    /**
     * Override Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add Listener
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    /**
     * Methods
     */

    /**
     * Callbacks
     */
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonAdd: {
                LogBase log = mLog.addLog(System.nanoTime(), "hoge");
                Log.d(TAG, log.toString());
            }
                break;
            case R.id.buttonShow:
                mLog.showAllLog();
                break;
            case R.id.buttonClear:
                mLog.clearLog();
                Log.d(TAG, "clear logs");
                break;
        }
    }
}
