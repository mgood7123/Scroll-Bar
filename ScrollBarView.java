package smallville7123.UI.ScrollBarView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.ColorInt;
import androidx.annotation.InspectableProperty;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import smallville7123.UI.TwoWayNestedScrollView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static smallville7123.UI.ScrollBarView.ScrollBarLogic.HORIZONTAL;
import static smallville7123.UI.ScrollBarView.ScrollBarLogic.VERTICAL;


public class ScrollBarView extends FrameLayout {
    ScrollBarLogic scrollBarLogic;

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

    Context mContext;
    AttributeSet mAttrs;
    FrameLayout content;

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mAttrs = attrs;

        // make scroll view match parent height
//        setFillViewport(true);

        FrameLayout frame = new FrameLayout(context, attrs);
        content = frame;
        frame.setLayoutParams(
                new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        );
        frame.setTag(Internal);
        addView(frame);
        scrollBarLogic = new ScrollBarLogic();
        clip = newClip(scrollBarLogic);
        clip.setColor(Color.LTGRAY);
        scrollBarLogic.init();
        content.addView(clip.content);
        setPaint();
    }

    /**
     * Should the scroll bar be horizontal (scrolling left and right)
     * or vertical (scrolling up and down).
     * @param orientation Pass {@link ScrollBarLogic#HORIZONTAL} or
     * {@link ScrollBarLogic#VERTICAL}. Default
     * value is {@link ScrollBarLogic#VERTICAL}.
     */
    public void setOrientation(@ScrollBarLogic.OrientationMode int orientation) {
        if (scrollBarLogic.getOrientation() != orientation) {
            scrollBarLogic.setOrientation(orientation);
            requestLayout();
        }
    }

    /**
     * Returns the current orientation.
     *
     * @return either {@link ScrollBarLogic#HORIZONTAL} or
     * {@link ScrollBarLogic#VERTICAL}
     */
    @ScrollBarLogic.OrientationMode
    @InspectableProperty(enumMapping = {
            @InspectableProperty.EnumEntry(value = HORIZONTAL, name = "horizontal"),
            @InspectableProperty.EnumEntry(value = VERTICAL, name = "vertical")
    })
    public int getOrientation() {
        return scrollBarLogic.getOrientation();
    }

    View document;
    boolean layout = false;


    public void attachTo(View document) {
        this.document = document;
        invalidate();
    }

    static boolean DEBUG = true;

    void getWindowSize() {
        if (scrollBarLogic.getOrientation() == VERTICAL) {
            scrollBarLogic.setWindowHeight(document.getHeight());
        } else {
            scrollBarLogic.setWindowWidth(document.getWidth());
        }
    }

    boolean getDocumentSizeType_RecyclerView() {
        if (document instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) document;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                View view = manager.getChildAt(firstVisibleItemPosition);
                if (view != null) {
                    if (scrollBarLogic.getOrientation() == VERTICAL) {
                        scrollBarLogic.setDocumentHeight(
                                view.getHeight() * manager.getItemCount()
                        );
                    } else {
                        scrollBarLogic.setDocumentWidth(
                                view.getWidth() * manager.getItemCount()
                        );
                    }
                }
            }
            return true;
        } else return false;
    }

    boolean getDocumentSizeType_TwoWayNestedScrollView() {
        if (document instanceof TwoWayNestedScrollView) {
            TwoWayNestedScrollView twoWayNestedScrollView = (TwoWayNestedScrollView) document;
            View child = twoWayNestedScrollView.getChildAt(0);
            if (scrollBarLogic.getOrientation() == VERTICAL) {
                scrollBarLogic.setDocumentHeight(child.getHeight());
            } else {
                scrollBarLogic.setDocumentWidth(child.getWidth());
            }
            return true;
        } else return false;
    }

    boolean getDocumentSizeType_HorizontalScrollView() {
        if (document instanceof HorizontalScrollView) {
            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) document;
            if (scrollBarLogic.getOrientation() == VERTICAL) {
                throw new RuntimeException("attempting to obtain a scroll view of invalid scrolling direction, needed vertical, got horizontal");
            }
            scrollBarLogic.setDocumentWidth(
                    horizontalScrollView.getChildAt(0).getWidth()
            );
            return true;
        } else return false;
    }

    boolean getDocumentSizeType_ScrollView() {
        if (document instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) document;
            if (scrollBarLogic.getOrientation() == HORIZONTAL) {
                throw new RuntimeException("attempting to obtain a scroll view of invalid scrolling direction, needed horizontal, got vertical");
            }
            scrollBarLogic.setDocumentHeight(
                    scrollView.getChildAt(0).getHeight()
            );
            return true;
        } else return false;
    }

    boolean getDocumentSize() {
        if (getDocumentSizeType_RecyclerView()) return true;
        if (getDocumentSizeType_TwoWayNestedScrollView()) return true;
        if (getDocumentSizeType_HorizontalScrollView()) return true;
        if (getDocumentSizeType_ScrollView()) return true;
        return false;
    }

    void scrollDocument() {
        scrolling = true;
        if (document instanceof RecyclerView) {
            if (scrollBarLogic.getOrientation() == VERTICAL) {
                document.scrollBy(0, -scrollBarLogic.documentScrollY);
                document.scrollBy(0, (int) scrollBarLogic.absoluteOffset);
            } else {
                document.scrollBy(-scrollBarLogic.documentScrollX, 0);
                document.scrollBy((int) scrollBarLogic.absoluteOffset, 0);
            }
        } else {
            if (scrollBarLogic.getOrientation() == VERTICAL) {
                document.scrollTo(document.getScrollX(), (int) scrollBarLogic.absoluteOffset);
            } else {
                document.scrollTo((int) scrollBarLogic.absoluteOffset, document.getScrollY());
            }
        }
        scrolling = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scrollBarLogic.setScrollBarWidth(getWidth());
        scrollBarLogic.setScrollBarHeight(getHeight());
        scrollBarLogic.adjustThumb();
        if (document != null) {
            getWindowSize();
            if (getDocumentSize()) {
                scrollBarLogic.setThumbSize();
                scrollBarLogic.computeDocumentScroll();
                scrollDocument();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged() called with: w = [" + w + "], h = [" + h + "], oldw = [" + oldw + "], oldh = [" + oldh + "]");
        super.onSizeChanged(w, h, oldw, oldh);
        invalidate();
    }

    class Clip {
        View content;

        Clip(View content) {
            this.content = content;
            scrollBarLogic.onThumbXChanged = this::setX;
            scrollBarLogic.onThumbYChanged = this::setY;
            scrollBarLogic.onThumbWidthChanged = this::setWidth;
            scrollBarLogic.onThumbHeightChanged = this::setHeight;
            if (scrollBarLogic.getOrientation() == VERTICAL) {
                scrollBarLogic.setThumbHeight(100);
            } else {
                scrollBarLogic.setThumbWidth(100);
            }
        }

        Clip(Context context, AttributeSet attrs, ScrollBarLogic scrollBarLogic) {
            this(new FrameLayout(context, attrs));
        }

        public void setColor(@ColorInt int color) {
            content.setBackgroundColor(color);
        }

        public void setX(float x) {
            Log.d(TAG, "setX() called with: x = [" + x + "]");
            ViewGroup.LayoutParams p = content.getLayoutParams();
            if (p != null) {
                if (p instanceof MarginLayoutParams) {
                    ((MarginLayoutParams) p).leftMargin = (int) x;
                    content.setLayoutParams(p);
                } else {
                    throw new RuntimeException("layout is not an instance of MarginLayoutParams");
                }
            } else {
                content.setLayoutParams(
                        new MarginLayoutParams(
                                MATCH_PARENT,
                                MATCH_PARENT
                        ) {
                            {
                                leftMargin = (int) x;
                            }
                        }
                );
            }
        }

        public float getX() {
            return content.getX();
        }

        public void setY(float y) {
            ViewGroup.LayoutParams p = content.getLayoutParams();
            if (p != null) {
                if (p instanceof MarginLayoutParams) {
                    ((MarginLayoutParams) p).topMargin = (int) y;
                    content.setLayoutParams(p);
                } else {
                    throw new RuntimeException("layout is not an instance of MarginLayoutParams");
                }
            } else {
                content.setLayoutParams(
                        new MarginLayoutParams(
                                MATCH_PARENT,
                                MATCH_PARENT
                        ) {
                            {
                                topMargin = (int) y;
                            }
                        }
                );
            }
        }

        public float getY() {
            return content.getY();
        }

        public void setWidth(int width) {
            ViewGroup.LayoutParams p = content.getLayoutParams();
            if (p != null) {
                p.width = width;
                content.setLayoutParams(p);
            } else {
                content.setLayoutParams(
                        new MarginLayoutParams(
                                width,
                                MATCH_PARENT
                        )
                );
            }
        }

        public int getWidth() {
            return content.getWidth();
        }

        public void setHeight(int height) {
            ViewGroup.LayoutParams p = content.getLayoutParams();
            if (p != null) {
                p.height = height;
                content.setLayoutParams(p);
            } else {
                content.setLayoutParams(
                        new MarginLayoutParams(
                                MATCH_PARENT,
                                height
                        )
                );
            }
        }

        public int getHeight() {
            return content.getHeight();
        }

        public ViewPropertyAnimator animate() {
            return content.animate();
        }
    }

    Clip newClip(ScrollBarLogic scrollBarLogic) {
        return new Clip(mContext, mAttrs, scrollBarLogic);
    };

    private static class Internal {}
    Internal Internal = new Internal();
    Clip clip;

    private static final String TAG = "ScrollBarView";

    private float relativeToViewX;
    private float relativeToViewY;

    boolean clipTouch = false;
    Clip touchedClip;
    float downDX;
    float downDY;
    float downRawX;
    float downRawY;
    float currentRawX;
    float currentRawY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (clip != null) {
                    boolean ret = onClipTouchEvent(clip, event);
                    if (ret) {
                        clipTouch = true;
                        touchedClip = clip;
                        return ret;
                    }
                }
                scrolling = true;
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                return clipTouch ? onClipTouchEvent(touchedClip, event) : super.onTouchEvent(event);
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (clipTouch) {
                    boolean ret = onClipTouchEvent(touchedClip, event);
                    clipTouch = false;
                    return ret;
                }
                scrolling = false;
                return super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public float touchZoneWidthLeft = 80.0f;
    public float touchZoneWidthLeftOffset = 80.0f;
    public float touchZoneWidthRight = 80.0f;
    public float touchZoneWidthRightOffset = 80.0f;
    public float touchZoneHeightTop = 80.0f;
    public float touchZoneHeightTopOffset = 80.0f;
    public float touchZoneHeightBottom = 80.0f;
    public float touchZoneHeightBottomOffset = 80.0f;

    Paint highlightPaint;
    Paint touchZonePaint;

    private void setPaint() {
        highlightPaint = new Paint();
        touchZonePaint = new Paint();

        highlightPaint.setARGB(200, 0, 0, 255);
        touchZonePaint.setARGB(160, 0, 90, 0);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        int height = getHeight();
        int width = getWidth();
        if (isResizing) {
            drawHighlight(canvas, width, height, highlightPaint);
        }
//        drawTouchZones(canvas, width, height, touchZonePaint);
    }

    void drawHighlight(Canvas canvas, int width, int height, Paint paint) {
        if (scrollBarLogic.getOrientation() == VERTICAL) {
            float clipStart = touchedClip.getY();
            float clipHeight = touchedClip.getHeight();
            float clipEnd = clipStart + clipHeight;
            canvas.drawRect(0, clipStart, width, clipEnd, paint);
        } else {
            float clipStart = touchedClip.getX();
            float clipWidth = touchedClip.getWidth();
            float clipEnd = clipStart + clipWidth;
            canvas.drawRect(clipStart, 0, clipEnd, height, paint);
        }
    }

    void drawTouchZones(Canvas canvas, int width, int height, Paint paint) {
        if (clip != null) {
            if (scrollBarLogic.getOrientation() == VERTICAL) {
                float clipStart = touchedClip.getY();
                float clipHeight = touchedClip.getHeight();
                float clipEnd = clipStart + clipHeight;
                // top
                canvas.drawRect(0, clipStart - touchZoneHeightTopOffset, width, (clipStart + touchZoneHeightTop) - touchZoneHeightTopOffset, paint);
                // bottom
                canvas.drawRect(0, (clipEnd - touchZoneHeightBottom) + touchZoneHeightBottomOffset, width, clipEnd + touchZoneHeightBottomOffset, paint);
            } else {
                float clipStart = touchedClip.getX();
                float clipWidth = touchedClip.getWidth();
                float clipEnd = clipStart + clipWidth;
                // left
                canvas.drawRect(clipStart - touchZoneWidthLeftOffset, 0, (clipStart + touchZoneWidthLeft) - touchZoneWidthLeftOffset, height, paint);
                // right
                canvas.drawRect((clipEnd - touchZoneWidthRight) + touchZoneWidthRightOffset, 0, clipEnd + touchZoneWidthRightOffset, height, paint);
            }
        }
    }

    boolean isResizing;
    boolean isDragging;
    float clipOriginalStartX;
    float clipOriginalStartY;
    float clipOriginalWidth;
    float clipOriginalHeight;
    float clipOriginalEndX;
    float clipOriginalEndY;
    boolean resizingLeft;
    boolean resizingRight;
    boolean resizingTop;
    boolean resizingBottom;

    boolean scrolling = false;

    public void updateRelativePosition(int dx, int dy) {
        scrollBarLogic.documentScrollX += dx;
        scrollBarLogic.documentScrollY += dy;
        if (!scrolling) scrollBarLogic.scrollThumb();
    }

    public void updateAbsolutePosition(int scrollX, int scrollY) {
        scrollBarLogic.documentScrollX = scrollX;
        scrollBarLogic.documentScrollY = scrollY;
        if (!scrolling) scrollBarLogic.scrollThumb();
    }

    public boolean onClipTouchEvent(Clip clip, MotionEvent event) {
        currentRawX = event.getRawX();
        currentRawY = event.getRawY();
        relativeToViewX = event.getX() + getScrollX();
        relativeToViewY = event.getY() + getScrollY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isResizing && isDragging) {
                    isDragging = false;
                    return true;
                } else if (isResizing && !isDragging) {
                    isResizing = false;
                    invalidate();
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!isResizing && isDragging) {
                    if (scrollBarLogic.getOrientation() == VERTICAL) {
                        scrollBarLogic.computeThumbYPosition(currentRawY + downDY, scrollBarLogic.getSavedThumbHeight());
                    } else {
                        scrollBarLogic.computeThumbXPosition(currentRawX + downDX, scrollBarLogic.getSavedThumbWidth());
                    }
                    return true;
                } else if (isResizing && !isDragging) {
//                    MarginLayoutParams layoutParams = (MarginLayoutParams) clip.content.getLayoutParams();
//                    if (resizingLeft) {
//                        float bounds = currentRawX + downDX;
//                        if (layoutParams.width > 0) {
//                            if (bounds > clipOriginalEndX) bounds = clipOriginalEndX;
//                            float newWidth = clipOriginalWidth - (bounds - clipOriginalStartX);
//                            if (newWidth < 1.0f) newWidth = 1.0f;
//                            clip.setX(bounds);
//                            clip.setWidth((int) newWidth);
//                        }
//                    } else if (resizingRight) {
//                        float bounds = currentRawX + downDX;
//                        if (layoutParams.width > 0) {
//                            float newWidth = clipOriginalWidth + (bounds - clipOriginalStartX);
//                            if (newWidth < 1.0f) newWidth = 1.0f;
//                            clip.setWidth((int) newWidth);
//                        }
//                    } else if (resizingTop) {
//                        float bounds = currentRawY + downDY;
//                        if (layoutParams.height > 0) {
//                            if (bounds > clipOriginalEndY) bounds = clipOriginalEndY;
//                            float newHeight = clipOriginalHeight - (bounds - clipOriginalStartY);
//                            if (newHeight < 1.0f) newHeight = 1.0f;
//                            clip.setY(bounds);
//                            clip.setHeight((int) newHeight);
//                        }
//                    } else if (resizingBottom) {
//                        float bounds = currentRawY + downDY;
//                        if (layoutParams.height > 0) {
//                            float newHeight = clipOriginalHeight + (bounds - clipOriginalStartY);
//                            if (newHeight < 1.0f) newHeight = 1.0f;
//                            clip.setHeight((int) newHeight);
//                        }
//                    }
                    return true;
                }
                return false;
            case MotionEvent.ACTION_DOWN:
                isDragging = false;
                isResizing = false;
                scrollBarLogic.saveThumbHeight();
                scrollBarLogic.saveThumbWidth();
                clipOriginalStartX = scrollBarLogic.getThumbX();
                clipOriginalStartY = scrollBarLogic.getThumbY();
                clipOriginalWidth = scrollBarLogic.getThumbWidth();
                clipOriginalHeight = scrollBarLogic.getThumbHeight();
                clipOriginalEndX = clipOriginalStartX + clipOriginalWidth;
                clipOriginalEndY = clipOriginalStartY + clipOriginalHeight;
                downRawX = currentRawX;
                downRawY = currentRawY;
                resizingLeft = false;
                resizingRight = false;
                resizingTop = false;
                resizingBottom = false;
                if (scrollBarLogic.getOrientation() == VERTICAL) {
                    float topStart = clipOriginalStartY - touchZoneHeightTopOffset;
                    float topEnd = (clipOriginalStartY + touchZoneHeightTop) - touchZoneHeightTopOffset;
                    float bottomStart = (clipOriginalEndY - touchZoneHeightBottom) + touchZoneHeightBottomOffset;
                    float bottomEnd = clipOriginalEndY + touchZoneHeightBottomOffset;
                    if (within(relativeToViewY, topStart, topEnd)) {
//                    resizingTop = true;
//                    isResizing = true;
                    } else if (within(relativeToViewY, bottomStart, bottomEnd)) {
//                    resizingBottom = true;
//                    isResizing = true;
                    } else if (within(relativeToViewY, clipOriginalStartY, clipOriginalEndY)) {
                        isDragging = true;
                    }
                } else {
                    float leftStart = clipOriginalStartX - touchZoneWidthLeftOffset;
                    float leftEnd = (clipOriginalStartX + touchZoneWidthLeft) - touchZoneWidthLeftOffset;
                    float rightStart = (clipOriginalEndX - touchZoneWidthRight) + touchZoneWidthRightOffset;
                    float rightEnd = clipOriginalEndX + touchZoneWidthRightOffset;
                    if (within(relativeToViewX, leftStart, leftEnd)) {
//                        resizingLeft = true;
//                        isResizing = true;
                    } else if (within(relativeToViewX, rightStart, rightEnd)) {
//                        resizingRight = true;
//                        isResizing = true;
                    } else if (within(relativeToViewX, clipOriginalStartX, clipOriginalEndX)) {
                        isDragging = true;
                    }
                }
                if (isResizing || isDragging) {
                    invalidate();
                    downDX = clipOriginalStartX - downRawX;
                    downDY = clipOriginalStartY - downRawY;
                    return true;
                }
            default:
                return false;
        }
    }

    boolean within(float point, float start, float end) {
        return point >= start && point <= end;
    }
}