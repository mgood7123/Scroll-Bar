# A Scroll Bar designed for Android

based on my Processing 3 Scroll Bar - https://github.com/mgood7123/Processing-3-Scrollable-View/blob/main/ScrollBarView.pde

but with more features

# Basic Usage

creating a scroll bar is simple:

```Java
ScrollBarView scrollBar = findViewById(R.id.vertical_scroll_bar);

// Orientations:
//
// ScrollBarView.HORIZONTAL
// ScrollBarView.VERTICAL
//
// this controls which direction the scrollBar will scroll

scrollBar.setOrientation(ScrollBarView.VERTICAL);

// compatible Views:
//
// View
// ViewGroup
// ScrollView
// HorizontalScrollView
// RecyclerView
//

scrollBar.attachTo(scrollView);
```

# View Registration

View Registration is a powerful tool
that allows the scroll bar to be compatible
with any View

```Java
/**
 * Registers a view for use with the scroll bar
 * @param clazz the class that will be looked for
 * @param howToObtainTheViewSize how to obtain the view size
 * @param howToScrollTheView how to scroll the view
 * @param setScrollX how to set the view's scroll X
 * @param setScrollY how to set the view's scroll Y
 * @param getScrollX how to get the view's scroll X
 * @param getScrollY how to get the view's scroll Y
 */
public void registerView(
        Class clazz,
        HowToObtainTheViewSize howToObtainTheViewSize,
        HowToScrollTheView howToScrollTheView,
        SetScroll setScrollX,
        SetScroll setScrollY,
        GetScroll getScrollX,
        GetScroll getScrollY
);
```

an example of this is

```Java
myScrollBarView.registerView (
        View.class,
        (view, orientation) -> {
            if (orientation == VERTICAL) {
                return ScrollBarView.SUCCESS(view.getHeight());
            } else {
                return ScrollBarView.SUCCESS(view.getWidth());
            }
        },
        defaultHowToScrollTheView,
        defaultSetScrollX,
        defaultSetScrollY,
        defaultGetScrollX,
        defaultGetScrollY
);
```

this is done for you in ScrollBarViews constructor, along with ViewGroup, ScrollView, HorizontalScrollView, and RecyclerView

this is what the registration looks like for RecyclerView, which is a more complex class

```Java
myScrollBarView.registerView(
        RecyclerView.class,
        (view, mOrientation) -> {
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null) {
                if (layoutManager.getChildCount() != 0) {
                    View child = layoutManager.getChildAt(0);
                    if (child != null) {
                        int itemCount = layoutManager.getItemCount();
                        int size = 0;
                        if (mOrientation == VERTICAL) {
                            size = child.getHeight();
                        } else {
                            size = child.getWidth();
                        }
                        return SUCCESS(size * itemCount);
                    }
                }
            }
            return FAIL;
        },
        (view, orientation, srcX, srcY, dest) -> {
            if (orientation == VERTICAL) {
                view.scrollBy(0, -srcY);
                view.scrollBy(0, dest);
            } else {
                view.scrollBy(-srcX, 0);
                view.scrollBy(dest, 0);
            }
        },
        (view, value) -> tmpX = value,
        (view, value) -> tmpY = value,
        (view) -> tmpX,
        (view) -> tmpY
);
```

with this, the scroll bar can now work with RecyclerView

do note, that an additional step is required to be fully compatible with recyclerView:

```Java
recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        myScrollBarView.updateRelativePosition(dx, dy);
    }
});
```

this allows for the scroll bar thumb position's to update when the RecyclerView is scrolled directly via swiping