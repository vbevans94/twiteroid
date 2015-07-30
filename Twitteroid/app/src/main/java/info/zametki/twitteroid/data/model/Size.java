package info.zametki.twitteroid.data.model;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Author vbevans94.
 */
public class Size extends RealmObject {

    @Expose
    private int w;

    @Expose
    private int h;

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
