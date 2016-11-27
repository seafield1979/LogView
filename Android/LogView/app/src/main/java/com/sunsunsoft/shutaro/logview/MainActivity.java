package com.sunsunsoft.shutaro.logview;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UDrawManager.getInstance().init();
        ULog.init();

        // Realmの初期化
        RealmManager.initRealm(getApplicationContext());

        if (savedInstanceState == null) {
            FragmentTop fragment = new FragmentTop();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // コンテナにMainFragmentを格納
            transaction.add(R.id.fragment_container, fragment, FragmentTop.TAG);
            // 画面に表示
            transaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RealmManager.closeRealm();
    }
}
