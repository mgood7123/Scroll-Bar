package smallville7123.UI.ScrollBarView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CanvasViewBase extends FrameLayout {

    public CanvasViewBase(@NonNull Context context) {
        super(context);
    }

    public CanvasViewBase(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasViewBase(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CanvasViewBase(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    final public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    final protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    final protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    final public boolean willNotDraw() {
        return super.willNotDraw();
    }

    @Override
    final public void setWillNotDraw(boolean willNotDraw) {
        super.setWillNotDraw(willNotDraw);
    }

    @Override
    final protected boolean isChildrenDrawingOrderEnabled() {
        return super.isChildrenDrawingOrderEnabled();
    }

    @Override
    final protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    final public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
    }

    @Override
    final protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who);
    }

    @Override
    final protected int getChildDrawingOrder(int childCount, int drawingPosition) {
        return super.getChildDrawingOrder(childCount, drawingPosition);
    }

    @Override
    final protected int[] onCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    final protected void setChildrenDrawingOrderEnabled(boolean enabled) {
        super.setChildrenDrawingOrderEnabled(enabled);
    }

    @Override
    final protected boolean isChildrenDrawnWithCacheEnabled() {
        return super.isChildrenDrawnWithCacheEnabled();
    }

    @Override
    final protected void setChildrenDrawingCacheEnabled(boolean enabled) {
        super.setChildrenDrawingCacheEnabled(enabled);
    }

    @Override
    final protected void setChildrenDrawnWithCacheEnabled(boolean enabled) {
        super.setChildrenDrawnWithCacheEnabled(enabled);
    }

    @Override
    final public Bitmap getDrawingCache() {
        return super.getDrawingCache();
    }

    @Override
    final public Bitmap getDrawingCache(boolean autoScale) {
        return super.getDrawingCache(autoScale);
    }

    @Override
    final public boolean isAlwaysDrawnWithCacheEnabled() {
        return super.isAlwaysDrawnWithCacheEnabled();
    }

    @Override
    final public boolean isDrawingCacheEnabled() {
        return super.isDrawingCacheEnabled();
    }

    @Override
    final public boolean willNotCacheDrawing() {
        return super.willNotCacheDrawing();
    }

    @Override
    final public int getDrawingCacheBackgroundColor() {
        return super.getDrawingCacheBackgroundColor();
    }

    @Override
    final public int getDrawingCacheQuality() {
        return super.getDrawingCacheQuality();
    }

    @Override
    final public int getPersistentDrawingCache() {
        return super.getPersistentDrawingCache();
    }

    @Override
    final public void buildDrawingCache() {
        super.buildDrawingCache();
    }

    @Override
    final public void buildDrawingCache(boolean autoScale) {
        super.buildDrawingCache(autoScale);
    }

    @Override
    final public void destroyDrawingCache() {
        super.destroyDrawingCache();
    }

    @Override
    final public void setAlwaysDrawnWithCacheEnabled(boolean always) {
        super.setAlwaysDrawnWithCacheEnabled(always);
    }

    @Override
    final public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
    }

    @Override
    final public void setDrawingCacheBackgroundColor(int color) {
        super.setDrawingCacheBackgroundColor(color);
    }

    @Override
    final public void setDrawingCacheEnabled(boolean enabled) {
        super.setDrawingCacheEnabled(enabled);
    }

    @Override
    final public void setDrawingCacheQuality(int quality) {
        super.setDrawingCacheQuality(quality);
    }

    @Override
    final public void setPersistentDrawingCache(int drawingCacheToKeep) {
        super.setPersistentDrawingCache(drawingCacheToKeep);
    }

    @Override
    final public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
        super.setWillNotCacheDrawing(willNotCacheDrawing);
    }
}
