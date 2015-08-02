package info.zametki.twitteroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer.extractor.ts.TsExtractor;
import com.google.android.exoplayer.extractor.webm.WebmExtractor;
import com.google.android.exoplayer.metadata.GeobMetadata;
import com.google.android.exoplayer.metadata.PrivMetadata;
import com.google.android.exoplayer.metadata.TxxxMetadata;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.Map;

import info.zametki.twitteroid.R;
import info.zametki.twitteroid.ui.player.DemoPlayer;
import info.zametki.twitteroid.ui.player.ExtractorRendererBuilder;
import info.zametki.twitteroid.ui.player.HlsRendererBuilder;

/**
 * An activity that plays media using {@link DemoPlayer}.
 */
public class PlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        DemoPlayer.Listener, DemoPlayer.CaptionListener, DemoPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {

    public static final int TYPE_HLS = 2;
    public static final int TYPE_MP4 = 3;
    public static final int TYPE_MP3 = 4;
    public static final int TYPE_FMP4 = 5;
    public static final int TYPE_WEBM = 6;
    public static final int TYPE_MKV = 7;
    public static final int TYPE_TS = 8;
    public static final int TYPE_AAC = 9;
    public static final int TYPE_M4A = 10;

    public static final String CONTENT_TYPE_EXTRA = "content_type";

    private static final String TAG = "PlayerActivity";
    private static final int MENU_GROUP_TRACKS = 1;
    private static final int ID_OFFSET = 2;

    private static final CookieManager defaultCookieManager;

    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private AspectRatioFrameLayout videoFrame;
    private SurfaceView surfaceView;

    private DemoPlayer player;
    private boolean playerNeedsPrepare;

    private long playerPosition;

    private Uri contentUri;
    private int contentType;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private AudioCapabilities audioCapabilities;

    public static void start(Context context, String videoUrl) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.setData(Uri.parse(videoUrl));
        // TODO:parse content type
        intent.putExtra(CONTENT_TYPE_EXTRA, TYPE_MP4);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        contentUri = intent.getData();
        contentType = intent.getIntExtra(CONTENT_TYPE_EXTRA, -1);

        setContentView(R.layout.activity_player);
        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getApplicationContext(), this);

        videoFrame = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);

        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // The player will be prepared on receiving audio capabilities.
        audioCapabilitiesReceiver.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        audioCapabilitiesReceiver.unregister();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            this.audioCapabilities = audioCapabilities;
            releasePlayer();
            preparePlayer();
        } else if (player != null) {
            player.setBackgrounded(false);
        }
    }

    // Internal methods

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        switch (contentType) {
            case TYPE_HLS:
                return new HlsRendererBuilder(this, userAgent, contentUri.toString(), audioCapabilities);
            case TYPE_M4A: // There are no file format differences between M4A and MP4.
            case TYPE_MP4:
                return new ExtractorRendererBuilder(this, userAgent, contentUri, new Mp4Extractor());
            case TYPE_MP3:
                return new ExtractorRendererBuilder(this, userAgent, contentUri, new Mp3Extractor());
            case TYPE_TS:
                return new ExtractorRendererBuilder(this, userAgent, contentUri,
                        new TsExtractor(0, audioCapabilities));
            case TYPE_AAC:
                return new ExtractorRendererBuilder(this, userAgent, contentUri, new AdtsExtractor());
            case TYPE_FMP4:
                return new ExtractorRendererBuilder(this, userAgent, contentUri,
                        new FragmentedMp4Extractor());
            case TYPE_WEBM:
            case TYPE_MKV:
                return new ExtractorRendererBuilder(this, userAgent, contentUri, new WebmExtractor());
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    private void preparePlayer() {
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.setCaptionListener(this);
            player.setMetadataListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    // DemoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
    }

    @Override
    public void onError(Exception e) {
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            int stringId = Util.SDK_INT < 18 ? R.string.drm_error_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
            Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthAspectRatio) {
        videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    private void configurePopupWithTracks(PopupMenu popup,
                                          final OnMenuItemClickListener customActionClickListener,
                                          final int trackType) {
        if (player == null) {
            return;
        }
        int trackCount = player.getTrackCount(trackType);
        if (trackCount == 0) {
            return;
        }
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return (customActionClickListener != null
                        && customActionClickListener.onMenuItemClick(item))
                        || onTrackItemClick(item, trackType);
            }
        });
        Menu menu = popup.getMenu();
        // ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0)
        menu.add(MENU_GROUP_TRACKS, DemoPlayer.DISABLED_TRACK + ID_OFFSET, Menu.NONE, R.string.off);
        if (trackCount == 1 && TextUtils.isEmpty(player.getTrackName(trackType, 0))) {
            menu.add(MENU_GROUP_TRACKS, DemoPlayer.PRIMARY_TRACK + ID_OFFSET, Menu.NONE, R.string.on);
        } else {
            for (int i = 0; i < trackCount; i++) {
                menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE, player.getTrackName(trackType, i));
            }
        }
        menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
        menu.findItem(player.getSelectedTrackIndex(trackType) + ID_OFFSET).setChecked(true);
    }

    private boolean onTrackItemClick(MenuItem item, int type) {
        if (player == null || item.getGroupId() != MENU_GROUP_TRACKS) {
            return false;
        }
        player.selectTrack(type, item.getItemId() - ID_OFFSET);
        return true;
    }

    // DemoPlayer.CaptionListener implementation

    @Override
    public void onCues(List<Cue> cues) {
    }

    // DemoPlayer.MetadataListener implementation

    @Override
    public void onId3Metadata(Map<String, Object> metadata) {
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (TxxxMetadata.TYPE.equals(entry.getKey())) {
                TxxxMetadata txxxMetadata = (TxxxMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s",
                        TxxxMetadata.TYPE, txxxMetadata.description, txxxMetadata.value));
            } else if (PrivMetadata.TYPE.equals(entry.getKey())) {
                PrivMetadata privMetadata = (PrivMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s",
                        PrivMetadata.TYPE, privMetadata.owner));
            } else if (GeobMetadata.TYPE.equals(entry.getKey())) {
                GeobMetadata geobMetadata = (GeobMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        GeobMetadata.TYPE, geobMetadata.mimeType, geobMetadata.filename,
                        geobMetadata.description));
            } else {
                Log.i(TAG, String.format("ID3 TimedMetadata %s", entry.getKey()));
            }
        }
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }
}