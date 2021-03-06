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
    public List<LogBase> selectAll() {
        RealmResults<Log2> results = mRealm.where(Log2.class).findAll();
        LinkedList<LogBase> list = new LinkedList<>();
        for (Log2 log : results) {
            list.add(log);
        }

        return list;
    }

    /**
     * 一番時間が早いログを取得
     * @return
     */
    public long selectMinLogTime() {
        RealmResults<Log2> results = mRealm.where(Log2.class)
                .findAll();
        if (results == null || results.size() == 0) return 0;

        Number min = results.min("time");
        return min.longValue();
    }

    /**
     * 時間が一番遅いログを取得
     * @return
     */
    public long selectMaxLogTime() {
        RealmResults<Log2> results = mRealm.where(Log2.class)
                .findAll();
        if (results == null || results.size() == 0) return 0;

        Number max = results.max("time");
        return max.longValue();
    }

    /**
     * 2点間の領域に含まれるログを取得する
     * @param start
     * @param end
     * @return
     */
    public List<LogBase> selectByAreaTime(long start, long end) {
        RealmResults<Log2> results = mRealm.where(Log2.class)
                .between("time", start, end)
                .findAll();

        // Log2 -> LogBase に変換
        LinkedList<LogBase> list = new LinkedList<>();
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
