package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.metrics.PlaybackStateEvent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.player.BrightnessVolumeControl;
import nemosofts.streambox.Util.player.CustomPlayerView;
import nemosofts.streambox.adapter.player.AdapterEpisodesPlayer;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;

@UnstableApi
public class PlayerEpisodesActivity extends AppCompatActivity {

    private SimpleExoPlayer exoPlayer;
    private CustomPlayerView playerView;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;

    private SharedPref sharedPref;
    private ProgressBar pb_player;
    private TextView tv_player_title;
    public boolean isOpenList = false;

    // Live List
    private RelativeLayout rl_data;
    private AdapterEpisodesPlayer adapter;
    private RecyclerView rv;

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

        sharedPref = new SharedPref(this);

        rl_data = findViewById(R.id.rl_data);
        rl_data.setOnClickListener(v -> {

        });

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
        playerView.setControllerVisibilityListener((PlayerView.ControllerVisibilityListener) visibility -> {
            findViewById(R.id.rl_player_top).setVisibility(visibility);
            findViewById(R.id.rl_player_bottom).setVisibility(visibility);
        });
        playerView.setControllerHideOnTouch(false);
        playerView.setControllerAutoShow(true);
        playerView.setBrightnessControl(new BrightnessVolumeControl(PlayerEpisodesActivity.this));

        setMediaSource();

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
                Toast.makeText(PlayerEpisodesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Player.Listener.super.onPlayerError(error);
            }
        });

        findViewById(R.id.exo_resize).setOnClickListener(v -> setResize());
        findViewById(R.id.iv_back_player).setOnClickListener(v -> finish());
        findViewById(R.id.iv_close_player_list).setOnClickListener(view -> playerList());
        findViewById(R.id.ll_player_list).setOnClickListener(v -> playerList());

        rv = findViewById(R.id.rv);
        if (!Callback.arrayListEpisodes.isEmpty()){
            LinearLayoutManager llm_trending = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(llm_trending);
            rv.setItemAnimator(new DefaultItemAnimator());
            rv.setHasFixedSize(true);
            adapter = new AdapterEpisodesPlayer(this, Callback.arrayListEpisodes, (item, position) -> {
                Callback.playPosEpisodes = position;
                setMediaSource();
            });
            rv.setAdapter(adapter);
        }
    }

    private void setMediaSource() {
        if (!Callback.arrayListEpisodes.isEmpty() && sharedPref.isLogged()){
            tv_player_title.setText(Callback.arrayListEpisodes.get(Callback.playPosEpisodes).getTitle());
            String episodeUrl= sharedPref.getServerURL()+"series/"+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+Callback.arrayListEpisodes.get(Callback.playPosEpisodes).getId()+"."+Callback.arrayListEpisodes.get(Callback.playPosEpisodes).getContainerExtension();
            Uri uri = Uri.parse(episodeUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.seekTo(0);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);

            if (adapter != null){
                rv.scrollToPosition(Callback.playPosEpisodes);
                adapter.select(Callback.playPosEpisodes);
            }
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
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(this, "ExoPlayerDemo"))
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

    private void playerList() {
        if (isOpenList) {
            isOpenList = false;
            rl_data.setVisibility(View.GONE);
        } else {
            isOpenList = true;
            rl_data.setVisibility(View.VISIBLE);
            if (!Callback.arrayListLive.isEmpty() && (adapter != null)){
                rv.scrollToPosition(Callback.playPosLive);
                adapter.select(Callback.playPosLive);
            }
        }
    }

    private long getCurrentSeekPosition() {
        return exoPlayer.getCurrentPosition();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_player_episodes;
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
        if (isOpenList) {
            isOpenList = false;
            rl_data.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

}