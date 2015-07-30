package info.zametki.twitteroid.data.model;

import io.realm.RealmObject;

public class RealmFloat extends RealmObject {

    private float value;

    public RealmFloat() {
    }

    public RealmFloat(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}