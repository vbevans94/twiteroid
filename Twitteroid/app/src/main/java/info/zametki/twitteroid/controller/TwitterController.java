package info.zametki.twitteroid.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import info.zametki.twitteroid.annotations.ActivityScope;
import info.zametki.twitteroid.data.ConnectionInfo;
import info.zametki.twitteroid.data.apis.TwitterApi;
import info.zametki.twitteroid.data.model.Tweet;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Author vbevans94.
 */
@ActivityScope
public class TwitterController {

    private final TwitterApi twitterApi;
    private final ConnectionInfo connectionInfo;
    private final Realm realm;

    @Inject
    TwitterController(TwitterApi twitterApi, ConnectionInfo connectionInfo) {
        this.twitterApi = twitterApi;
        this.connectionInfo = connectionInfo;
        this.realm = Realm.getDefaultInstance();
    }

    public void timeline(Callback<List<Tweet>> callback) {
        if (connectionInfo.isNetworkAvailable()) {
            twitterApi.timeline(new SaveWrapper(callback));
        } else {
            getFromDatabase(callback);
        }
    }

    public void timeline(long maxId, Callback<List<Tweet>> callback) {
        if (connectionInfo.isNetworkAvailable()) {
            twitterApi.timeline(maxId, new SaveWrapper(callback));
        } else {
            getFromDatabase(callback);
        }
    }

    public void release() {
        realm.close();
    }

    public void getFromDatabase(Callback<List<Tweet>> callback) {
        RealmResults<Tweet> result = realm.where(Tweet.class).findAll();
        result.sort("id", false);
        callback.success(result, null);
    }

    public void removeOlderThanBeforeYesterday() {
        realm.beginTransaction();

        // calculate day before yesterday
        Date date = new Date();
        long newTime = date.getTime() - TimeUnit.DAYS.toMillis(2);
        date.setTime(newTime);

        RealmResults<Tweet> oldTweets = realm.where(Tweet.class).lessThan("createdAt", date).findAll();
        oldTweets.clear();
        realm.commitTransaction();
    }

    public void removeOlderThanBeforeYesterday(Callback<List<Tweet>> callback) {
        removeOlderThanBeforeYesterday();

        getFromDatabase(callback);
    }

    private class SaveWrapper implements Callback<List<Tweet>> {

        private final Callback<List<Tweet>> original;

        private SaveWrapper(Callback<List<Tweet>> original) {
            this.original = original;
        }

        @Override
        public void success(List<Tweet> tweets, Response response) {
            removeOlderThanBeforeYesterday(); // to keep database size limited

            realm.beginTransaction();

            realm.copyToRealmOrUpdate(tweets);

            realm.commitTransaction();

            getFromDatabase(original);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    }
}
