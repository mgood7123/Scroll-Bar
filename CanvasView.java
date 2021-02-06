package smallville7123.UI.ScrollBarView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * a special view specifically designed for use with ScrollBar
 */
public abstract class CanvasView extends CanvasViewBase {
    private final Canvas canvas = new Canvas();
    final CanvasDrawer canvasDrawer = new CanvasDrawer();
    private Bitmap bitmap;

    public CanvasView(@NonNull Context context) {
        this(context, null);
    }

    public CanvasView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    private Context savedContext;
    private AttributeSet savedAttributeSet;
    private int savedDefStyleAttr;
    private int savedDefStyleRes;

    public CanvasView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
        savedContext = context;
        savedAttributeSet = attrs;
        savedDefStyleAttr = defStyleAttr;
        savedDefStyleRes = defStyleRes;
    }

    boolean initRan = false;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!initRan) {
            init(savedContext, savedAttributeSet, savedDefStyleAttr, savedDefStyleRes);
            initRan = true;
        }
    }

    /**
     * this function runs during the first call to {@link #onSizeChanged(int, int, int, int)}
     * <br>
     * <br>
     * this ensures that the class is fully constructed before invoking the init function
     */
    abstract public void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes);

    final protected void createCanvas(int width, int height) {
        if (bitmap != null) bitmap.recycle();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        canvasDrawer.setCanvas(canvas, width, height);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    protected void onDrawCanvas(CanvasDrawer canvas) {
    }

    /**
     * @deprecated please call {@link #onDrawCanvas(CanvasDrawer)} instead
     */
    @Override
    @Deprecated
    final public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    /**
     * @deprecated please call {@link #onDrawCanvas(CanvasDrawer)} instead
     */
    @Override
    @Deprecated
    final protected void onDraw(Canvas realCanvas) {
        super.onDraw(realCanvas);
        onDrawCanvas(canvasDrawer);
        Scroller<CanvasDrawer> viewScroller = canvasDrawer.getViewScroller();
        realCanvas.drawBitmap(bitmap, -viewScroller.mScrollX, -viewScroller.mScrollY, null);
    }

    // DEPRECIATED

    /**
     * @deprecated please call
     * {@link
     *     Scroller#setOnScrollChangeListener(Scroller.OnScrollChangeListener)
     *     canvasDrawer.viewScroller.setOnScrollChangeListener(Scroller.OnScrollChangeListener)
     * }
     * instead
     */
    @Override
    @Deprecated
    public void setOnScrollChangeListener(View.OnScrollChangeListener l) {
        throw new RuntimeException("please call canvasDrawer.viewScroller.setOnScrollChangeListener(CanvasDrawer.OnScrollChangeListener) instead");
    }

    /**
     * @deprecated please call
     * {@link
     *     Scroller#setScrollX(int)
     *     canvasDrawer.viewScroller.setScrollX(int)
     * }
     * instead
     */
    @Override
    @Deprecated
    public void setScrollX(int value) {
        throw new RuntimeException("please call canvasDrawer.viewScroller.setScrollX(int) instead");
    }

    /**
     * @deprecated please call
     * {@link
     *     Scroller#setScrollY(int)
     *     canvasDrawer.viewScroller.setScrollY(int)
     * }
     * instead
     */
    @Override
    @Deprecated
    public void setScrollY(int value) {
        throw new RuntimeException("please call canvasDrawer.viewScroller.setScrollY(int) instead");
    }

    /**
     * @deprecated please call
     * {@link
     *     Scroller#scrollTo(int, int)
     *     canvasDrawer.viewScroller.scrollTo(int, int)
     * }
     * instead
     */
    @Override
    @Deprecated
    public void scrollTo(int x, int y) {
        throw new RuntimeException("please call canvasDrawer.viewScroller.scrollTo(int, int) instead");
    }

    /**
     * @deprecated please call
     * {@link
     *     Scroller#scrollBy(int, int)
     *     canvasDrawer.viewScroller.scrollBy(int, int)
     * }
     * instead
     */
    @Override
    @Deprecated
    public void scrollBy(int x, int y) {
        throw new RuntimeException("please call canvasDrawer.viewScroller.scrollBy(int, int) instead");
    }

    /**
     * @deprecated please call
     * {@link
     *     Scroller#onScrollChanged(int, int, int, int)
     *     canvasDrawer.viewScroller.onScrollChanged(int, int, int, int)
     * }
     * instead
     */
    @Override
    @Deprecated
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        throw new RuntimeException("please call canvasDrawer.viewScroller.onScrollChanged(int, int, int, int) instead");
    }

}
