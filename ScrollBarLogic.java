package smallville7123.UI.ScrollBarView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ScrollBarLogic {

    // methods

    public void init() {
        if (mOrientation == VERTICAL) {
            setThumbY(0);
            setThumbHeight(100);
        } else {
            setThumbX(0);
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

    void setThumbSize() {
        if (mOrientation == VERTICAL) {
            // this is exactly the same as
            // scrollBarHeight * (viewportHeight / totalPageHeight)
            float thumbHeight = scrollBarHeight / (documentHeight / windowHeight);
            setThumbHeight((int) thumbHeight);
            // offset for height
            computeThumbYPosition(thumbY, thumbHeight);
        } else {
            // this is exactly the same as
            // scrollBarWidth * (viewportWidth / totalPageWidth)
            float thumbWidth = scrollBarWidth / (documentWidth / windowWidth);
            setThumbWidth((int) thumbWidth);
            // offset for width
            computeThumbXPosition(thumbX, thumbWidth);
        }
    }

    void scrollThumb() {
        float multiplier;
        float scrollBarPosition;
        if (mOrientation == VERTICAL) {
            multiplier = documentScrollY / (documentHeight - windowHeight);
            scrollBarPosition = multiplier * (scrollBarHeight - thumbHeight);
            setThumbY(scrollBarPosition);
        } else {
            multiplier = documentScrollX / (documentWidth - windowWidth);
            scrollBarPosition = multiplier * (scrollBarWidth - thumbWidth);
            setThumbX(scrollBarPosition);
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
                setThumbY(thumbX);
                setThumbX(0);
            } else {
                setThumbX(thumbY);
                setThumbY(0);
            }
            setThumbWidth(100);
        }
    }

    public void computeThumbYPosition(float thumbY, float thumbHeight) {
        float scrollBarPosition = thumbY;
        if (scrollBarPosition <= 0) {
            setThumbY(0);
        } else {
            float thumbEnd = scrollBarPosition + thumbHeight;
            if (thumbEnd > scrollBarHeight) {
                scrollBarPosition = scrollBarPosition - (thumbEnd - scrollBarHeight);
            }
            setThumbY(scrollBarPosition);
        }
    }

    public void computeThumbXPosition(float thumbX, float thumbWidth) {
        float scrollBarPosition = thumbX;
        if (scrollBarPosition <= 0) {
            setThumbX(0);
        } else {
            float thumbEnd = scrollBarPosition + thumbWidth;
            if (thumbEnd > scrollBarWidth) {
                scrollBarPosition = scrollBarPosition - (thumbEnd - scrollBarWidth);
            }
            setThumbX(scrollBarPosition);
        }
    }

    // variables and setters
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

    interface FloatRunnable { void run(Float value); }

    IntegerRunnable         onScrollBarWidthChanged;
    IntegerRunnable         onScrollBarHeightChanged;
    FloatRunnable           onThumbXChanged;
    FloatRunnable           onThumbYChanged;
    IntegerRunnable         onThumbWidthChanged;
    IntegerRunnable         onThumbHeightChanged;
    IntegerRunnable         onWindowWidthChanged;
    IntegerRunnable         onWindowHeightChanged;
    IntegerRunnable         onDocumentWidthChanged;
    IntegerRunnable         onDocumentHeightChanged;
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

    public void setThumbX(float thumbX) {
        this.thumbX = thumbX;
        if (onThumbXChanged != null) {
            onThumbXChanged.run(thumbX);
        }
    }

    public void setThumbY(float thumbY) {
        this.thumbY = thumbY;
        if (onThumbYChanged != null) {
            onThumbYChanged.run(thumbY);
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

    public void setDocumentWidth(int documentWidth) {
        this.documentWidth = documentWidth;
        if (onDocumentWidthChanged != null) {
            onDocumentWidthChanged.run(documentWidth);
        }
    }

    public void setDocumentHeight(int documentHeight) {
        this.documentHeight = documentHeight;
        if (onDocumentHeightChanged != null) {
            onDocumentHeightChanged.run(documentHeight);
        }
    }
}
