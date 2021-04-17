package smallville7123.AndroidDAW.SDK.UI.ScrollBar;

import androidx.annotation.InspectableProperty;

public class Scroller <T> {
    T onScrollChangeObject;
    public int mScrollY = 0;
    public int mScrollX = 0;

    Scroller(T onScrollChangeObject) {
        this.onScrollChangeObject = onScrollChangeObject;
    }

    /**
     * Interface definition for a callback to be invoked when the scroll
     * X or Y positions of a view change.
     *
     * @see #setOnScrollChangeListener(OnScrollChangeListener)
     */
    public interface OnScrollChangeListener<T> {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param c          The Object whose scroll position has changed.
         * @param scrollX    Current horizontal scroll origin.
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(T c, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    private OnScrollChangeListener<T> mOnScrollChangeListener;

    /**
     * Register a callback to be invoked when the scroll X or Y positions of
     * this view change.
     *
     * @param l The listener to notify when the scroll X or Y position changes.
     * @see #getScrollX()
     * @see #getScrollY()
     */
    public void setOnScrollChangeListener(OnScrollChangeListener<T> l) {
        mOnScrollChangeListener = l;
    }

    /**
     * @return the current scroll listener
     */
    public OnScrollChangeListener<T> getOnScrollChangeListener() {
        return mOnScrollChangeListener;
    }

    /**
     * Set the horizontal scrolled position of your view.
     *
     * @param value the x position to scroll to
     */
    public void setScrollX(int value) {
        scrollTo(value, mScrollY);
    }

    /**
     * Set the vertical scrolled position of your view.
     *
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
     *
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
     *
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
     * @param l    Current horizontal scroll origin.
     * @param t    Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    @SuppressWarnings("SpellCheckingInspection")
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChange(onScrollChangeObject, l, t, oldl, oldt);
        }
    }
}
