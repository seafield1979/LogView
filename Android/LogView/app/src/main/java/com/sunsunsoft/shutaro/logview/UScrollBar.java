package com.sunsunsoft.shutaro.logview;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

// スクロールバーの配置場所
enum ScrollBarType {
    Top,
    Bottom,
    Left,
    Right
}

// スクロールバーの配置場所2
enum ScrollBarInOut {
    In,
    Out
}

// スクロールバーのバーの位置設定用
enum ScrollBarPos {
    Top,
    Center,
    Bottom
}

/**
 * 自前で描画するスクロールバー
 * タッチ操作あり
 *
 * 機能
 *  外部の値に連動してスクロール位置を画面に表示
 *  ドラッグしてスクロール
 *  バー以外の領域をタップしてスクロール
 *  指定のViewに張り付くように配置
 */
public class UScrollBar {
    /**
     * Constants
     */
    public static final String TAG = "UScrollBar";
    private static final int MIN_BAR_LEN = 70;

    /**
     * Membar Variables
     */
    private ScrollBarType type;
    private ScrollBarInOut inOut;

    private PointF pos = new PointF();
    private PointF parentPos;
    private int bgColor, barColor;
    private boolean isDraging;

    // スクリーン座標系
    private int bgLength, bgWidth;
    private float barPos;        // バーの座標（縦ならy,横ならx)
    private int barLength;       // バーの長さ(縦バーなら高さ、横バーなら幅)

    // コンテンツ座標系
    private long contentLen;       // コンテンツ領域のサイズ
    private long pageLen;          // 表示画面のサイズ
    private long topPos;         // スクロールの現在の位置

    // 縦のスクロールバーか
    private boolean isVertical() {
        return (type == ScrollBarType.Left || type == ScrollBarType.Right);
    }
    // 横のスクロールバーか
    private boolean isHorizontal() {
        return (type == ScrollBarType.Top || type == ScrollBarType.Bottom);
    }

    /**
     * Get/Set
     */
    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    public float getTopPos() {
        return topPos;
    }

    private void updateBarLength() {
        ULog.print(TAG, "pageLen:" + pageLen + " contentLen:" + contentLen);
        if (pageLen >= contentLen) {
            // 表示領域よりコンテンツの領域が小さいので表示不要
            barLength = 0;
            topPos = 0;
        } else {
            barLength = (int) (this.bgLength * ((float) pageLen / (float) contentLen));
        }
        ULog.print(TAG, "barLength:" + barLength);
    }

    public int getBgWidth() {
        return bgWidth;
    }

    public void setPageLen(long pageLen) {
        this.pageLen = pageLen;
    }


    /**
     * コンストラクタ
     * 指定のViewに張り付くタイプのスクロールバーを作成
     *
     * @param type
     * @param inOut
     * @param parentPos
     * @param viewWidth
     * @param viewHeight
     * @param width
     * @param pageLen   1ページ分のコンテンツの長さ
     * @param contentLen  全体のコンテンツの長さ
     */
    public UScrollBar(ScrollBarType type, ScrollBarInOut inOut,
                      PointF parentPos, int viewWidth, int viewHeight, int width,
                      long pageLen, long contentLen ) {
        this.type = type;
        this.inOut = inOut;
        this.parentPos = parentPos;
        topPos = 0;
        barPos = 0;
        this.bgWidth = width;
        this.contentLen = contentLen;
        this.pageLen = pageLen;

        updateBarLength();

        bgColor = Color.argb(128,255,255,255);
        barColor = Color.argb(255, 255,128,0);

        updateSize(viewWidth, viewHeight);
    }

    /**
     * スクロールバーを表示する先のViewのサイズが変更された時の処理
     * @param viewW
     * @param viewH
     */
    public void updateSize(int viewW, int viewH) {
        switch (type) {
            case Top:
                pos.x = 0;
                bgLength = viewW;
                if (inOut == ScrollBarInOut.In) {
                    pos.y = 0;
                } else {
                    pos.y = -bgWidth;
                }
                break;
            case Bottom:
                pos.x = 0;
                bgLength = viewW;
                if (inOut == ScrollBarInOut.In) {
                    pos.y = viewH - bgWidth;
                } else {
                    pos.y = viewH;
                }
                break;
            case Left:
                pos.y = 0;
                bgLength = viewH;
                if (inOut == ScrollBarInOut.In) {
                    pos.x = 0;
                } else {
                    pos.x = -bgWidth;
                }
                break;
            case Right:
                pos.y = 0;
                bgLength = viewH;
                if (inOut == ScrollBarInOut.In) {
                    pos.x = viewW - bgWidth;
                } else {
                    pos.x = viewW;
                }
                break;
        }
        updateBarLength();
        if (barPos + barLength > bgLength) {
            barPos = bgLength - barLength;
        }
    }


    /**
     * 色を設定
     * @param bgColor  背景色
     * @param barColor バーの色
     */
    public void setColor(int bgColor, int barColor) {
        this.bgColor = bgColor;
        this.barColor = barColor;
    }

    /**
     * 領域がスクロールした時の処理
     * ※外部のスクロールを反映させる
     * @param topPos
     */
    public void updateScroll(PointL topPos) {
        long _pos = isVertical() ? topPos.y : topPos.x;
        barPos = (_pos / (float)contentLen) * bgLength;
        this.topPos = _pos;
    }

    public void updateBarPos() {
        barPos = (topPos / (float)contentLen) * bgLength;
    }

    /**
     * バーの位置を設定する
     */
    public long setBarPos(ScrollBarPos pos) {
        switch(pos) {
            case Top:
                topPos = 0;
                break;
            case Center:
                topPos = (contentLen - pageLen) / 2;
                break;
            case Bottom:
                topPos = contentLen - pageLen;
                break;
        }
        updateBarPos();
        return topPos;
    }

    /**
     * バーの座標からスクロール量を求める
     * updateScrollの逆バージョン
     */
    private void updateScrollByBarPos() {
        topPos = (long)((barPos / bgLength) * contentLen);
    }

    /**
     * コンテンツやViewのサイズが変更された時の処理
     */
    public float updateContent(SizeL contentSize) {
        if (isVertical()) {
            this.contentLen = contentSize.height;
        } else {
            this.contentLen = contentSize.width;
        }

        updateBarLength();
        return topPos;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (barLength == 0) return;

        paint.setStyle(Paint.Style.FILL);

        RectF bgRect = new RectF();
        RectF barRect = new RectF();

        float baseX = pos.x + parentPos.x;
        float baseY = pos.y + parentPos.y;

        // バーは一定以下の長さの場合はある程度の長さに補正する
        float _barPos = barPos;
        float _barLen = barLength;
        if (barLength < MIN_BAR_LEN) {
            _barPos = barPos - MIN_BAR_LEN / 2;
            _barLen = MIN_BAR_LEN;
        }

        if (isHorizontal()) {
            bgRect.left = baseX;
            bgRect.right = baseX + bgLength;
            bgRect.top = baseY;
            bgRect.bottom = baseY + bgWidth;

            barRect.left = baseX + _barPos;
            barRect.top = baseY + 10;
            barRect.right = baseX + _barPos + _barLen;
            barRect.bottom = baseY + bgWidth - 10;
        } else {
            bgRect.left = baseX;
            bgRect.top = baseY;
            bgRect.right = baseX + bgWidth;
            bgRect.bottom = baseY + bgLength;

            barRect.left = baseX + 10;
            barRect.top = baseY + _barPos;
            barRect.right = baseX + bgWidth - 10;
            barRect.bottom =baseY + _barPos + _barLen;
        }

        // 背景
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bgColor);
        canvas.drawRect(bgRect.left,
                bgRect.top,
                bgRect.right,
                bgRect.bottom,
                paint);

        // バー
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(barColor);
        canvas.drawRoundRect(barRect,
                10, 10,
                paint);
    }


    /**
     * １画面分上（前）にスクロール
     */
    public void scrollUp() {
        topPos -= pageLen;
        if (topPos < 0) {
            topPos = 0;
        }
        updateBarPos();
    }

    /**
     * １画面分下（先）にスクロール
     */
    public void scrollDown() {
        topPos += pageLen;
        if (topPos + pageLen > contentLen) {
            topPos = contentLen - pageLen;
        }
        updateBarPos();
    }

    /**
     * バーを移動
     * @param move 移動量
     */
    public void barMove(float move) {
        barPos += move;
        if (barPos < 0) {
            barPos = 0;
        }
        else if (barPos + barLength > bgLength) {
            barPos = bgLength - barLength;
        }

        updateScrollByBarPos();
    }

    /**
     * タッチ系の処理
     * @param tv
     * @return
     */
    public boolean touchEvent(ViewTouch tv) {
        switch(tv.type) {
            case Touch:
                if (touchDown(tv)) {
                    return true;
                }
                break;
            case Moving:
                if (touchMove(tv)) {
                    return true;
                }
                break;
            case MoveEnd:
                touchUp();
                break;
        }
        return false;
    }

    /**
     * スクロールバーのタッチ処理
     * @param vt
     * @return true:バーがスクロールした
     */
    private boolean touchDown(ViewTouch vt) {
        // スペース部分をタッチしたら１画面分スクロール
        float ex = vt.touchX() - parentPos.x;
        float ey = vt.touchY() - parentPos.y;

        float _barPos = barPos;
        float _barLen = barLength;
        if (barLength < MIN_BAR_LEN) {
            _barPos = barPos - MIN_BAR_LEN / 2;
            _barLen = MIN_BAR_LEN;
        }

        if (isVertical()) {
            if (pos.x <= ex && ex < pos.x + bgWidth &&
                    pos.y <= ey && ey < pos.y + bgLength)
            {
                if (ey < _barPos) {
                    // 上にスクロール
                    ULog.print(TAG, "Scroll Up");
                    scrollUp();
                    return true;
                } else if (ey > pos.y + _barPos + _barLen) {
                    // 下にスクロール
                    ULog.print(TAG, "Scroll Down");
                    scrollDown();
                    return true;
                } else {
                    // バー
                    ULog.print(TAG, "Drag Start");
                    isDraging = true;
                    return true;
                }
            }
        } else {
            if (pos.x <= ex && ex < pos.x + bgLength &&
                    pos.y <= ey && ey < pos.y + bgWidth)
            {
                if (ex < _barPos) {
                    // 上にスクロール
                    ULog.print(TAG, "Scroll Up");
                    scrollUp();
                    return true;
                } else if (ex > pos.x + _barPos + _barLen) {
                    // 下にスクロール
                    ULog.print(TAG, "Scroll Down");
                    scrollDown();
                    return true;
                } else {
                    // バー
                    ULog.print(TAG, "Drag Start");
                    isDraging = true;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean touchUp() {
        ULog.print(TAG, "touchUp");
        isDraging = false;

        return false;
    }

    private boolean touchMove(ViewTouch vt) {
        if (isDraging) {
            float move = isVertical() ? vt.moveY : vt.moveX;
            barMove(move);
            return true;
        }
        return false;
    }
}
