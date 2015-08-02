package info.zametki.twitteroid.data.apis;

import java.util.List;

import info.zametki.twitteroid.data.model.Tweet;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Author vbevans94.
 */
public interface TwitterApi {

    String HOME_TIMELINE = "/statuses/home_timeline.json?extended_entities=true";

    @GET(HOME_TIMELINE)
    void timeline(/*@Query("since_id") long sinceId, */@Query("max_id") long maxId, Callback<List<Tweet>> callback);

    @GET(HOME_TIMELINE)
    void timeline(Callback<List<Tweet>> callback);
}
