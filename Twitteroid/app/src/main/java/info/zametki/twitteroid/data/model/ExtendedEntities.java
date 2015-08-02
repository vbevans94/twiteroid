package info.zametki.twitteroid.data.model;

import com.google.gson.annotations.Expose;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Author vbevans94.
 */
public class ExtendedEntities extends RealmObject {

    @Expose
    private RealmList<ExtendedEntity> media;

    public RealmList<ExtendedEntity> getMedia() {
        return media;
    }

    public void setMedia(RealmList<ExtendedEntity> media) {
        this.media = media;
    }
}
