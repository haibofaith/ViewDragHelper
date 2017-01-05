package com.simple.dragview;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by user on 2017/1/5.
 */

public class VDHLayout extends LinearLayout {
    private ViewDragHelper mDragger;
    //三个子View
    private View mDragView;
    private View mAutoBackView;
    private View mEdgeTrackerView;
    //获得点的位置
    private Point mAutoBackOriginPos = new Point();

    public VDHLayout(Context context) {
        super(context);
        init();
    }

    public VDHLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        /**
         * Factory method to create a new ViewDragHelper.
         * @param forParent drag操作的父布局
         * @param sensitivity 灵敏度，越大越灵敏
         * @param cb 触摸回调
         * @return a new ViewDragHelper instance
         */
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            //tryCaptureView如何返回ture则表示可以捕获该view，你可以根据传入的第一个view参数决定哪些可以捕获
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child==mDragView||child==mAutoBackView;
            }

            /**
             * clampViewPositionHorizontal,clampViewPositionVertical可以在该方法中对child移动的边界进行控制.
             * 在DragHelperCallback中实现clampViewPositionHorizontal方法,
             * 并且返回一个适当的数值就能实现横向拖动效果，
             * clampViewPositionHorizontal的第二个参数是指当前拖动子view应该到达的x坐标。
             * 所以按照常理这个方法原封返回第二个参数就可以了，但为了让被拖动的view遇到边界之后就不在拖动，
             * 对返回的值做了更多的考虑。
             * */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            //clampViewPositionHorizontal,clampViewPositionVertical可以在该方法中对child移动的边界进行控制
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            /**
             * 手指释放时会调用它，默认移动到当前位置。可实现手指释放时，回到原来位置
             * */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild == mAutoBackView)
                {
                    mDragger.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            //在边界拖动时回调,对应下面的边缘跟踪
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId)
            {
                mDragger.captureChildView(mEdgeTrackerView, pointerId);
            }
        });

        //为父视图的选定边缘启用边缘跟踪.
        mDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

    }

    //通过mDragger判断是否拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragger.shouldInterceptTouchEvent(ev);
    }

    //通过mDragger处理事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }
    //执行完滑动
    @Override
    public void computeScroll()
    {
        if(mDragger.continueSettling(true))
        {
            invalidate();
        }
    }

    //通过onlayout方法获取mAutoBackView坐标点的位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }
    //完成绘制布局的时候，给三个子View对象初始化
    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        mDragView = getChildAt(0);
        mAutoBackView = getChildAt(1);
        mEdgeTrackerView = getChildAt(2);
    }
}
