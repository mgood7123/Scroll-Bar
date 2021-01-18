package smallville7123.UI.ScrollBarView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.view.View.OnScrollChangeListener;

import androidx.annotation.InspectableProperty;

public class CanvasDrawer extends Canvas {

    private static final String TAG = "CanvasDrawer";

    public Paint paint;
    private Canvas canvas;
    int width = 0;
    int height = 0;
    int mScrollY = 0;
    int mScrollX = 0;
    int offsetX = 0;
    int offsetY = 0;
    /**
     * Interface definition for a callback to be invoked when the scroll
     * X or Y positions of a view change.
     *
     * @see #setOnScrollChangeListener(OnScrollChangeListener)
     */
    public interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param c The CanvasDrawer whose scroll position has changed.
         * @param scrollX Current horizontal scroll origin.
         * @param scrollY Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(CanvasDrawer c, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }
    protected OnScrollChangeListener mOnScrollChangeListener;

    /**
     * Register a callback to be invoked when the scroll X or Y positions of
     * this view change.
     *
     * @param l The listener to notify when the scroll X or Y position changes.
     * @see #getScrollX()
     * @see #getScrollY()
     */
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        mOnScrollChangeListener = l;
    }

    /**
     * Set the horizontal scrolled position of your view.
     * @param value the x position to scroll to
     */
    public void setScrollX(int value) {
        scrollTo(value, mScrollY);
    }

    /**
     * Set the vertical scrolled position of your view.
     * @param value the y position to scroll to
     */
    public void setScrollY(int value) {
        scrollTo(mScrollX, value);
    }

    /**
     * Return the scrolled left position of this view. This is the left edge of
     * the displayed part of your view. You do not need to draw any pixels
     * farther left, since those are outside of the frame of your view on
     * screen.
     *
     * @return The left edge of the displayed part of your view, in pixels.
     */
    @InspectableProperty
    public final int getScrollX() {
        return mScrollX;
    }

    /**
     * Return the scrolled top position of this view. This is the top edge of
     * the displayed part of your view. You do not need to draw any pixels above
     * it, since those are outside of the frame of your view on screen.
     *
     * @return The top edge of the displayed part of your view, in pixels.
     */
    @InspectableProperty
    public final int getScrollY() {
        return mScrollY;
    }

    /**
     * Set the scrolled position of your view.
     * @param x the x position to scroll to
     * @param y the y position to scroll to
     */
    public void scrollTo(int x, int y) {
        if (mScrollX != x || mScrollY != y) {
            int oldX = mScrollX;
            int oldY = mScrollY;
            mScrollX = x;
            mScrollY = y;
            onScrollChanged(mScrollX, mScrollY, oldX, oldY);
        }
    }

    /**
     * Move the scrolled position of your view.
     * @param x the amount of pixels to scroll by horizontally
     * @param y the amount of pixels to scroll by vertically
     */
    public void scrollBy(int x, int y) {
        scrollTo(mScrollX + x, mScrollY + y);
    }

    /**
     * This is called in response to an internal scroll in this view (i.e., the
     * view scrolled its own contents). This is typically as a result of
     * {@link #scrollBy(int, int)} or {@link #scrollTo(int, int)} having been
     * called.
     *
     * @param l Current horizontal scroll origin.
     * @param t Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    @SuppressWarnings("SpellCheckingInspection")
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

        @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private void checkCanvas() {
        if (canvas == null) {
            throw new RuntimeException("cannot draw with no canvas");
        }
    }

    private void checkPaint() {
        if (paint == null) {
            throw new RuntimeException("cannot draw with no paint");
        }
    }

    private void check() {
        checkCanvas();
        checkPaint();
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        width = canvas.getWidth();
        height = canvas.getHeight();
    }

    public void setCanvas(Canvas canvas, int width, int height) {
        assert canvas.getWidth() == width;
        assert canvas.getHeight() == height;
        this.canvas = canvas;
        this.width = width;
        this.height = height;
    }

    public void drawRect(int x, int y, int w, int h) {
        check();
        canvas.drawRect(
                x + offsetX,
                y + offsetY,
                x + w + offsetX,
                y + h + offsetY,
                paint
        );
    }

    public void drawRect(int x, int y) {
        drawRect(x, y, width - x, height - y);
    }

    public void setOffset(int x, int y) {
        offsetX = x;
        Log.d(TAG, "offsetX = [" + (offsetX) + "]");;
        offsetY = y;
        Log.d(TAG, "offsetY = [" + (offsetY) + "]");
    }

    public final static Paint paintBlack = new Paint() {
        {
            setColor(0);
        }
    };

    public final static Paint paintRed = new Paint() {
        {
            setARGB(255, 255, 0, 0);
        }
    };

    public void clear() {
        clear(0);
    }

    public void clear(int color) {
        checkCanvas();
        canvas.drawColor(color, PorterDuff.Mode.CLEAR);
    }
}
