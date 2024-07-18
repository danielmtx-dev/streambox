package nemosofts.streambox.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.metrics.PlaybackStateEvent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
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
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.player.BrightnessVolumeControl;
import nemosofts.streambox.Util.player.CustomPlayerView;
import nemosofts.streambox.adapter.player.AdapterLivePlayer;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;

@UnstableApi
public class PlayerLiveActivity extends AppCompatActivity {

    private Boolean is_play = false;
    private SimpleExoPlayer exoPlayer;
    private CustomPlayerView playerView;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;
    private ImageView iv_play, iv_player_fav;

    private DBHelper dbHelper;
    private SharedPref sharedPref;
    private ImageView btnTryAgain;
    private TextView tv_player_title;
    private ProgressBar pb_player;
    public boolean isOpenList = false;
    private boolean isLocked = false;

    // Live List
    private RelativeLayout rl_data;
    private AdapterLivePlayer adapter;
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
        dbHelper = new DBHelper(this);

        rl_data = findViewById(R.id.rl_data);
        rl_data.setOnClickListener(v -> {

        });

        tv_player_title = findViewById(R.id.tv_player_title);
        pb_player = findViewById(R.id.pb_player);
        btnTryAgain = findViewById(R.id.iv_reset);
        iv_play = findViewById(R.id.iv_play);
        iv_player_fav = findViewById(R.id.iv_player_fav);

        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(this).build();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView = findViewById(R.id.nSoftsPlayerView);
        playerView.setPlayer(exoPlayer);
        playerView.setUseController(true);
        playerView.requestFocus();
        playerView.setControllerHideOnTouch(false);
        playerView.setControllerAutoShow(true);
        playerView.setBrightnessControl(new BrightnessVolumeControl(this));

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
                    iv_play.setImageResource(R.drawable.ic_pause);
                    pb_player.setVisibility(View.GONE);
                    is_play = true;
                } else if (playbackState == Player.STATE_BUFFERING) {
                    pb_player.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                if (Boolean.TRUE.equals(is_play)){
                    setMediaSource();
                } else {
                    exoPlayer.stop();
                    btnTryAgain.setVisibility(View.VISIBLE);
                    pb_player.setVisibility(View.GONE);
                    iv_play.setImageResource(R.drawable.ic_play);
                    iv_play.setVisibility(View.GONE);
                }
                Toast.makeText(PlayerLiveActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Player.Listener.super.onPlayerError(error);
            }
        });

        btnTryAgain.setOnClickListener(v -> {
            btnTryAgain.setVisibility(View.GONE);
            pb_player.setVisibility(View.VISIBLE);
            setMediaSource();
        });

        rv = findViewById(R.id.rv);
        if (!Callback.arrayListLive.isEmpty()){
            LinearLayoutManager llm_trending = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(llm_trending);
            rv.setItemAnimator(new DefaultItemAnimator());
            rv.setHasFixedSize(true);
            adapter = new AdapterLivePlayer(this, Callback.arrayListLive, (item, position) -> {
                Callback.playPosLive = position;
                setMediaSource();
            });
            rv.setAdapter(adapter);
        }

        ImageView iv_lock_player = findViewById(R.id.iv_lock_player);
        iv_lock_player.setOnClickListener(v -> {
            isLocked = !isLocked;
            iv_lock_player.setImageResource(isLocked ? R.drawable.ic_player_unlock : R.drawable.ic_player_lock);
        });
        findViewById(R.id.ll_channels_list).setOnClickListener(view -> playerList());
        findViewById(R.id.iv_close_player_list).setOnClickListener(v -> playerList());
        findViewById(R.id.ll_aspect_ratio).setOnClickListener(view -> setResize());
        findViewById(R.id.iv_back_player).setOnClickListener(v -> finish());
        iv_play.setOnClickListener(v -> {
            exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
            iv_play.setImageResource(Boolean.TRUE.equals(exoPlayer.getPlayWhenReady()) ? R.drawable.ic_pause : R.drawable.ic_play);
        });

        iv_player_fav.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(dbHelper.checkFavLive(Callback.arrayListLive.get(Callback.playPosLive).getStreamID()))){
                dbHelper.removeFavLive(Callback.arrayListLive.get(Callback.playPosLive).getStreamID());
                iv_player_fav.setImageResource(R.drawable.ic_favorite_border);
                Toast.makeText(PlayerLiveActivity.this, getString(R.string.fav_remove_success), Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addToFavLive(Callback.arrayListLive.get(Callback.playPosLive));
                iv_player_fav.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(PlayerLiveActivity.this, getString(R.string.fav_success), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.ll_multiple_screen).setOnClickListener(v -> {
            Intent intent = new Intent(PlayerLiveActivity.this, MultipleScreenActivity.class);
            intent.putExtra("is_player", true);
            intent.putExtra("stream_id", Callback.arrayListLive.get(Callback.playPosLive).getStreamID());
            startActivity(intent);
            finish();
        });
    }

    private void setMediaSource() {
        if (!Callback.arrayListLive.isEmpty() && sharedPref.isLogged()){
            if (btnTryAgain.getVisibility() == View.VISIBLE){
                btnTryAgain.setVisibility(View.GONE);
            }

            if (adapter != null){
                rv.scrollToPosition(Callback.playPosLive);
                adapter.select(Callback.playPosLive);
            }

            tv_player_title.setText(Callback.arrayListLive.get(Callback.playPosLive).getName());
            iv_player_fav.setImageResource(Boolean.TRUE.equals(dbHelper.checkFavLive(Callback.arrayListLive.get(Callback.playPosLive).getStreamID())) ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

            String channelUrl = "";
            if (Boolean.TRUE.equals(sharedPref.getIsXuiUser())){
                channelUrl = sharedPref.getServerURL()+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+Callback.arrayListLive.get(Callback.playPosLive).getStreamID()+".m3u8";
            } else {
                channelUrl = sharedPref.getServerURL()+"live/"+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+Callback.arrayListLive.get(Callback.playPosLive).getStreamID()+".m3u8";
            }
            Uri uri = Uri.parse(channelUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
            iv_play.setImageResource(R.drawable.ic_pause);
            iv_play.setVisibility(View.VISIBLE);
        }
    }

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
        return new DefaultDataSourceFactory(PlayerLiveActivity.this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(PlayerLiveActivity.this, "ExoPlayerDemo"))
                .setTransferListener(bandwidthMeter)
                .setAllowCrossProtocolRedirects(true)
                .setKeepPostFor302Redirects(true);
    }

    private void setResize() {
        if (!isLocked){
            if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                ApplicationUtil.showText(playerView, getString(R.string.video_resize_crop));
            } else {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                ApplicationUtil.showText(playerView, getString(R.string.video_resize_fit));
            }
        }
    }

    private void playerList() {
        if (!isLocked){
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
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_player_live;
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