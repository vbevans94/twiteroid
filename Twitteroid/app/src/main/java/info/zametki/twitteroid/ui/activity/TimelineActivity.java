package info.zametki.twitteroid.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Component;
import info.zametki.twitteroid.AppComponent;
import info.zametki.twitteroid.MainApp;
import info.zametki.twitteroid.R;
import info.zametki.twitteroid.annotations.ActivityScope;
import info.zametki.twitteroid.controller.TwitterController;
import info.zametki.twitteroid.data.model.Tweet;
import info.zametki.twitteroid.ui.adapter.TweetAdapter;
import info.zametki.twitteroid.ui.fragment.ClearTweetsFragment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TimelineActivity extends AppCompatActivity implements Callback<List<Tweet>>, ClearTweetsFragment.ClearTweetsListener {

    @InjectView(R.id.list_tweets)
    ListView listTweets;

    private TweetAdapter adapter;
    private boolean appending;
    private HolderFragment holder;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FragmentManager manager = getSupportFragmentManager();
        holder = (HolderFragment) manager.findFragmentByTag(HolderFragment.TAG);

        if (holder == null) {
            holder = new HolderFragment();

            manager.beginTransaction().add(holder, HolderFragment.TAG).commit();
        }

        // inject dependencies in created instance
        if (holder.component == null) {
            holder.component = DaggerTimelineActivity_TwitterComponent.builder()
                    .appComponent(MainApp.get(this).appComponent())
                    .build();

            holder.component.inject(holder);
        }

        setContentView(R.layout.activity_timeline);

        ButterKnife.inject(this);

        adapter = new TweetAdapter(this, holder.picasso);
        listTweets.setAdapter(adapter);

        listTweets.setOnScrollListener(loadScrollListener);

        // maybe we rotated the device
        if (holder.items == null) {
            refreshData(false);
        } else {
            adapter.replaceWith(holder.items);
            restorePosition();
        }
    }

    private void restorePosition() {
        listTweets.setSelectionFromTop(holder.index, holder.top);
    }

    private void refreshData(boolean append) {
        // indicate state of loading
        setActionRefreshing(true);

        if (append) {
            // we need to append the list
            appending = true;
            holder.twitterController.timeline(adapter.getMaxId(), this);
        } else {
            // initial calls
            persistPosition();
            holder.twitterController.timeline(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshData(false);
                break;

            case R.id.action_clear:
                ClearTweetsFragment.show(getSupportFragmentManager());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClear() {
        holder.twitterController.removeOlderThanBeforeYesterday(this);
    }

    @Override
    public void success(List<Tweet> tweets, Response response) {
        adapter.replaceWith(tweets);
        if (!appending) {
            restorePosition();
        }
        appending = false;
        setActionRefreshing(false);
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(this, R.string.error_api, Toast.LENGTH_LONG).show();

        setActionRefreshing(false);
    }

    public void setActionRefreshing(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.collapseActionView();
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        holder.items = adapter.getAll();
        persistPosition();
    }

    private void persistPosition() {
        holder.index = listTweets.getFirstVisiblePosition();
        View v = listTweets.getChildAt(0);
        holder.top = (v == null) ? 0 : (v.getTop() - listTweets.getPaddingTop());
    }

    private final AbsListView.OnScrollListener loadScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view,
                                         int scrollState) {
            int threshold = 1;
            int count = listTweets.getCount();

            if (scrollState == SCROLL_STATE_IDLE) {
                if (!appending && listTweets.getLastVisiblePosition() >= count - threshold) {
                    // we need to append tweets to end
                    refreshData(true);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    };

    public static class HolderFragment extends Fragment {

        private static final String TAG = HolderFragment.class.getName();

        @Inject
        TwitterController twitterController;

        @Inject
        Picasso picasso;

        public TwitterComponent component;
        public List<Tweet> items;
        public int top;
        public int index;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setRetainInstance(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            twitterController.release();
        }
    }

    @ActivityScope
    @Component(dependencies = AppComponent.class)
    public interface TwitterComponent {

        void inject(HolderFragment fragment);
    }
}
