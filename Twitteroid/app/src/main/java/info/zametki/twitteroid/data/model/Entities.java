package info.zametki.twitteroid.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Author vbevans94.
 */
public class Entities extends RealmObject {

    @Expose
    private RealmList<TweetUrl> urls;

    @Expose
    private RealmList<TweetMedia> media;

    public RealmList<TweetUrl> getUrls() {
        return urls;
    }

    public void setUrls(RealmList<TweetUrl> urls) {
        this.urls = urls;
    }

    public RealmList<TweetMedia> getMedia() {
        return media;
    }

    public void setMedia(RealmList<TweetMedia> media) {
        this.media = media;
    }
}
