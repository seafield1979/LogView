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
    Nano100(0, 100, TimeUnit.Micro),
    Nano250(1, 250, TimeUnit.Micro),
    Nano500(2, 500, TimeUnit.Micro),
    Nano1000(3, 1000, TimeUnit.Micro),
    Nano2500(4, 2500, TimeUnit.Micro),
    Nano5000(5, 5000, TimeUnit.Micro),
    Micro10(6, 10000, TimeUnit.Micro),
    Micro25(7, 25000, TimeUnit.Milli),
    Micro50(8, 50000, TimeUnit.Milli),
    Micro100(9, 100000, TimeUnit.Milli),
    Micro250(10, 250000, TimeUnit.Milli),
    Micro500(11, 500000, TimeUnit.Milli),
    Micro1000(12, 1000000, TimeUnit.Milli),
    Micro2500(13, 2500000, TimeUnit.Milli),
    Micro5000(14, 5000000, TimeUnit.Milli),
    Milli10(15, 10000000, TimeUnit.Milli),
    Milli25(16, 25000000, TimeUnit.Sec),
    Milli50(17, 50000000, TimeUnit.Sec),
    Milli100(18, 100000000, TimeUnit.Sec),
    Milli250(19, 250000000, TimeUnit.Sec),
    Milli500(20, 500000000, TimeUnit.Sec),
    Milli1000(21, 1000000000, TimeUnit.Sec),
    Milli2500(22, 2500000000L, TimeUnit.Sec),
    Milli5000(23, 5000000000L, TimeUnit.Sec),
    Sec10(24, 10000000000L, TimeUnit.Sec),
    Sec25(25, 25000000000L, TimeUnit.Sec),
    Sec50(26, 50000000000L, TimeUnit.Sec),
    Sec100(27, 100000000000L, TimeUnit.Sec),
    Sec250(28, 250000000000L, TimeUnit.Sec),
    Sec500(29, 500000000000L, TimeUnit.Sec),
    Sec1000(30, 1000000000000L, TimeUnit.Sec)
    ;

    public int value;
    long divValue;
    TimeUnit timeUnit;

    PixelPerTime(final int value, long divValue, TimeUnit timeUnit) {
        this.value = value;
        this.divValue = divValue;
        this.timeUnit = timeUnit;
    }

    private void update(int value) {
        PixelPerTime p2t = toEnum(value);
        divValue = p2t.divValue;
        timeUnit = p2t.timeUnit;
    }

    /**
     * １ピクセルに相当する時間を返す
     * この値でlong型のtimeを割ると1ピクセルあたりの時間が取得できる
     */
    public long getDivValue() {
        return divValue;
    }

    /**
     * タイムバーに表示する単位情報を取得
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }


    /**
     * 次の値に進める
     */
    public void zoomOut() {
        value++;
        if (value >= values().length) {
            value = values().length - 1;
        }
        update(value);
    }

    /**
     * 次の値に進める
     */
    public void zoomIn() {
        value--;
        if (value < 0) {
            value = 0;
        }
        update(value);
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
