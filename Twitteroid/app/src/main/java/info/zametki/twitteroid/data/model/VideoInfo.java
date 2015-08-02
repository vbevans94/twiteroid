package info.zametki.twitteroid.data.model;

import com.google.gson.annotations.Expose;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Author vbevans94.
 */
public class VideoInfo extends RealmObject {

    @Expose
    private RealmList<VideoVariant> variants;

    public RealmList<VideoVariant> getVariants() {
        return variants;
    }

    public void setVariants(RealmList<VideoVariant> variants) {
        this.variants = variants;
    }
}
