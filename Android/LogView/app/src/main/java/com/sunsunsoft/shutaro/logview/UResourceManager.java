package com.sunsunsoft.shutaro.logview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;

import java.util.HashMap;

/**
 * Created by shutaro on 2016/12/09.
 *
 * Bitmap画像やstrings以下の文字列等のリソースを管理する
 */
public class UResourceManager {
    private HashMap<Integer,Bitmap> mBitmaps = new HashMap<>();

    /**
     * Member variables
     */
    private Context mContext;

    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static UResourceManager singleton;

    // Singletonオブジェクトを作成する
    public static UResourceManager createInstance(Context context) {
        if (singleton == null ) {
            singleton = new UResourceManager(context);
        }
        return singleton;
    }
    public static UResourceManager getInstance() { return singleton; }

    private UResourceManager(Context context) {
        mContext = context;
    }

    /**
     * Methods
     */
    public static void clear() {
        UResourceManager instance = getInstance();
        instance.mBitmaps.clear();
    }

    /**
     * stringsのIDで文字列を取得する
     * @param strId
     */
    public static String getStringById(int strId) {
        UResourceManager instance = getInstance();

        return instance.mContext.getString(strId);
    }

    /**
     * Bitmapを取得
     * @param bmpId
     * @return Bitmapオブジェクト / もしBitmapがロードできなかったら null
     */
    public static Bitmap getBitmapById(int bmpId) {
        UResourceManager instance = getInstance();

        if (instance.mBitmaps.containsKey(bmpId)) {
            // すでにロード済みならオブジェクトを返す
            return instance.mBitmaps.get(bmpId);
        } else {
            // 未ロードならロードしてからオブジェクトを返す
            Bitmap bmp = BitmapFactory.decodeResource(instance.mContext.getResources(), bmpId);
            if (bmp != null) {
                instance.mBitmaps.put(bmpId, bmp);
                return bmp;
            }
        }
        return null;
    }
}
