package info.zametki.twitteroid.ui.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * Author vbevans94.
 */
public class BetterRefreshLayout extends SwipeRefreshLayout {

    public BetterRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        hookListView(this);
    }

    @Override
    public void setRefreshing(final boolean refreshing) {
        post(new Runnable() {
            @Override
            public void run() {
                BetterRefreshLayout.super.setRefreshing(refreshing);
            }
        });
    }

    /**
     * Hooks list views if they are indirect children. So that they could scroll normally.
     */
    private void hookListView(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof AbsListView) {
                ((AbsListView) view).setOnScrollListener(swipeListener());
            } else if (view instanceof ViewGroup) {
                hookListView((ViewGroup) view);
            }
        }
    }

    private AbsListView.OnScrollListener swipeListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (view == null || view.getChildCount() == 0) ? 0 : view.getChildAt(0).getTop();
                setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        };
    }
}
