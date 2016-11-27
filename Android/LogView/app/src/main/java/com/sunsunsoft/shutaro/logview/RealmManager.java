package com.sunsunsoft.shutaro.logview;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import android.content.Context;
/**
 * Created by shutaro on 2016/11/27.
 */

public class RealmManager {
    public static Realm realm;
    public static final int Version1 = 1;
    public static final int latestVersion = Version1; // Add TangoItemPos

    public static Realm getRealm() { return realm; }

    private static LogViewDataDao logDao;

    public static void initRealm(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .schemaVersion(latestVersion)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();

        logDao = new LogViewDataDao(realm);
    }

    public static LogViewDataDao getLogViewDao() {
        return logDao;
    }

    public static void closeRealm() {
        realm.close();
    }
}
