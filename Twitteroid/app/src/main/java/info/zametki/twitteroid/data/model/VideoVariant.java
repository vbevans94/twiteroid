package info.zametki.twitteroid.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Author vbevans94.
 */
public class VideoVariant extends RealmObject {

    @Expose
    private int bitrate;

    @Expose
    @SerializedName("content_type")
    private String contentType;

    @Expose
    private String url;

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
