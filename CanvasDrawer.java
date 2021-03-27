package smallville7123.UI.ScrollBarView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;

import java.util.Stack;

public class CanvasDrawer {

    private static final String TAG = "CanvasDrawer";

    private final Scroller<CanvasDrawer> viewScroller = new Scroller<>(this);
    public Scroller<CanvasDrawer> getViewScroller() {
        return viewScroller;
    }

    private Paint paint;
    private Canvas canvas;
    private int width = 0;
    private int height = 0;
    private int offsetX = 0;
    private int offsetY = 0;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
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

    public void drawRectAbsoluteLocation(int left, int top, int right, int bottom) {
        check();
        canvas.drawRect(
                left + offsetX,
                top + offsetY,
                right + offsetX,
                bottom + offsetY,
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
            setARGB(255, 0, 0, 0);
        }
    };

    public final static Paint paintRed = new Paint() {
        {
            setARGB(255, 255, 0, 0);
        }
    };

    public void clear() {
        clear(Color.BLACK);
    }

    /**
     * clears the canvas to the specified color
     * <br>
     * <br>
     * the alpha component is not modified
     */
    public void clear(int color) {
        checkCanvas();
        canvas.drawColor(color, PorterDuff.Mode.SRC);
    }

    Stack<Paint> paintStack = new Stack<>();

    public void savePaint() {
        paintStack.push(paint);
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void restorePaint() {
        paint = paintStack.pop();
    }
}
