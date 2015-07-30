package info.zametki.twitteroid.data.model;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Author vbevans94.
 */
public class Sizes extends RealmObject {

    @Expose
    private Size large;

    public Size getLarge() {
        return large;
    }

    public void setLarge(Size large) {
        this.large = large;
    }
}
