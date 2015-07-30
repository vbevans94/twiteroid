package info.zametki.twitteroid.data.model;

import io.realm.RealmObject;

/**
 * Author vbevans94.
 */
public class Coordinates extends RealmObject {

    private double lat;

    private double lng;

    public Coordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Coordinates() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
