package smallville7123.UI.ScrollBarView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.InspectableProperty;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import smallville7123.UI.ScrollBarView.ChangeDetectors.Float_Detector;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ScrollBarView extends View {

    public static final Pair<Boolean, Integer> FAIL = new Pair<>(false, -1);

    public static Pair<Boolean, Integer> SUCCESS(int size) {
        return new Pair<>(true, size);
    }

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

    public ScrollBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    Paint paintGrey;
    Paint paintPurple;

    private void init(Context context, AttributeSet attrs) {

        // make sure we will draw
        setWillNotDraw(false);

        paintGrey = new Paint();
        paintGrey.setColor(Color.rgb(155, 155, 155));
        paintPurple = new Paint();
        paintPurple.setColor(Color.rgb(155, 155, 255));
        registerViews();
    }

    void registerViews() {

        registerView(
                RecyclerView.class,
                (view, orientation) -> {
                    RecyclerView recyclerView = (RecyclerView) view;
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager == null) return FAIL;
                    View child = layoutManager.getChildAt(0);
                    if (child == null) return FAIL;
                    int itemCount = layoutManager.getItemCount();
                    int size;
                    if (orientation == VERTICAL) {
                        size = child.getHeight();
                    } else {
                        size = child.getWidth();
                    }
                    return SUCCESS(size * itemCount);
                },
                (view, orientation, srcX, srcY, dest) -> {
                    if (orientation == VERTICAL) {
                        view.scrollBy(0, -srcY);
                        view.scrollBy(0, dest);
                    } else {
                        view.scrollBy(-srcX, 0);
                        view.scrollBy(dest, 0);
                    }
                },
                (view, value) -> viewportScrollX = value,
                (view, value) -> viewportScrollY = value,
                (view) -> viewportScrollX,
                (view) -> viewportScrollY
        );

        registerView(
                View.class,
                (view, orientation) -> {
                    int size;
                    if (orientation == VERTICAL) {
                        size = view.getHeight();
                    } else {
                        size = view.getWidth();
                    }
                    return SUCCESS(size);
                },
                defaultHowToScrollTheView,
                defaultSetScrollX,
                defaultSetScrollY,
                defaultGetScrollX,
                defaultGetScrollY
        );

        registerView(
                ViewGroup.class,
                (view, orientation) -> {
                    ViewGroup viewGroup = (ViewGroup) view;
                    View child = viewGroup.getChildAt(0);
                    if (child == null) return FAIL;
                    int size;
                    if (orientation == VERTICAL) {
                        size = child.getHeight();
                    } else {
                        size = child.getWidth();
                    }
                    return SUCCESS(size);
                },
                defaultHowToScrollTheView,
                defaultSetScrollX,
                defaultSetScrollY,
                defaultGetScrollX,
                defaultGetScrollY
        );

        registerView(
                ScrollView.class,
                (view, orientation) -> {
                    ScrollView scrollView = (ScrollView) view;
                    if (orientation == HORIZONTAL) {
                        throw new RuntimeException("attempting to obtain a scroll view of invalid scrolling direction, needed horizontal, got vertical");
                    }
                    View child = scrollView.getChildAt(0);
                    if (child == null) return FAIL;
                    return SUCCESS(child.getHeight());
                },
                defaultHowToScrollTheView,
                defaultSetScrollX,
                defaultSetScrollY,
                defaultGetScrollX,
                defaultGetScrollY
        );

        registerView(
                HorizontalScrollView.class,
                (view, orientation) -> {
                    HorizontalScrollView horizontalScrollView = (HorizontalScrollView) view;
                    if (orientation == VERTICAL) {
                        throw new RuntimeException("attempting to obtain a scroll view of invalid scrolling direction, needed vertical, got horizontal");
                    }
                    View child = horizontalScrollView.getChildAt(0);
                    if (child == null) return FAIL;
                    return SUCCESS(child.getWidth());
                },
                defaultHowToScrollTheView,
                defaultSetScrollX,
                defaultSetScrollY,
                defaultGetScrollX,
                defaultGetScrollY
        );

        registerView(
                CanvasView.class,
                (view, orientation) -> {
                    CanvasView canvasView = (CanvasView) view;
                    int size;
                    if (orientation == VERTICAL) {
                        size = canvasView.canvasDrawer.getHeight();
                    } else {
                        size = canvasView.canvasDrawer.getWidth();
                    }
                    return SUCCESS(size);
                },
                (view, orientation, srcX, srcY, dest) -> {
                    if (orientation == VERTICAL) {
                        ((CanvasView) view).canvasDrawer.getViewScroller().scrollTo(srcX, dest);
                    } else {
                        ((CanvasView) view).canvasDrawer.getViewScroller().scrollTo(dest, srcY);
                    }
                },
                (view, value) -> ((CanvasView) view).canvasDrawer.getViewScroller().setScrollX(value),
                (view, value) -> ((CanvasView) view).canvasDrawer.getViewScroller().setScrollY(value),
                view -> ((CanvasView) view).canvasDrawer.getViewScroller().getScrollX(),
                view -> ((CanvasView) view).canvasDrawer.getViewScroller().getScrollY()
        );
    }

    private void drawTrack(Canvas canvas, Paint paint) {
        canvas.drawRect(trackX, trackY, trackWidth + trackX, trackHeight + trackY, paint);
    }

    private void drawThumb(Canvas canvas, Paint paint) {
        canvas.drawRect(thumbX, thumbY, thumbWidth + thumbX, thumbHeight + thumbY, paint);
    }

    int getViewportScrollX() {
        if (viewport != null) {
            for (ViewInformation registeredView : registeredViews) {
                if (registeredView.clazz.isInstance(viewport)) {
                    return registeredView.getScrollX.run(viewport);
                }
            }
        }
        return 0;
    }

    int getViewportScrollY() {
        if (viewport != null) {
            for (ViewInformation registeredView : registeredViews) {
                if (registeredView.clazz.isInstance(viewport)) {
                    return registeredView.getScrollY.run(viewport);
                }
            }
        }
        return 0;
    }

    void setViewportScrollX(int value) {
        if (viewport != null) {
            for (ViewInformation registeredView : registeredViews) {
                if (registeredView.clazz.isInstance(viewport)) {
                    registeredView.setScrollX.run(viewport, value);
                    return;
                }
            }
        }
    }

    void setViewportScrollY(int value) {
        if (viewport != null) {
            for (ViewInformation registeredView : registeredViews) {
                if (registeredView.clazz.isInstance(viewport)) {
                    registeredView.setScrollY.run(viewport, value);
                    return;
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        synchronized (lock) {
            if (viewport == null) return;
            if (mOrientation == HORIZONTAL) viewportSize.set(viewport.getMeasuredWidth());
            else if (mOrientation == VERTICAL) viewportSize.set(viewport.getMeasuredHeight());
            if (viewportSize.get() == 0) {
                return;
            }
            if (mOrientation == HORIZONTAL) {
                viewportOffset.set(getViewportScrollX());
                getContentSize();
                if (viewportOffset.changed() || contentSize.changed()) {
                    computeLayout(getWidth(), getHeight());
                }
            } else if (mOrientation == VERTICAL) {
                viewportOffset.set(getViewportScrollY());
                getContentSize();

                if (viewportOffset.changed() || contentSize.changed()) {
                    computeLayout(getWidth(), getHeight());
                }
            }
            if (thumbSize < trackLength) {
                if (trackWidth != 0 && trackHeight != 0 && thumbWidth != 0 && thumbHeight != 0) {
                    drawTrack(canvas, paintGrey);
                    drawThumb(canvas, paintPurple);
                }
            }
        }
        invalidate();
    }

    int minimum = 10;
    int maximum = 10;
    Float_Detector viewportSize = new Float_Detector(0);
    Float_Detector viewportOffset = new Float_Detector(0);
    Float_Detector contentSize = new Float_Detector(0);
    float maximumContentOffset = 0;
    float trackLength = 0;
    int thumbSize = 0;
    boolean allowOverflow = false;

    public abstract class OverflowRunnable {
        abstract public void run(int value);
    }

    OverflowRunnable onOverflow;

    public void attachTo(View viewport) {
        this.viewport = viewport;
        invalidate();
    }

    /**
     * this specifies how the view's total size should be obtained
     * <br>
     * <br>
     * there is no such way of obtaining
     * the total size of every view
     * using the exact same method
     * <br>
     * <br>
     * this will be different for different
     * classes
     * <br>
     * <br>
     * return: SUCCESS(totalSize)
     * <br>
     * return this when the total
     * size of the view has been
     * found
     * <br>
     * <br>
     * return: FAIL
     * <br>
     * return this when the total
     * size of the view cannot be
     * found
     *
     */
    public interface HowToObtainTheViewSize {
        Pair<Boolean, Integer> run(View view, int orientation);
    }

    /**
     * this specifies how the view can be scrolled
     * <br>
     * <br>
     * there is no such way of scrolling
     * every view using the exact same method
     * <br>
     * <br>
     * this will be different for different
     * classes
     * <br>
     * <br>
     * a {@link #defaultHowToScrollTheView} is provided
     * that will work for most view's that scroll by
     * {@link View#setScrollX(int) setScrollX} and
     * {@link View#setScrollY(int) setScrollY}
     */
    public interface HowToScrollTheView {
        void run(
                View view,
                int orientation,
                int srcX,
                int srcY,
                int dest
        );
    }

    public static HowToScrollTheView defaultHowToScrollTheView = new HowToScrollTheView() {
        @Override
        public void run(
                View view,
                int orientation,
                int srcX,
                int srcY,
                int dest
        ) {
            if (orientation == VERTICAL) {
                view.scrollTo(srcX, dest);
            } else {
                view.scrollTo(dest, srcY);
            }
        }
    };

    /**
     * this specifies how the view's scroll location can be stored
     * <br>
     * <br>
     * there is no such way of setting the scroll location of
     * every view using the exact same method
     * <br>
     * <br>
     * this will be different for different
     * classes
     * <br>
     * <br>
     * a {@link #defaultSetScrollX} and {@link #defaultSetScrollY}
     * is provided that will work for most view's that scroll by
     * {@link View#setScrollX(int) setScrollX} and
     * {@link View#setScrollY(int) setScrollY}
     */
    public interface SetScroll {
        void run(View view, int value);
    }

    /**
     * this specifies how the view's scroll location can be obtained
     * <br>
     * <br>
     * there is no such way of obtaining the scroll location of
     * every view using the exact same method
     * <br>
     * <br>
     * this will be different for different
     * classes
     * <br>
     * <br>
     * a {@link #defaultGetScrollX} and {@link #defaultGetScrollY}
     * is provided that will work for most view's that scroll by
     * {@link View#getScrollX() getScrollX} and
     * {@link View#getScrollY() getScrollY}
     */
    public interface GetScroll {
        int run(View view);
    }

    static public SetScroll defaultSetScrollX = View::setScrollX;
    static public SetScroll defaultSetScrollY = View::setScrollY;
    static public GetScroll defaultGetScrollX = View::getScrollX;
    static public GetScroll defaultGetScrollY = View::getScrollY;

    static public final class ViewInformation {
        Class clazz;
        HowToObtainTheViewSize howToObtainTheViewSize;
        HowToScrollTheView howToScrollTheView;
        SetScroll setScrollX;
        SetScroll setScrollY;
        GetScroll getScrollX;
        GetScroll getScrollY;
        int depth;
    }

    ArrayList<ViewInformation> registeredViews = new ArrayList<>();

    /**
     * Registers a view for use with the scroll bar
     * @param clazz the class that will be looked for
     * @param howToObtainTheViewSize how to obtain the view size
     * @param howToScrollTheView how to scroll the view
     * @param setScrollX how to set the view's scroll X
     * @param setScrollY how to set the view's scroll Y
     * @param getScrollX how to get the view's scroll X
     * @param getScrollY how to get the view's scroll Y
     */
    public void registerView(
            Class clazz,
            HowToObtainTheViewSize howToObtainTheViewSize,
            HowToScrollTheView howToScrollTheView,
            SetScroll setScrollX,
            SetScroll setScrollY,
            GetScroll getScrollX,
            GetScroll getScrollY
    ) {
        if (clazz == null) {
            throw new RuntimeException("Cannot register a null class");
        }
        ViewInformation viewInformation = new ViewInformation();
        viewInformation.clazz = clazz;
        viewInformation.depth = -1;
        viewInformation.howToObtainTheViewSize = howToObtainTheViewSize;
        viewInformation.howToScrollTheView = howToScrollTheView;
        viewInformation.setScrollX = setScrollX;
        viewInformation.setScrollY = setScrollY;
        viewInformation.getScrollX = getScrollX;
        viewInformation.getScrollY = getScrollY;

        Class s1 = clazz;
        while (s1 != null) {
            viewInformation.depth++;
            s1 = s1.getSuperclass();
        }
        registeredViews.add(viewInformation);
        registeredViews.sort((o1, o2) -> Integer.compare(o2.depth, o1.depth));
    }

    boolean getContentSize() {
        if (viewport != null) {
            for (ViewInformation registeredView : registeredViews) {
                if (registeredView.clazz.isInstance(viewport)) {
                    Pair<Boolean, Integer> result = registeredView.howToObtainTheViewSize.run(viewport, mOrientation);
                    if (result.first) {
                        contentSize.set(result.second);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void scrollViewport(float absoluteOffset) {
        if (viewport != null) {
            for (ViewInformation registeredView : registeredViews) {
                if (registeredView.clazz.isInstance(viewport)) {
                    registeredView.howToScrollTheView.run(
                            viewport,
                            mOrientation,
                            getViewportScrollX(),
                            getViewportScrollY(),
                            (int) absoluteOffset
                    );
                    return;
                }
            }
        }
    }

    final Object lock = new Object();

    void computeLayout(int w, int h) {
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

        maximumContentOffset = contentSize.get()-viewportSize.get();
        thumbSize = (int) ((viewportSize.get()/contentSize.get()) * trackLength);
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
                        onOverflow.run((int) contentSize.get());
                    }
                }
            } else {
                if (viewport != null) {
                    viewportOffset.set(getViewportScrollX());
                    if (!allowOverflow && viewportOffset.get() > 0 && viewportOffset.get() > maximumContentOffset) {
                        float offset = viewportOffset.get() - maximumContentOffset;
                        viewportOffset.set(viewportOffset.get() - offset);
                        setViewportScrollX((int) viewportOffset.get());
                    }
                    thumbX = (int) (maximumThumbPosition * (viewportOffset.get() / maximumContentOffset));
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
                        onOverflow.run((int) contentSize.get());
                    }
                }
            } else {
                if (viewport != null) {
                    viewportOffset.set(getViewportScrollY());
                    if (!allowOverflow && viewportOffset.get() > 0 && viewportOffset.get() > maximumContentOffset) {
                        float offset = viewportOffset.get() - maximumContentOffset;
                        viewportOffset.set(viewportOffset.get() - offset);
                        setViewportScrollY((int) viewportOffset.get());
                    }
                    thumbY = (int) (maximumThumbPosition * (viewportOffset.get() / maximumContentOffset));
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        synchronized (lock) {
            if (viewport == null) return;
            if (mOrientation == HORIZONTAL) viewportSize.set(viewport.getMeasuredWidth());
            else if (mOrientation == VERTICAL) viewportSize.set(viewport.getMeasuredHeight());
            if (viewportSize.get() == 0) {
                return;
            }
            getContentSize();
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

    private static final String TAG = "ScrollBarView";

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
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
                        float absoluteOffset = multiplier * (contentSize.get() - viewportSize.get());
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