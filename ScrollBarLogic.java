package smallville7123.UI.ScrollBarView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ScrollBarLogic {

    // methods

    public void init() {
        if (mOrientation == VERTICAL) {
            setThumbY(0, DO_SCROLL);
            setThumbHeight(100);
        } else {
            setThumbX(0, DO_SCROLL);
            setThumbWidth(100);
        }
    }

    void adjustThumb() {
        if (mOrientation == VERTICAL) {
            setThumbWidth(scrollBarWidth);
        } else {
            setThumbHeight(scrollBarHeight);
        }
    }

    // the thumb size is only set when:
    // 1. the document width and/or height changes
    // 2. the window width and/or height changes
    // 3. the scroll bar width and/or height changes

    void setThumbSize() {
        if (mOrientation == VERTICAL) {
            // this is exactly the same as
            // scrollBarHeight * (viewportHeight / totalPageHeight)
            float thumbHeight = scrollBarHeight / (documentHeight / windowHeight);
            setThumbHeight((int) thumbHeight);
            // offset for height
            computeThumbYPosition(thumbY, thumbHeight, DO_SCROLL);
        } else {
            // this is exactly the same as
            // scrollBarWidth * (viewportWidth / totalPageWidth)
            float thumbWidth = scrollBarWidth / (documentWidth / windowWidth);
            setThumbWidth((int) thumbWidth);
            // offset for width
            computeThumbXPosition(thumbX, thumbWidth, DO_SCROLL);
        }
    }

    void scrollThumb() {
        float multiplier;
        float scrollBarPosition;
        if (mOrientation == VERTICAL) {
            multiplier = documentScrollY / (documentHeight - windowHeight);
            scrollBarPosition = multiplier * (scrollBarHeight - thumbHeight);
            computeThumbYPosition(scrollBarPosition, thumbHeight, DO_SCROLL);
        } else {
            multiplier = documentScrollX / (documentWidth - windowWidth);
            scrollBarPosition = multiplier * (scrollBarWidth - thumbWidth);
            computeThumbXPosition(scrollBarPosition, thumbWidth, DO_SCROLL);
        }
    }

    public void computeDocumentScroll() {
        float multiplier;
        if (mOrientation == VERTICAL) {
            multiplier = thumbY / (scrollBarHeight - thumbHeight);
            absoluteOffset = multiplier * (documentHeight - windowHeight);
        } else {
            multiplier = thumbX / (scrollBarWidth - thumbWidth);
            absoluteOffset = multiplier * (documentWidth - windowWidth);
        }
    }

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
                setThumbY(thumbX, DO_SCROLL);
                setThumbX(0, DO_SCROLL);
            } else {
                setThumbX(thumbY, DO_SCROLL);
                setThumbY(0, DO_SCROLL);
            }
            setThumbWidth(100);
        }
    }

    public void computeThumbYPosition(float thumbY, float thumbHeight, boolean scroll) {
        float scrollBarPosition = thumbY;
        if (scrollBarPosition <= 0) {
            scrollBarPosition = 0;
        } else {
            float thumbEnd = scrollBarPosition + thumbHeight;
            if (thumbEnd > scrollBarHeight) {
                scrollBarPosition = scrollBarPosition - (thumbEnd - scrollBarHeight);
            }
        }
        setThumbY(scrollBarPosition, scroll);
    }

    public void computeThumbXPosition(float thumbX, float thumbWidth, boolean scroll) {
        float scrollBarPosition = thumbX;
        if (scrollBarPosition <= 0) {
            scrollBarPosition = 0;
        } else {
            float thumbEnd = scrollBarPosition + thumbWidth;
            if (thumbEnd > scrollBarWidth) {
                scrollBarPosition = scrollBarPosition - (thumbEnd - scrollBarWidth);
            }
        }
        setThumbX(scrollBarPosition, scroll);
    }


    // variables and setters

    public static final boolean DO_NOT_SCROLL = false;
    public static final boolean DO_SCROLL = true;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    static final int DEFAULT_ORIENTATION = VERTICAL;
    private int mOrientation = DEFAULT_ORIENTATION;
    /**
     * Returns the current orientation.
     *
     * @return either {@link #HORIZONTAL} or
     * {@link #VERTICAL}
     */
    @ScrollBarLogic.OrientationMode
    public int getOrientation() {
        return mOrientation;
    }

    interface IntegerRunnable { void run(int value); }

    interface FloatRunnable { void run(float value); }

    interface ScrollIntegerRunnable { void run(int value, boolean scroll); }
    interface ScrollFloatRunnable { void run(float value, boolean scroll); }

    IntegerRunnable         onScrollBarWidthChanged;
    IntegerRunnable         onScrollBarHeightChanged;
    ScrollFloatRunnable     onThumbXChanged;
    ScrollFloatRunnable     onThumbYChanged;
    IntegerRunnable         onThumbWidthChanged;
    IntegerRunnable         onThumbHeightChanged;
    IntegerRunnable         onWindowWidthChanged;
    IntegerRunnable         onWindowHeightChanged;
    FloatRunnable           onDocumentWidthChanged;
    FloatRunnable           onDocumentHeightChanged;
    private int             scrollBarWidth;
    private int             scrollBarHeight;
    private float           thumbX;
    private float           thumbY;
    private int             thumbWidth;
    private int             thumbWidthSaved;
    private int             thumbHeight;
    private int             thumbHeightSaved;
    private int             windowWidth;
    private int             windowHeight;
    int                     documentScrollX;
    int                     documentScrollY;
    private float           documentWidth;
    private float           documentHeight;
    float                   absoluteOffset;

    public float getThumbX() {
        return thumbX;
    }

    public float getThumbY() {
        return thumbY;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void saveThumbWidth() {
        thumbWidthSaved = thumbWidth;
    }

    public float getSavedThumbWidth() {
        return thumbWidthSaved;
    }

    public void saveThumbHeight() {
        thumbHeightSaved = thumbHeight;
    }

    public float getSavedThumbHeight() {
        return thumbHeightSaved;
    }

    public void setScrollBarWidth(int scrollBarWidth) {
        this.scrollBarWidth = scrollBarWidth;
        if (onScrollBarWidthChanged != null) {
            onScrollBarWidthChanged.run(scrollBarWidth);
        }
    }

    public void setScrollBarHeight(int scrollBarHeight) {
        this.scrollBarHeight = scrollBarHeight;
        if (onScrollBarHeightChanged != null) {
            onScrollBarHeightChanged.run(scrollBarHeight);
        }
    }

    public void setThumbX(float thumbX, boolean scroll) {
        this.thumbX = thumbX;
        if (onThumbXChanged != null && scroll) {
            onThumbXChanged.run(thumbX, scroll);
        }
    }

    public void setThumbY(float thumbY, boolean scroll) {
        this.thumbY = thumbY;
        if (onThumbYChanged != null && scroll) {
            onThumbYChanged.run(thumbY, scroll);
        }
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
        if (onThumbWidthChanged != null) {
            onThumbWidthChanged.run(thumbWidth);
        }
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
        if (onThumbHeightChanged != null) {
            onThumbHeightChanged.run(thumbHeight);
        }
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
        if (onWindowWidthChanged != null) {
            onWindowWidthChanged.run(windowWidth);
        }
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
        if (onWindowHeightChanged != null) {
            onWindowHeightChanged.run(windowHeight);
        }
    }

    public void setDocumentWidth(float documentWidth) {
        this.documentWidth = documentWidth;
        if (onDocumentWidthChanged != null) {
            onDocumentWidthChanged.run(documentWidth);
        }
    }

    public void setDocumentHeight(float documentHeight) {
        this.documentHeight = documentHeight;
        if (onDocumentHeightChanged != null) {
            onDocumentHeightChanged.run(documentHeight);
        }
    }
}
