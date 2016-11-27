package com.sunsunsoft.shutaro.logview;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Log2 モデルの DAO
 */
public class Log2Dao {

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
    public Log2Dao(Realm realm) {
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

    /**
     * 要素を追加 Log2オブジェクトをそのまま追加
     * @param log
     */
    public void addOne(Log2 log) {

        mRealm.beginTransaction();
        mRealm.copyToRealm(log);
        mRealm.commitTransaction();
    }

    /**
     * 全オブジェクトをクリアする
     */
    public void clear() {
        RealmResults<Log2> results = mRealm.where(Log2.class).findAll();
        if ( results == null ) return;

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * 変更可能なコピーを作成する
     * @param log
     */
    public Log2 toChangeable(Log2 log) {
        return mRealm.copyFromRealm(log);
    }

    /**
     * かぶらないプライマリIDを取得する
     * @return
     */
    public int getNextId() {
        int nextId = 1;
        Number maxUserId = mRealm.where(Log2.class).max("id");

        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxUserId != null) {
            nextId = maxUserId.intValue() + 1;
        }
        return nextId;
    }
}
