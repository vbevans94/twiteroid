package info.zametki.twitteroid.data.model;

import com.google.gson.annotations.Expose;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Author vbevans94.
 */
public class BoundingBox extends RealmObject {

    @Expose
    private RealmList<Coordinates> coordinates;

    public RealmList<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(RealmList<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }
}
