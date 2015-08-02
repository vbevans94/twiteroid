package info.zametki.twitteroid.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Author vbevans94.
 */
public class ExtendedEntity extends RealmObject {

    public static final String VIDEO_TYPE = "video";

    @Expose
    @PrimaryKey
    private long id;

    @Expose
    @SerializedName("media_url")
    private String mediaUrl;

    @Expose
    @SerializedName("url")
    private String extractedUrl;

    @Expose
    private String type;

    @Expose
    @SerializedName("duration_millis")
    private long durationMillis;

    @Expose
    @SerializedName("video_info")
    private VideoInfo videoInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getExtractedUrl() {
        return extractedUrl;
    }

    public void setExtractedUrl(String extractedUrl) {
        this.extractedUrl = extractedUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }
}
