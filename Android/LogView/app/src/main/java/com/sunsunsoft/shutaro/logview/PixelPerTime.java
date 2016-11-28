package com.sunsunsoft.shutaro.logview;

/**
 * Created by shutaro on 2016/11/28.
 */

/**
 * タイムバーに表示する時間単位
 */
enum TimeUnit {
    Nano,
    Micro,
    Milli,
    Sec
    ;

    // タイムバーに表示する時間の単位
    private static String[] timeUnitTable = {
        "ns",
        "us",
        "ms",
        "s"
    };

    // time(nano sec)を指定の単位に変換するための割り算の値
    private static long[] divValueTable = {
            1,
            1000,
            1000000,
            1000000000
    };

    public String unitStr() {
        return timeUnitTable[ordinal()];
    }
    public long divValue() {
        return divValueTable[ordinal()];
    }
}

/**
 * PixelPerTimeで使用するデータの構造体風クラス
 */
class P2TTableData {
    long divValue;
    TimeUnit timeUnit;

    public P2TTableData(long divValue, TimeUnit unit) {
        this.divValue = divValue;
        this.timeUnit = unit;
    }
}

enum PixelPerTime {
    /**
     * Enums
     */
    // 変換の種類
    Nano100(0),
    Nano250(1),
    Nano500(2),
    Nano1000(3),
    Nano2500(4),
    Nano5000(5),
    Micro10(6),
    Micro25(7),
    Micro50(8),
    Micro100(9),
    Micro250(10),
    Micro500(11),
    Micro1000(12),
    Micro2500(13),
    Micro5000(14),
    Milli10(15),
    Milli25(16),
    Milli50(17),
    Milli100(18),
    Milli250(19),
    Milli500(20),
    Milli1000(21),
    Milli2500(22),
    Milli5000(23),
    Sec10(24),
    Sec25(25),
    Sec50(26),
    Sec100(27),
    Sec250(28),
    Sec500(29),
    Sec1000(30)
    ;

    // Type毎に1ピクセルに相当する時間(nano sec)を返すテーブル
    // 例: 1 なら 1ピクセルで1ナノ秒(ns)(0.000000001秒)
    private static final P2TTableData[] perSecTable = {
            new P2TTableData(100, TimeUnit.Micro), // Nano100,
            new P2TTableData(250, TimeUnit.Micro), // Nano250,
            new P2TTableData(500, TimeUnit.Micro), // Nano500,
            new P2TTableData(1000, TimeUnit.Micro), // Nano1000,
            new P2TTableData(2500, TimeUnit.Micro), // Nano2500,
            new P2TTableData(5000, TimeUnit.Micro), // Nano5000,
            new P2TTableData(10000, TimeUnit.Micro), // Micro10,
            new P2TTableData(25000, TimeUnit.Milli), // Micro25,
            new P2TTableData(50000, TimeUnit.Milli), // Micro50,
            new P2TTableData(100000, TimeUnit.Milli), // Micro100,
            new P2TTableData(250000, TimeUnit.Milli), // Micro250,
            new P2TTableData(500000, TimeUnit.Milli), // Micro500,
            new P2TTableData(1000000, TimeUnit.Milli), // Micro1000,
            new P2TTableData(2500000, TimeUnit.Milli), // Micro2500,
            new P2TTableData(5000000, TimeUnit.Milli), // Micro5000,
            new P2TTableData(10000000, TimeUnit.Milli), // Milli10,
            new P2TTableData(25000000, TimeUnit.Sec), // Milli25,
            new P2TTableData(50000000, TimeUnit.Sec), // Milli50,
            new P2TTableData(100000000, TimeUnit.Sec), // Milli100,
            new P2TTableData(250000000, TimeUnit.Sec), // Milli250,
            new P2TTableData(500000000, TimeUnit.Sec), // Milli500,
            new P2TTableData(1000000000, TimeUnit.Sec), // Milli1000,
            new P2TTableData(2500000000L, TimeUnit.Sec), // Milli2500,
            new P2TTableData(5000000000L, TimeUnit.Sec), // Milli5000,
            new P2TTableData(10000000000L, TimeUnit.Sec), // Sec10,
            new P2TTableData(25000000000L, TimeUnit.Sec), // Sec25,
            new P2TTableData(50000000000L, TimeUnit.Sec), // Sec50,
            new P2TTableData(100000000000L, TimeUnit.Sec), // Sec100,
            new P2TTableData(250000000000L, TimeUnit.Sec), // Sec250,
            new P2TTableData(500000000000L, TimeUnit.Sec), // Sec500,
            new P2TTableData(1000000000000L, TimeUnit.Sec), // Sec1000
    };

    public int value;

    PixelPerTime(final int value) {
        this.value = value;
    }

    /**
     * １ピクセルに相当する時間を返す
     * この値でlong型のtimeを割ると1ピクセルあたりの時間が取得できる
     */
    public long getDivValue() {
        return perSecTable[value].divValue;
    }

    /**
     * タイムバーに表示する単位情報を取得
     */
    public TimeUnit getTimeUnit() {
        return perSecTable[value].timeUnit;
    }


    /**
     * 次の値に進める
     */
    public void next() {
        value++;
        if (value >= values().length) {
            value = values().length - 1;
        }
    }

    /**
     * 次の値に進める
     */
    public void pref() {
        value--;
        if (value < 0) {
            value = 0;
        }
    }

    public PixelPerTime toEnum(int value) {
        for (PixelPerTime p2t : values()) {
            if (p2t.ordinal() == value) {
                return p2t;
            }
        }
        return Nano100;
    }
}
