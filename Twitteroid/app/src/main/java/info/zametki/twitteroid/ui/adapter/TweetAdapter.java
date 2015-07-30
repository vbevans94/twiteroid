package info.zametki.twitteroid.ui.adapter;

import android.content.Context;
import android.view.View;

import com.alterplay.custompat.APBindableArrayAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import info.zametki.twitteroid.R;
import info.zametki.twitteroid.data.model.Tweet;
import info.zametki.twitteroid.ui.view.TweetItemView;

/**
 * Author vbevans94.
 */
public class TweetAdapter extends APBindableArrayAdapter<Tweet> {

    private final Picasso picasso;
    private long maxId = Long.MAX_VALUE;

    public TweetAdapter(Context context, Picasso picasso) {
        super(context, R.layout.item_tweet);
        this.picasso = picasso;
    }

    @Override
    public void appendLoaded(List<Tweet> items) {
        super.appendLoaded(items);

        updateMaxAndSince();
    }

    @Override
    public void replaceWith(List<Tweet> items) {
        super.replaceWith(items);

        updateMaxAndSince();
    }

    private void updateMaxAndSince() {
        for (Tweet tweet : getAll()) {
            maxId = Math.min(maxId, tweet.getId());
        }
    }

    public long getMaxId() {
        return maxId - 1;
    }

    @Override
    public void bindView(Tweet item, int position, View view) {
        TweetItemView itemView = (TweetItemView) view;
        itemView.bind(item, picasso);
    }
}
