package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.metrics.PlaybackStateEvent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.dash.DefaultDashChunkSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.smoothstreaming.DefaultSsChunkSource;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.player.BrightnessVolumeControl;
import nemosofts.streambox.Util.player.CustomPlayerView;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;

@UnstableApi
public class PlayerMovieActivity extends AppCompatActivity {

    private String stream_id = "", container=".mp4", movie_name="";
    private SimpleExoPlayer exoPlayer;
    private CustomPlayerView playerView;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;
    private DBHelper dbHelper;
    private SharedPref sharedPref;
    private ProgressBar pb_player;
    private TextView tv_player_title;

    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Boolean.TRUE.equals(Callback.isLandscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        IsStatusBar.ifSupported(this);
        IsScreenshot.ifSupported(this);
        IsRTL.ifSupported(this);

        stream_id = getIntent().getStringExtra("stream_id");
        container = getIntent().getStringExtra("container");
        movie_name = getIntent().getStringExtra("movie_name");

        dbHelper = new DBHelper(this);
        sharedPref = new SharedPref(this);

        pb_player = findViewById(R.id.pb_player);
        tv_player_title = findViewById(R.id.tv_player_title);

        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(this).build();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView = findViewById(R.id.nSoftsPlayerView);
        playerView.setPlayer(exoPlayer);
        playerView.setShowVrButton(true);
        playerView.setShowSubtitleButton(true);
        playerView.setShowFastForwardButton(true);
        playerView.setShowRewindButton(true);
        playerView.setShowNextButton(false);
        playerView.setShowPreviousButton(false);
        playerView.setControllerHideOnTouch(false);
        playerView.setControllerAutoShow(true);

        playerView.setControllerVisibilityListener((PlayerView.ControllerVisibilityListener) visibility -> findViewById(R.id.rl_player_movie_top).setVisibility(visibility));

        playerView.setBrightnessControl(new BrightnessVolumeControl(this));

        setMediaSource(dbHelper.getSeekMovie(stream_id, movie_name));

        exoPlayer.addListener(new Player.Listener() {

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                playerView.setKeepScreenOn(isPlaying);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == PlaybackStateEvent.STATE_PLAYING) {
                    pb_player.setVisibility(View.GONE);
                } else if (playbackState == Player.STATE_BUFFERING) {
                    pb_player.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                exoPlayer.stop();
                pb_player.setVisibility(View.GONE);
                Toast.makeText(PlayerMovieActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Player.Listener.super.onPlayerError(error);
            }
        });

        findViewById(R.id.exo_resize).setOnClickListener(v -> setResize());
        findViewById(R.id.iv_back_player).setOnClickListener(v -> finish());
    }
    private void setMediaSource(int currentPosition) {
        if (sharedPref.isLogged()){
            tv_player_title.setText(movie_name);
            String channelUrl = sharedPref.getServerURL()+"movie/"+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+stream_id+"."+container;
            Uri uri = Uri.parse(channelUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.seekTo(currentPosition);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @SuppressLint("SwitchIntDef")
    @NonNull
    private MediaSource buildMediaSource(Uri uri) {
        int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_RTSP:
                return new RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(PlayerMovieActivity.this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(PlayerMovieActivity.this, "ExoPlayerDemo"))
                .setTransferListener(bandwidthMeter)
                .setAllowCrossProtocolRedirects(true)
                .setKeepPostFor302Redirects(true);
    }

    private void setResize() {
        if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            ApplicationUtil.showText(playerView, getString(R.string.video_resize_crop));
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            ApplicationUtil.showText(playerView, getString(R.string.video_resize_fit));
        }
    }

    private long getCurrentSeekPosition() {
        return exoPlayer.getCurrentPosition();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_player_movie;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.getPlaybackState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.getPlaybackState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.getPlaybackState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            dbHelper.addToSeekMovie(String.valueOf(getCurrentSeekPosition()), stream_id, movie_name);
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}