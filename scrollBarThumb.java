package smallville7123.UI.ScrollBarView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static smallville7123.UI.ScrollBarView.ScrollBarLogic.VERTICAL;

public class scrollBarThumb {
    private static final String TAG = "scrollBarThumb";
    View content;

    scrollBarThumb(View content, ScrollBarLogic scrollBarLogic) {
        this.content = content;
        scrollBarLogic.onThumbXChanged = (value, scroll) -> { if (scroll) setX(value); };
        scrollBarLogic.onThumbYChanged = (value, scroll) -> { if (scroll) setY(value); };
        scrollBarLogic.onThumbWidthChanged = this::setWidth;
        scrollBarLogic.onThumbHeightChanged = this::setHeight;
        if (scrollBarLogic.getOrientation() == VERTICAL) {
            scrollBarLogic.setThumbHeight(100);
        } else {
            scrollBarLogic.setThumbWidth(100);
        }
    }

    scrollBarThumb(Context context, AttributeSet attrs, ScrollBarLogic scrollBarLogic) {
        this(new FrameLayout(context, attrs), scrollBarLogic);
    }

    public void setColor(@ColorInt int color) {
        content.setBackgroundColor(color);
    }

    public void setX(float x) {
        Log.d(TAG, "setX() called with: x = [" + x + "]");
        ViewGroup.LayoutParams p = content.getLayoutParams();
        if (p != null) {
            if (p instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) p).leftMargin = (int) x;
                content.setLayoutParams(p);
            } else {
                throw new RuntimeException("layout is not an instance of MarginLayoutParams");
            }
        } else {
            content.setLayoutParams(
                    new ViewGroup.MarginLayoutParams(
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
            if (p instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) p).topMargin = (int) y;
                content.setLayoutParams(p);
            } else {
                throw new RuntimeException("layout is not an instance of MarginLayoutParams");
            }
        } else {
            content.setLayoutParams(
                    new ViewGroup.MarginLayoutParams(
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
                    new ViewGroup.MarginLayoutParams(
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
                    new ViewGroup.MarginLayoutParams(
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
