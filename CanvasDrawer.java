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

    private Scroller<CanvasDrawer> viewScroller = new Scroller<>(this);
    public Scroller<CanvasDrawer> getViewScroller() {
        return viewScroller;
    }

    public Paint paint;
    private Canvas canvas;
    int width = 0;
    int height = 0;
    int offsetX = 0;
    int offsetY = 0;

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
