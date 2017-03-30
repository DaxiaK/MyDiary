package com.kiminonawa.mydiary.entries.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.facebook.device.yearclass.YearClass;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

import java.util.Calendar;

//Ref: http://blog.csdn.net/hmg25/article/details/6419694
public class PageEffectView extends View {

    private CalendarFactory calendarFactory;

    private int mWidth;
    private int mHeight;
    private int mCornerX = 0; // 拖拽点对应的页脚
    private int mCornerY = 0;
    private Path mPath0;
    private Path mPath1;
    private Bitmap mCurPageBitmap = null; // this page
    private Bitmap mNextPageBitmap = null;
    private Canvas mCurrentPageCanvas, mNextPageCanvas;
    //This rect is to clip the over view shadow.
    private Rect calendarRect;

    private PointF mTouch = new PointF(); // Touch point
    private float initialTouchX; // start touch point
    private int minSize;
    private PointF mBezierStart1 = new PointF(); // 贝塞尔曲线起始点
    private PointF mBezierControl1 = new PointF(); // 贝塞尔曲线控制点
    private PointF mBeziervertex1 = new PointF(); // 贝塞尔曲线顶点
    private PointF mBezierEnd1 = new PointF(); // 贝塞尔曲线结束点

    private PointF mBezierStart2 = new PointF(); // 另一条贝塞尔曲线
    private PointF mBezierControl2 = new PointF();
    private PointF mBeziervertex2 = new PointF();
    private PointF mBezierEnd2 = new PointF();

    private float mMiddleX;
    private float mMiddleY;
    private float mDegrees;
    private float mTouchToCornerDis;
    private ColorMatrixColorFilter mColorMatrixFilter;
    private Matrix mMatrix;
    private float[] mMatrixArray = {0, 0, 0, 0, 0, 0, 0, 0, 1.0f};

    private boolean mIsRTandLB; // 是否属于右上左下
    private float mMaxLength;
    private int[] mBackShadowColors;
    private int[] mFrontShadowColors;
    private GradientDrawable mBackShadowDrawableLR;
    private GradientDrawable mBackShadowDrawableRL;
    private GradientDrawable mFolderShadowDrawableLR;
    private GradientDrawable mFolderShadowDrawableRL;

    private GradientDrawable mFrontShadowDrawableHBT;
    private GradientDrawable mFrontShadowDrawableHTB;
    private GradientDrawable mFrontShadowDrawableVLR;
    private GradientDrawable mFrontShadowDrawableVRL;

    private Paint mPaint;

    private Scroller mScroller;


    //Calendar lock
    private boolean isCalendarUpdated = false;

    public PageEffectView(Context context, Calendar calendar) {
        super(context);
        init(context, calendar);
    }

    private void init(Context context, Calendar calendar) {

        //In the Android 4.2 , LAYER_TYPE_SOFTWARE will cause some question
        int year = YearClass.get(context.getApplicationContext());
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1 ||
                year < YearClass.CLASS_2013) {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //View
        setScreen(context);
        createBitmaps();

        //Set calendar , this object should be created after w,h was set.
        calendarFactory = new CalendarFactory(context, calendar, mWidth, mHeight);

        //Page effect
        mPath0 = new Path();
        mPath1 = new Path();
        createDrawable();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

        ColorMatrix cm = new ColorMatrix();
        float array[] = {0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
                0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0};
        cm.set(array);
        mColorMatrixFilter = new ColorMatrixColorFilter(cm);
        mMatrix = new Matrix();
        mScroller = new Scroller(getContext());

        mTouch.x = 0.01f; // 不让x,y为0,否则在点计算时会有问题
        mTouch.y = 0.01f;

        //Set today text
        calendarFactory.onDraw(mCurrentPageCanvas);
    }


    private void setScreen(Context context) {
        mWidth = ScreenHelper.getScreenWidth(context);
        if (ChinaPhoneHelper.getDeviceStatusBarType() == ChinaPhoneHelper.OTHER) {
            mHeight =
                    (int) ((ScreenHelper.getScreenHeight(context) - ScreenHelper.getStatusBarHeight(context)
                            - context.getResources().getDimension(R.dimen.top_bar_height))
                            * 0.7);
        } else {
            mHeight =
                    (int) ((ScreenHelper.getScreenHeight(context) -
                            context.getResources().getDimension(R.dimen.top_bar_height))
                            * 0.7);
        }
        calendarRect = new Rect(0, 0, mWidth, mHeight);
        mMaxLength = (float) Math.hypot(mWidth, mHeight);
        minSize = mWidth / 10;
    }

    private void createBitmaps() {
        mCurPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        mCurrentPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
    }

    /**
     * Author : hmg25 Version: 1.0 Description : 计算拖拽点对应的拖拽脚
     */
    public void calcCornerXY(float x, float y) {
        if (x <= mWidth / 2)
            mCornerX = 0;
        else
            mCornerX = mWidth;
        if (y <= mHeight / 2)
            mCornerY = 0;
        else
            mCornerY = mHeight;
        if ((mCornerX == 0 && mCornerY == mHeight)
                || (mCornerX == mWidth && mCornerY == 0))
            mIsRTandLB = true;
        else
            mIsRTandLB = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return doTouchEvent(event);
    }

    public boolean doTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                isCalendarUpdated = false;
                //
                abortAnimation();
                calcCornerXY(event.getX(), event.getY());
                calendarFactory.onDraw(mCurrentPageCanvas);
                //
                mTouch.x = event.getX();
                mTouch.y = event.getY();
                initialTouchX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragOverMinSize(event.getX()) && !isCalendarUpdated) {
                    if (DragToRight()) {
                        calendarFactory.preDateDraw(mNextPageCanvas);
                    } else {
                        calendarFactory.nextDateDraw(mNextPageCanvas);
                    }
                    isCalendarUpdated = true;
                }
                mTouch.x = event.getX();
                mTouch.y = event.getY();
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (canDragOver() && isCalendarUpdated) {
                    startAnimation(1200);
                } else {
                    mTouch.x = mCornerX - 0.09f;
                    mTouch.y = mCornerY - 0.09f;
                }
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    /**
     * Author : hmg25 Version: 1.0 Description : 求解直线P1P2和直线P3P4的交点坐标
     */

    public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
        PointF CrossP = new PointF();
        // 二元函数通式： y=ax+b
        float a1 = (P2.y - P1.y) / (P2.x - P1.x);
        float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

        float a2 = (P4.y - P3.y) / (P4.x - P3.x);
        float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
        CrossP.x = (b2 - b1) / (a1 - a2);
        CrossP.y = a1 * CrossP.x + b1;
        return CrossP;
    }

    private void calcPoints() {
        mMiddleX = (mTouch.x + mCornerX) / 2;
        mMiddleY = (mTouch.y + mCornerY) / 2;
        mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
        mBezierControl1.y = mCornerY;
        mBezierControl2.x = mCornerX;
        mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

        // Log.i("TAG", "mTouchX  " + mTouch.x + "  mTouchY  " + mTouch.y);
        // Log.i("TAG", "mBezierControl1.x  " + mBezierControl1.x
        // + "  mBezierControl1.y  " + mBezierControl1.y);
        // Log.i("TAG", "mBezierControl2.x  " + mBezierControl2.x
        // + "  mBezierControl2.y  " + mBezierControl2.y);

        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
                / 2;
        mBezierStart1.y = mCornerY;

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (mTouch.x > 0 && mTouch.x < mWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
                if (mBezierStart1.x < 0)
                    mBezierStart1.x = mWidth - mBezierStart1.x;

                float f1 = Math.abs(mCornerX - mTouch.x);
                float f2 = mWidth * f1 / mBezierStart1.x;
                mTouch.x = Math.abs(mCornerX - f2);

                float f3 = Math.abs(mCornerX - mTouch.x)
                        * Math.abs(mCornerY - mTouch.y) / f1;
                mTouch.y = Math.abs(mCornerY - f3);

                mMiddleX = (mTouch.x + mCornerX) / 2;
                mMiddleY = (mTouch.y + mCornerY) / 2;

                mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                        * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
                mBezierControl1.y = mCornerY;

                mBezierControl2.x = mCornerX;
                mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                        * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);
                // Log.i("TAG", "mTouchX --> " + mTouch.x + "  mTouchY-->  "
                // + mTouch.y);
                // Log.i("TAG", "mBezierControl1.x--  " + mBezierControl1.x
                // + "  mBezierControl1.y -- " + mBezierControl1.y);
                // Log.i("TAG", "mBezierControl2.x -- " + mBezierControl2.x
                // + "  mBezierControl2.y -- " + mBezierControl2.y);
                mBezierStart1.x = mBezierControl1.x
                        - (mCornerX - mBezierControl1.x) / 2;
            }
        }
        mBezierStart2.x = mCornerX;
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
                / 2;

        mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
                (mTouch.y - mCornerY));

        mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
                mBezierStart2);
        mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
                mBezierStart2);

        // Log.i("TAG", "mBezierEnd1.x  " + mBezierEnd1.x + "  mBezierEnd1.y  "
        // + mBezierEnd1.y);
        // Log.i("TAG", "mBezierEnd2.x  " + mBezierEnd2.x + "  mBezierEnd2.y  "
        // + mBezierEnd2.y);

		/*
         * mBeziervertex1.x 推导
		 * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 化简等价于
		 * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
		 */
        mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
        mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
        mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
        mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;

    }

    private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
        mPath0.reset();
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
                mBezierEnd1.y);
        mPath0.lineTo(mTouch.x, mTouch.y);
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
                mBezierStart2.y);
        mPath0.lineTo(mCornerX, mCornerY);
        mPath0.close();

        canvas.save();
        canvas.clipPath(path, Region.Op.XOR);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();
    }

    private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
        mPath1.reset();
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.lineTo(mCornerX, mCornerY);
        mPath1.close();

        mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
                - mCornerX, mBezierControl2.y - mCornerY));
        int leftx;
        int rightx;
        GradientDrawable mBackShadowDrawable;
        if (mIsRTandLB) {
            leftx = (int) (mBezierStart1.x);
            rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
            mBackShadowDrawable = mBackShadowDrawableLR;
        } else {
            leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
            rightx = (int) mBezierStart1.x;
            mBackShadowDrawable = mBackShadowDrawableRL;
        }
        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
                (int) (mMaxLength + mBezierStart1.y));
        mBackShadowDrawable.draw(canvas);
        canvas.restore();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        calcPoints();
        drawCurrentPageArea(canvas, mCurPageBitmap, mPath0);
        drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
        drawCurrentPageShadow(canvas);
        drawCurrentBackArea(canvas, mCurPageBitmap);
    }

    /**
     * Author : hmg25 Version: 1.0 Description : 创建阴影的GradientDrawable
     */
    private void createDrawable() {
        int[] color = {0x333333, 0xb0333333};
        mFolderShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, color);
        mFolderShadowDrawableRL
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFolderShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, color);
        mFolderShadowDrawableLR
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowColors = new int[]{0xff111111, 0x111111};
        mBackShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowColors = new int[]{0x80111111, 0x111111};
        mFrontShadowDrawableVLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
        mFrontShadowDrawableVLR
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mFrontShadowDrawableVRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
        mFrontShadowDrawableVRL
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHTB = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
        mFrontShadowDrawableHTB
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHBT = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
        mFrontShadowDrawableHBT
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }

    /**
     * Author : hmg25 Version: 1.0 Description : 绘制翻起页的阴影
     */
    public void drawCurrentPageShadow(Canvas canvas) {
        double degree;
        if (mIsRTandLB) {
            degree = Math.PI
                    / 4
                    - Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x
                    - mBezierControl1.x);
        } else {
            degree = Math.PI
                    / 4
                    - Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x
                    - mBezierControl1.x);
        }
        // 翻起页阴影顶点与touch点的距离
        double d1 = (float) 25 * 1.414 * Math.cos(degree);
        double d2 = (float) 25 * 1.414 * Math.sin(degree);
        float x = (float) (mTouch.x + d1);
        float y;
        if (mIsRTandLB) {
            y = (float) (mTouch.y + d2);
        } else {
            y = (float) (mTouch.y - d2);
        }
        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.close();
        float rotateDegrees;
        canvas.save();

        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        int leftx;
        int rightx;
        GradientDrawable mCurrentPageShadow;
        if (mIsRTandLB) {
            leftx = (int) (mBezierControl1.x);
            rightx = (int) mBezierControl1.x + 25;
            mCurrentPageShadow = mFrontShadowDrawableVLR;
        } else {
            leftx = (int) (mBezierControl1.x - 25);
            rightx = (int) mBezierControl1.x + 1;
            mCurrentPageShadow = mFrontShadowDrawableVRL;
        }

        rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
                - mBezierControl1.x, mBezierControl1.y - mTouch.y));
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
        mCurrentPageShadow.setBounds(leftx,
                (int) (mBezierControl1.y - mMaxLength), rightx,
                (int) (mBezierControl1.y));
        mCurrentPageShadow.draw(canvas);
        canvas.restore();

        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.close();
        canvas.save();
        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        canvas.clipRect(calendarRect);
        if (mIsRTandLB) {
            leftx = (int) (mBezierControl2.y);
            rightx = (int) (mBezierControl2.y + 25);
            mCurrentPageShadow = mFrontShadowDrawableHTB;
        } else {
            leftx = (int) (mBezierControl2.y - 25);
            rightx = (int) (mBezierControl2.y + 1);
            mCurrentPageShadow = mFrontShadowDrawableHBT;
        }
        rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
                - mTouch.y, mBezierControl2.x - mTouch.x));
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
        float temp;
        if (mBezierControl2.y < 0) {
            temp = mBezierControl2.y - mHeight;
        } else {
            temp = mBezierControl2.y;
        }
        int hmg = (int) Math.hypot(mBezierControl2.x, temp);
        if (hmg > mMaxLength) {
            mCurrentPageShadow
                    .setBounds((int) (mBezierControl2.x - 25) - hmg, leftx,
                            (int) (mBezierControl2.x + mMaxLength) - hmg,
                            rightx);
        } else {
            mCurrentPageShadow.setBounds(
                    (int) (mBezierControl2.x - mMaxLength), leftx,
                    (int) (mBezierControl2.x), rightx);
        }

        // Log.i("TAG", "mBezierControl2.x   " + mBezierControl2.x
        // + "  mBezierControl2.y  " + mBezierControl2.y);
        mCurrentPageShadow.draw(canvas);
        canvas.restore();
    }

    /**
     * Author : hmg25 Version: 1.0 Description : 绘制翻起页背面
     */
    private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
        int i = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
        float f1 = Math.abs(i - mBezierControl1.x);
        int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
        float f2 = Math.abs(i1 - mBezierControl2.y);
        float f3 = Math.min(f1, f2);
        mPath1.reset();
        mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath1.close();
        GradientDrawable mFolderShadowDrawable;
        int left;
        int right;
        if (mIsRTandLB) {
            left = (int) (mBezierStart1.x - 1);
            right = (int) (mBezierStart1.x + f3 + 1);
            mFolderShadowDrawable = mFolderShadowDrawableLR;
        } else {
            left = (int) (mBezierStart1.x - f3 - 1);
            right = (int) (mBezierStart1.x + 1);
            mFolderShadowDrawable = mFolderShadowDrawableRL;
        }
        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);

        mPaint.setColorFilter(mColorMatrixFilter);

        float dis = (float) Math.hypot(mCornerX - mBezierControl1.x,
                mBezierControl2.y - mCornerY);
        float f8 = (mCornerX - mBezierControl1.x) / dis;
        float f9 = (mBezierControl2.y - mCornerY) / dis;
        mMatrixArray[0] = 1 - 2 * f9 * f9;
        mMatrixArray[1] = 2 * f8 * f9;
        mMatrixArray[3] = mMatrixArray[1];
        mMatrixArray[4] = 1 - 2 * f8 * f8;
        mMatrix.reset();
        mMatrix.setValues(mMatrixArray);
        mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
        mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        // canvas.drawBitmap(bitmap, mMatrix, null);
        mPaint.setColorFilter(null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
                (int) (mBezierStart1.y + mMaxLength));
        mFolderShadowDrawable.draw(canvas);
        canvas.restore();
    }

    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mTouch.x = x;
            mTouch.y = y;
            postInvalidate();
        }
    }

    private void startAnimation(int delayMillis) {
        int dx, dy;
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (mCornerX > 0) {
            dx = -(int) (mWidth + mTouch.x);
        } else {
            dx = (int) (mWidth - mTouch.x + mWidth);
        }
        if (mCornerY > 0) {
            dy = (int) (mHeight - mTouch.y);
        } else {
            dy = (int) (1 - mTouch.y); // 防止mTouch.y最终变为0
        }
        mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
                delayMillis);
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    /**
     * Check touch point is not in Corner
     *
     * @return
     */
    public boolean canDragOver() {
        if (mTouchToCornerDis > minSize) {
            return true;
        }
        return false;
    }

    public boolean isDragOverMinSize(float newX) {
        if (DragToRight()) {
            if ((newX - initialTouchX) > minSize) {
                return true;
            }
        } else {
            if ((initialTouchX - newX) > minSize) {
                return true;
            }
        }
        return false;
    }

    /**
     * Author : hmg25 Version: 1.0 Description : 是否从左边翻向右边
     */
    public boolean DragToRight() {
        if (mCornerX > 0) {
            return false;
        }
        return true;
    }

}
