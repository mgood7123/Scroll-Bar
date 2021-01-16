package smallville7123.UI.ScrollBarView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.InspectableProperty;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicBoolean;

import smallville7123.UI.TwoWayNestedScrollView;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ScrollBarView extends FrameLayout {

    int trackX = 0, trackY = 0, trackWidth = 0, trackHeight = 0;
    int thumbX = 0, thumbY = 0, thumbWidth = 0, thumbHeight = 0;

    public View viewport;

    public ScrollBarView(Context context) {
        super(context);
        init(context, null);
    }

    public ScrollBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScrollBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    Paint paintGrey;
    Paint paintPurple;

    private void init(Context context, AttributeSet attrs) {
        paintGrey = new Paint();
        paintGrey.setColor(Color.rgb(155, 155, 155));
        paintPurple = new Paint();
        paintPurple.setColor(Color.rgb(155, 155, 255));
    }

    private void drawTrack(Canvas canvas, Paint paint) {
        canvas.drawRect(trackX, trackY, trackWidth + trackX, trackHeight + trackY, paint);
    }

    private void drawThumb(Canvas canvas, Paint paint) {
        canvas.drawRect(thumbX, thumbY, thumbWidth + thumbX, thumbHeight + thumbY, paint);
    }

    int getViewportScrollX() {
        if (viewport instanceof RecyclerView) {
            return viewportScrollX;
        } else {
            return viewport.getScrollX();
        }
    }

    int getViewportScrollY() {
        if (viewport instanceof RecyclerView) {
            return viewportScrollY;
        } else {
            return viewport.getScrollY();
        }
    }

    void setViewportScrollX(int value) {
        if (viewport instanceof RecyclerView) {
            viewportScrollX = value;
        } else {
            viewport.setScrollX(value);
        }
    }

    void setViewportScrollY(int value) {
        if (viewport instanceof RecyclerView) {
            viewportScrollY = value;
        } else {
            viewport.setScrollY(value);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (thumbSize < trackLength) {
            if (trackWidth != 0 && trackHeight != 0 && thumbWidth != 0 && thumbHeight != 0) {
                drawTrack(canvas, paintGrey);
                drawThumb(canvas, paintPurple);
            }
        }
    }

    int minimum = 10;
    int maximum = 10;
    float viewportSize = 0;
    float oldViewportSize = 0;
    float contentSize = 0;
    float maximumContentOffset = 0;
    float trackLength = 0;
    int thumbSize = 0;
    float viewportOffset = 0;
    boolean allowOverflow = false;

    public abstract class OverflowRunnable {
        abstract public void run(int value);
    }

    OverflowRunnable onOverflow;

    public void attachTo(View viewport) {
        this.viewport = viewport;
        invalidate();
    }

    boolean getContentSizeType_RecyclerView() {
        if (viewport instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) viewport;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null) {
                if (layoutManager.getChildCount() != 0) {
                    View view = layoutManager.getChildAt(0);
                    if (view != null) {
                        if (mOrientation == VERTICAL) {
                            contentSize = view.getHeight() * layoutManager.getItemCount();
                        } else {
                            contentSize = view.getWidth() * layoutManager.getItemCount();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean getContentSizeType_TwoWayNestedScrollView() {
        if (viewport instanceof TwoWayNestedScrollView) {
            TwoWayNestedScrollView twoWayNestedScrollView = (TwoWayNestedScrollView) viewport;
            if (twoWayNestedScrollView.getChildCount() != 0) {
                View child = twoWayNestedScrollView.getChildAt(0);
                if (child != null) {
                    if (mOrientation == VERTICAL) {
                        contentSize = child.getHeight();
                    } else {
                        contentSize = child.getWidth();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    boolean getContentSizeType_HorizontalScrollView() {
        if (viewport instanceof HorizontalScrollView) {
            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) viewport;
            if (mOrientation == VERTICAL) {
                throw new RuntimeException("attempting to obtain a scroll view of invalid scrolling direction, needed vertical, got horizontal");
            }
            if (horizontalScrollView.getChildCount() != 0) {
                View child = horizontalScrollView.getChildAt(0);
                if (child != null) {
                    contentSize = child.getWidth();
                    return true;
                }
            }
        }
        return false;
    }

    boolean getContentSizeType_ScrollView() {
        if (viewport instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) viewport;
            if (mOrientation == HORIZONTAL) {
                throw new RuntimeException("attempting to obtain a scroll view of invalid scrolling direction, needed horizontal, got vertical");
            }
            if (scrollView.getChildCount() != 0) {
                View child = scrollView.getChildAt(0);
                if (child != null) {
                    contentSize = child.getWidth();
                    return true;
                }
            }
        }
        return false;
    }

    boolean getContentSize() {
        if (viewport != null) {
            // `else` and `else if` are not used here to keep it easier to add new items
            if (getContentSizeType_RecyclerView()) return true;
            if (getContentSizeType_TwoWayNestedScrollView()) return true;
            if (getContentSizeType_HorizontalScrollView()) return true;
            if (getContentSizeType_ScrollView()) return true;
        }
        return false;
    }

    Thread thread;
    AtomicBoolean monitor = new AtomicBoolean(false);
    final Object lock = new Object();

    void stopThread() {
        if (thread != null) {
            monitor.set(false);
            while (true) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    continue;
                }
                break;
            }
            thread = null;
        }
    }

    void createThread() {
        if (thread != null) stopThread();
        monitor.set(true);
        thread = new Thread(() -> {
            float oldViewportOffset = 0;
            float oldContentSize = 0;
            while (monitor.get()) {
                if (mOrientation == HORIZONTAL && viewport != null) {
                    synchronized (lock) {
                        oldViewportOffset = viewportOffset;
                        viewportOffset = getViewportScrollX();

                        oldContentSize = contentSize;
                        getContentSize();

                        if (viewportOffset != oldViewportOffset || contentSize != oldContentSize) {
                            computeLayout(getWidth(), getHeight());
                            postInvalidate();
                        }
                    }
                } else if (mOrientation == VERTICAL && viewport != null) {
                    synchronized (lock) {
                        oldViewportOffset = viewportOffset;
                        viewportOffset = getViewportScrollY();

                        oldContentSize = contentSize;
                        getContentSize();

                        if (viewportOffset != oldViewportOffset || contentSize != oldContentSize) {
                            computeLayout(getWidth(), getHeight());
                            postInvalidate();
                        }
                    }
                }
                while(true) {
                    try {
                        Thread.sleep(0, 1);
                    } catch (InterruptedException e) {
                        continue;
                    }
                    break;
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        switch (visibility) {
            case VISIBLE:
                createThread();
                break;
            case View.INVISIBLE:
            case View.GONE:
                stopThread();
                break;
        }
    }

    void computeLayout(int w, int h) {
        if (viewport == null) return;
        oldViewportSize = viewportSize;

        if (mOrientation == HORIZONTAL) viewportSize = viewport.getMeasuredWidth();
        else if (mOrientation == VERTICAL) viewportSize = viewport.getMeasuredHeight();
        if (viewportSize == 0) {
            return;
        }
        getContentSize();

        if (mOrientation == HORIZONTAL) {
            maximum = w;
            trackLength = w;
            trackWidth = w;
            trackHeight = h;
            thumbHeight = h;
        } else {
            maximum = h;
            trackLength = h;
            trackHeight = h;
            trackWidth = w;
            thumbWidth = w;
        }

        maximumContentOffset = contentSize-viewportSize;
        thumbSize = (int) ((viewportSize/contentSize) * trackLength);
        thumbSize = max(thumbSize, minimum);
        thumbSize = min(thumbSize, maximum);
        float maximumThumbPosition = trackLength-thumbSize;

        if (mOrientation == HORIZONTAL) {
            thumbWidth = thumbSize;
            if (maximumContentOffset == 0) {
                thumbX = 0;
            } else if (maximumContentOffset < 0) {
                thumbX = 0;
                if (!allowOverflow) {
                    if (onOverflow != null) {
                        onOverflow.run((int) contentSize);
                    }
                }
            } else {
                if (viewport != null) {
                    viewportOffset = getViewportScrollX();
                    if (!allowOverflow && viewportOffset > 0 && viewportOffset > maximumContentOffset) {
                        float offset = viewportOffset - maximumContentOffset;
                        viewportOffset -= offset;
                        setViewportScrollX((int) viewportOffset);
                    }
                    thumbX = (int) (maximumThumbPosition * (viewportOffset / maximumContentOffset));
                }
            }
        } else {
            thumbHeight = thumbSize;
            if (maximumContentOffset == 0) {
                thumbY = 0;
            } else if (maximumContentOffset < 0) {
                thumbY = 0;
                if (!allowOverflow) {
                    if (onOverflow != null) {
                        onOverflow.run((int) contentSize);
                    }
                }
            } else {
                if (viewport != null) {
                    viewportOffset = getViewportScrollY();
                    if (!allowOverflow && viewportOffset > 0 && viewportOffset > maximumContentOffset) {
                        float offset = viewportOffset - maximumContentOffset;
                        viewportOffset -= offset;
                        setViewportScrollY((int) viewportOffset);
                    }
                    thumbY = (int) (maximumThumbPosition * (viewportOffset / maximumContentOffset));
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        synchronized (lock) {
            computeLayout(w, h);
        }
    }

    boolean dragging = false;
    float xOffset;
    float yOffset;

    int viewportScrollX;
    int viewportScrollY;

    public void updateRelativePosition(int dx, int dy) {
        setViewportScrollX(getViewportScrollX() + dx);
        setViewportScrollY(getViewportScrollY() + dy);
    }

    public void updateAbsolutePosition(int scrollX, int scrollY) {
        setViewportScrollX(scrollX);
        setViewportScrollY(scrollY);
    }

    void scrollViewport(float absoluteOffset) {
        if (viewport instanceof RecyclerView) {
            if (mOrientation == VERTICAL) {
                viewport.scrollBy(0, -getViewportScrollY());
                viewport.scrollBy(0, (int) absoluteOffset);
            } else {
                viewport.scrollBy(-getViewportScrollX(), 0);
                viewport.scrollBy((int) absoluteOffset, 0);
            }
        } else {
            if (mOrientation == VERTICAL) {
                viewport.scrollTo(getViewportScrollX(), (int) absoluteOffset);
            } else {
                viewport.scrollTo((int) absoluteOffset, getViewportScrollY());
            }
        }
    }

    private static final String TAG = "ScrollBarView";

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        Log.d(TAG, "MotionEvent.actionToString(action) = [" + (MotionEvent.actionToString(action)) + "]");
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mOrientation == HORIZONTAL) {
                    synchronized (lock) {
                        if (event.getX() > thumbX && event.getX() < (thumbX + thumbWidth)) {
                            dragging = true;
                            xOffset = event.getX() - thumbX;
                        } else {
                            dragging = false;
                        }
                    }
                } else {
                    synchronized (lock) {
                        if (event.getY() > thumbY && event.getY() < (thumbY + thumbHeight)) {
                            dragging = true;
                            yOffset = event.getY() - thumbY;
                        } else {
                            dragging = false;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragging) {
                    synchronized (lock) {
                        float multiplier = 0;
                        if (mOrientation == HORIZONTAL) {
                            thumbX = (int) (event.getX() - xOffset);
                            if (thumbX < 0) {
                                thumbX = 0;
                            } else {
                                int x = getWidth() - thumbWidth;
                                if (thumbX > x) {
                                    thumbX = x;
                                }
                            }
                            invalidate();
                            multiplier = (float)thumbX / (float) (trackWidth - thumbWidth);
                        } else {
                            thumbY = (int) (event.getY() - yOffset);
                            if (thumbY < 0) {
                                thumbY = 0;
                            } else {
                                int y = getHeight() - thumbHeight;
                                if (thumbY > y) {
                                    thumbY = y;
                                }
                            }
                            invalidate();
                            multiplier = (float)thumbY / (float) (trackHeight - thumbHeight);
                        }
                        getContentSize();
                        float documentWidth = contentSize;
                        float absoluteOffset = multiplier * (documentWidth - viewportSize);
                        scrollViewport(absoluteOffset);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                dragging = false;
                break;
        }
        return true;
    }

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    static final int DEFAULT_ORIENTATION = VERTICAL;
    private int mOrientation = DEFAULT_ORIENTATION;

    /**
     * Should the scroll bar be horizontal (scrolling left and right)
     * or vertical (scrolling up and down).
     * @param orientation Pass {@link #HORIZONTAL} or {@link #VERTICAL}. Default
     * value is {@link #VERTICAL}.
     */
    public void setOrientation(@OrientationMode int orientation) {
        if (mOrientation != orientation) {
            mOrientation = orientation;
            if (mOrientation == VERTICAL) {
                // nothing to do here
            } else {
                // nothing to do here
            }
            requestLayout();
        }
    }

    /**
     * Returns the current orientation.
     *
     * @return either {@link #HORIZONTAL} or
     * {@link #VERTICAL}
     */
    @OrientationMode
    @InspectableProperty(enumMapping = {
            @InspectableProperty.EnumEntry(value = HORIZONTAL, name = "horizontal"),
            @InspectableProperty.EnumEntry(value = VERTICAL, name = "vertical")
    })
    public int getOrientation() {
        return mOrientation;
    }
}