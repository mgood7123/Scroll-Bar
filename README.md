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

# Example

here is an example of using ScrollBars
to make a non scrollable view, scrollable

```Java
// create a new view that will draw a red box
FrameLayout box = new FrameLayout(context, attrs) {
    Paint paintRed;

    {
        // we have stuff to draw
        setWillNotDraw(!true);
        paintRed = new Paint();
        paintRed.setColor(Color.argb(255, 255, 0, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw a 500x500 red box at location 500, 500
        canvas.drawRect(500, 500, 1000, 1000, paintRed);
    }
};

// set the box view's size to 4000x4000
box.setLayoutParams(new LayoutParams(4000, 4000));

// create a container for the box to sit in
FrameLayout container = new FrameLayout(context, attrs);

// add the box to the container
container.addView(box);

// this container will act as a window into the view
//
// the idea is that we do this:
//
//  ---------------------
// |         VIEW        |
// |                     |
// |     -----------     |
// |    | CONTAINER |    |
// |    |           |    |
// |    |           |    |
// |    |           |    |
// |    | CONTAINER |    |
// |     -----------     |
// |                     |
// |         VIEW        |
//  ---------------------
//
// here, the container is smaller than the view
// the container may be added to, and removed from, any view
//
// to scroll the contents of the container
// we do not move the container itself
// but instead move the contents of the container: VIEW
//
// by moving VIEW instead of CONTAINER, we ensure two things
//
// 1. the container's location in the current view is not modified
//    this is very important since do not want the container to move
//    around and causing other view's trouble
//
// 2. the container can be placed at any location
//    and the location of its contents will always remain
//    RELATIVE to the location of the view
//
//    for example:
//
//    if the container is at absolute location x=0; y=0
//    and the contents is at absolute location x=100, y=100
//
//    then if we move the container to absolute location x=40, y=40
//    then the contents will be at absolute location x=140, y=140
//    but the contents will still be at relative location x=100, y=100
//

// attach scroll bars to the container
horizontalScrollBar.attachTo(container);
verticalScrollBar.attachTo(container);

// add the container to your view however you wish
//
// IMPORTANT:
// if we where to instead attach scroll bars
// to linearLayoutHorizontal, it would behave in an
// unpredictable manner
// due to LinearLayoutHorizontal containing views
// other than our content
// and because we have not taught the scroll bars how
// to interact with a LinearLayout view, the scroll bars
// will treat the LinearLayout view as
// what ever the LinearLayout's top most supported view
// is, which is ViewGroup
linearLayoutHorizontal.addView(container, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
```

# View Registration

View Registration is a powerful tool
that allows the scroll bar to be compatible
with any View

Registering a view will teach the scroll bar
how it should interact with that view when it is attached
to that view

attaching a scrollbar to a view which is not registered
might result in unpredictable behaviour from the scroll bar
especially if the view is complex

this is not a requirement since the scroll bar
will treat its attached view as the top most supported view

for example
if you attach a scroll bar to an ImageView
then the scroll bar will treat the ImageView as a View
since View is implicitly registered and ImageView extends View

this may or may not produce correct behaviour depending
on how ImageView displays its contents


```Java
/**
 * Registers a view for use with the scroll bar
 * @param clazz the class that will be looked for, this is required
 * @param howToObtainTheViewSize how to obtain the view size, this is required
 * @param howToScrollTheView how to scroll the view, this is required
 * @param setScrollX how to set the view's scroll X, this is optional
 * @param setScrollY how to set the view's scroll Y, this is optional
 * @param getScrollX how to get the view's scroll X, this is optional
 * @param getScrollY how to get the view's scroll Y, this is optional
 * @param setupScrollTracking how the view's scrolling will be tracked, this is optional
 */
public void registerView(
        Class clazz,
        HowToObtainTheViewSize howToObtainTheViewSize,
        HowToScrollTheView howToScrollTheView,
        SetScroll setScrollX,
        SetScroll setScrollY,
        GetScroll getScrollX,
        GetScroll getScrollY,
        SetupScrollTracking setupScrollTracking
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
        defaultGetScrollY,
        defaultSetupScrollTracking
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
        (view) -> tmpY,
        (view) -> {
            ((RecyclerView) view).addOnScrollListener(
                    new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            updateRelativePosition(dx, dy);
                        }
                    };
            );
        }
);
```

with this, the scroll bar can now work with RecyclerView

do note, that some views such as `RecyclerView`, do NOT store its scroll position
in `getScrollX()`` and `getScrollY()`` and these will always return `0`

so we instead need to keep track of the current scroll position ourselves

additionally, `RecyclerView` does not support `absolute scrolling` via `scrollTo()`

absolute scrolling is the default that most view's support

if a view requires relative scrolling
then absolute scrolling can be accomplished by:

1. invoking `scrollBy(x,y) with the negative
value of the current scroll position, this will
cause the view to scroll to position 0

2. invoking `scrollBy(x,y)` with an absolute position

for example, the following is equivalent to `view.scrollTo(0, dest);`

```Java
// scroll absolute using relative scrolling
(view, orientation, srcX, srcY, dest) -> {
    if (orientation == VERTICAL) {
        // scroll to 0 on Y axis
        view.scrollBy(0, -srcY);
        // scroll to absolute position on Y axis
        view.scrollBy(0, dest);
    } else {
        // scroll to 0 on X axis
        view.scrollBy(-srcX, 0);
        // scroll to absolute position on X axis
        view.scrollBy(dest, 0);
    }
}
```