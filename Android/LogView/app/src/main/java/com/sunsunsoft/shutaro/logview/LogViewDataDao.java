package com.sunsunsoft.shutaro.logview;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Log2 モデルの DAO
 */
public class LogViewDataDao {

    /**
     * Constract
     */
    public static final String TAG = "TangoBoxDao";

    /**
     * Member variables
     */
    private Realm mRealm;

    /**
     * Constructor
     */
    public LogViewDataDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<Log2> selectAll() {
        RealmResults<Log2> results = mRealm.where(Log2.class).findAll();
        LinkedList<Log2> list = new LinkedList<>();
        for (Log2 log : results) {
            list.add(log);
        }

        return list;
    }

}
