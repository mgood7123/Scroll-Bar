package smallville7123.UI.ScrollBarView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * a special view specifically designed for use with ScrollBar
 */
public class CanvasView extends CanvasViewBase {
    public CanvasView(@NonNull Context context) {
        this(context, null);
    }

    public CanvasView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CanvasView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
    }

    CanvasDrawer canvasDrawer = new CanvasDrawer();

    Canvas canvas = new Canvas();
    Bitmap bitmap;

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

    @Override
    final protected void onDraw(Canvas realCanvas) {
        super.onDraw(realCanvas);
        onDrawCanvas(canvasDrawer);
        realCanvas.drawBitmap(bitmap, -canvasDrawer.mScrollX, -canvasDrawer.mScrollY, null);
    }


    // DEPRECIATED

    /**
     * @deprecated please call {@link CanvasDrawer#setOnScrollChangeListener(CanvasDrawer.OnScrollChangeListener)} instead
     */
    @Override
    @Deprecated
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        throw new RuntimeException("please call canvasDrawer.setOnScrollChangeListener(CanvasDrawer.OnScrollChangeListener) instead");
    }

    /**
     * @deprecated please call {@link CanvasDrawer#setScrollX(int)} instead
     */
    @Override
    @Deprecated
    public void setScrollX(int value) {
        throw new RuntimeException("please call canvasDrawer.setScrollX(int) instead");
    }

    /**
     * @deprecated please call {@link CanvasDrawer#setScrollY(int)} instead
     */
    @Override
    @Deprecated
    public void setScrollY(int value) {
        throw new RuntimeException("please call canvasDrawer.setScrollY(int) instead");
    }

    /**
     * @deprecated please call {@link CanvasDrawer#scrollTo(int, int)} instead
     */
    @Override
    @Deprecated
    public void scrollTo(int x, int y) {
        throw new RuntimeException("please call canvasDrawer.scrollTo(int, int) instead");
    }

    /**
     * @deprecated please call {@link CanvasDrawer#scrollBy(int, int)} instead
     */
    @Override
    @Deprecated
    public void scrollBy(int x, int y) {
        throw new RuntimeException("please call canvasDrawer.scrollBy(int, int) instead");
    }

    /**
     * @deprecated please call {@link CanvasDrawer#onScrollChanged(int, int, int, int)} instead
     */
    @Override
    @Deprecated
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        throw new RuntimeException("please call canvasDrawer.onScrollChanged(int, int, int, int) instead");
    }

}
