package info.zametki.twitteroid.ui.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.zametki.twitteroid.R;
import info.zametki.twitteroid.data.model.Coordinates;
import info.zametki.twitteroid.data.model.Entities;
import info.zametki.twitteroid.data.model.ExtendedEntities;
import info.zametki.twitteroid.data.model.ExtendedEntity;
import info.zametki.twitteroid.data.model.Place;
import info.zametki.twitteroid.data.model.Tweet;
import info.zametki.twitteroid.data.model.TweetMedia;
import info.zametki.twitteroid.data.model.TweetUrl;
import info.zametki.twitteroid.data.model.VideoInfo;
import info.zametki.twitteroid.data.model.VideoVariant;
import info.zametki.twitteroid.ui.activity.ImageActivity;
import info.zametki.twitteroid.ui.activity.MapActivity;
import info.zametki.twitteroid.ui.activity.PlayerActivity;
import info.zametki.twitteroid.ui.transformation.PlayTransformation;
import info.zametki.twitteroid.ui.transformation.RoundedTransformation;
import io.realm.RealmList;

/**
 * Author vbevans94.
 */
public class TweetItemView extends RelativeLayout {

    private static final String URL_FORMAT = "<a href=\"%s\">%s</a>";

    @InjectView(R.id.image_profile)
    ImageView imageProfile;

    @InjectView(R.id.text_tweet)
    TextView textTweet;

    @InjectView(R.id.image_media)
    ImageView imageMedia;

    @InjectView(R.id.image_video)
    ImageView imageVideo;

    @InjectView(R.id.text_name)
    TextView textName;

    @InjectView(R.id.text_place)
    TextView textPlace;

    private final int corner;

    public TweetItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        corner = getResources().getDimensionPixelSize(R.dimen.profile_image_corner);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        textTweet.setMovementMethod(LinkMovementMethod.getInstance());

        textPlace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() instanceof Tweet) {
                    Tweet tweet = (Tweet) v.getTag();
                    String name = tweet.getPlace().getName();

                    MapActivity.start(getContext(), toLatLng(tweet.getPlace()), name);
                }
            }
        });

        imageMedia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() instanceof TweetMedia) {
                    TweetMedia media = (TweetMedia) v.getTag();
                    ImageActivity.start(getContext(), getFullImage(media));
                }
            }
        });

        imageVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() instanceof ExtendedEntity) {
                    ExtendedEntity video = (ExtendedEntity) v.getTag();
                    // TODO: add dialog to choose format
                    VideoInfo videoInfo = video.getVideoInfo();
                    if (videoInfo != null) {
                        String url = null;
                        for (VideoVariant variant : videoInfo.getVariants()) {
                            if (variant.getContentType().equals("video/mp4")) {
                                url = variant.getUrl();
                                break;
                            }
                        }
                        if (url != null) {
                            PlayerActivity.start(getContext(), url);
                        } else {
                            Toast.makeText(getContext(), R.string.error_video_format, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    public void bind(Tweet tweet, Picasso picasso) {
        String tweetText = tweet.getText();
        textName.setText(tweet.getUser().getName());

        picasso.load(tweet.getUser().getImageUrl())
                .transform(new RoundedTransformation(corner))
                .into(imageProfile);

        Entities entities = tweet.getEntities();
        if (entities != null) {
            // extract videos
            ExtendedEntities extendedEntities = tweet.getExtendedEntities();
            boolean hasVideos = false;
            if (extendedEntities != null && extendedEntities.getMedia() != null && !extendedEntities.getMedia().isEmpty()) {
                for (ExtendedEntity video : extendedEntities.getMedia()) {
                    if (video.getType().equals(ExtendedEntity.VIDEO_TYPE)) {
                        tweetText = tweetText.replace(video.getExtractedUrl(), "");

                        picasso.load(video.getMediaUrl())
                                .transform(new PlayTransformation(getContext(), R.drawable.ic_play_normal))
                                .into(imageVideo);

                        imageVideo.setTag(video);

                        hasVideos = true;
                        break; // only one
                    }
                }
            }
            imageVideo.setVisibility(hasVideos ? VISIBLE : GONE);

            // extract images
            List<TweetMedia> medias = entities.getMedia();
            if (medias != null && !medias.isEmpty() && !hasVideos) {
                imageMedia.setVisibility(VISIBLE);
                for (TweetMedia media : medias) {
                    tweetText = tweetText.replace(media.getExtractedUrl(), "");

                    picasso.load(media.getMediaUrl())
                            .into(imageMedia);

                    imageMedia.setTag(media);
                }
            } else {
                imageMedia.setVisibility(GONE);
            }

            // extract urls
            List<TweetUrl> urls = entities.getUrls();
            if (urls != null && !urls.isEmpty()) {
                for (TweetUrl url : urls) {
                    tweetText = tweetText.replace(url.getUrl(), String.format(URL_FORMAT, url.getExpandedUrl(), url.getDisplayUrl()));
                }
            }
        }

        if (!TextUtils.isEmpty(tweetText)) {
            textTweet.setVisibility(VISIBLE);
            textTweet.setText(Html.fromHtml(tweetText));
        } else {
            textTweet.setVisibility(GONE);
        }

        Place place = tweet.getPlace();
        if (place != null) {
            textPlace.setVisibility(VISIBLE);

            textPlace.setText(getContext().getString(R.string.text_from_place, place.getCountry()));
            textPlace.setTag(tweet);
        } else {
            textPlace.setVisibility(GONE);
        }
    }

    private String getFullImage(TweetMedia media) {
        String mediaUrl = media.getMediaUrl();
        if (media.getSizes() != null && media.getSizes().getLarge() != null) {
            mediaUrl += ":large";
        }
        return mediaUrl;
    }

    private LatLng toLatLng(Place place) {
        Coordinates coordinates = place.getBoundingBox().getCoordinates().get(0);
        return new LatLng(coordinates.getLng(), coordinates.getLat());
    }
}
