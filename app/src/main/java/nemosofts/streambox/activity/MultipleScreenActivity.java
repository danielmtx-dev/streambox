package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.metrics.PlaybackStateEvent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import androidx.media3.ui.PlayerView;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.dialog.ScreenDialog;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;

@UnstableApi
public class MultipleScreenActivity extends AppCompatActivity {

    private SharedPref sharedPref;
    private String stream_id = "0";
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;
    private ScreenDialog screenDialog;
    private Boolean is_player = false;

    // Player One
    private SimpleExoPlayer exoPlayer_one;
    private ImageView add_btn_one, iv_volume_one;
    private ProgressBar pb_one;

    // Player Tow
    private SimpleExoPlayer exoPlayer_tow;
    private ImageView add_btn_tow, iv_volume_tow;
    private ProgressBar pb_tow;

    // Player Three
    private SimpleExoPlayer exoPlayer_three;
    private ImageView add_btn_three, iv_volume_three;
    private ProgressBar pb_three;

    // Player Four
    private SimpleExoPlayer exoPlayer_four;
    private ImageView add_btn_four, iv_volume_four;
    private ProgressBar pb_four;

    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        if (Boolean.TRUE.equals(Callback.isLandscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        IsStatusBar.ifSupported(this);
        IsScreenshot.ifSupported(this);
        IsRTL.ifSupported(this);

        findViewById(R.id.theme_bg).setBackgroundResource(ApplicationUtil.openThemeBg(this));

        sharedPref = new SharedPref(this);
        screenDialog = new ScreenDialog(this, this::setScreen);

        Intent intent = getIntent();
        is_player = intent.getBooleanExtra("is_player", false);
        if (Boolean.TRUE.equals(is_player)){
            stream_id = intent.getStringExtra("stream_id");
        }

        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(this).build();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        add_btn_one = findViewById(R.id.iv_add_btn_one);
        add_btn_tow = findViewById(R.id.iv_add_btn_tow);
        add_btn_three = findViewById(R.id.iv_add_btn_three);
        add_btn_four = findViewById(R.id.iv_add_btn_four);
        iv_volume_one = findViewById(R.id.iv_volume_one);
        iv_volume_tow = findViewById(R.id.iv_volume_tow);
        iv_volume_three = findViewById(R.id.iv_volume_three);
        iv_volume_four = findViewById(R.id.iv_volume_four);
        pb_one = findViewById(R.id.pb_one);
        pb_tow = findViewById(R.id.pb_tow);
        pb_three = findViewById(R.id.pb_three);
        pb_four = findViewById(R.id.pb_four);

        if (Boolean.TRUE.equals(is_player)){
            setScreen(5);
            setPlayerOne(getChannelUrl(stream_id));
        } else {
            screenDialog.showDialog();
        }

        add_btn_one.setOnClickListener(v -> {
            Intent result = new Intent(MultipleScreenActivity.this, FilterActivity.class);
            startActivityForResult(result, 101);
        });
        add_btn_tow.setOnClickListener(v -> {
            Intent result = new Intent(MultipleScreenActivity.this, FilterActivity.class);
            startActivityForResult(result, 102);
        });
        add_btn_three.setOnClickListener(v -> {
            Intent result = new Intent(MultipleScreenActivity.this, FilterActivity.class);
            startActivityForResult(result, 103);
        });
        add_btn_four.setOnClickListener(v -> {
            Intent result = new Intent(MultipleScreenActivity.this, FilterActivity.class);
            startActivityForResult(result, 104);
        });

        findViewById(R.id.vw_player_one).setOnClickListener(v -> setVolume(1));
        findViewById(R.id.vw_player_tow).setOnClickListener(v -> setVolume(2));
        findViewById(R.id.vw_player_three).setOnClickListener(v -> setVolume(3));
        findViewById(R.id.vw_player_four).setOnClickListener(v -> setVolume(4));

        findViewById(R.id.vw_player_one).setOnLongClickListener(v -> {
            if ((exoPlayer_one != null)) {
                exoPlayer_one.stop();
                exoPlayer_one.release();
                pb_one.setVisibility(View.GONE);
                add_btn_one.setVisibility(View.VISIBLE);
                findViewById(R.id.vw_player_one).setVisibility(View.INVISIBLE);
                findViewById(R.id.player_one).setVisibility(View.GONE);
                iv_volume_one.setImageResource(R.drawable.ic_volume_off);
                iv_volume_one.setVisibility(View.GONE);
                Toast.makeText(MultipleScreenActivity.this,"Removed", Toast.LENGTH_LONG).show();
            }
            return true;
        });
        findViewById(R.id.vw_player_tow).setOnLongClickListener(v -> {
            if ((exoPlayer_tow != null)) {
                exoPlayer_tow.stop();
                exoPlayer_tow.release();
                pb_tow.setVisibility(View.GONE);
                add_btn_tow.setVisibility(View.VISIBLE);
                findViewById(R.id.vw_player_tow).setVisibility(View.INVISIBLE);
                findViewById(R.id.player_tow).setVisibility(View.GONE);
                iv_volume_tow.setImageResource(R.drawable.ic_volume_off);
                iv_volume_tow.setVisibility(View.GONE);
                Toast.makeText(MultipleScreenActivity.this,"Removed", Toast.LENGTH_LONG).show();
            }
            return true;
        });
        findViewById(R.id.vw_player_three).setOnLongClickListener(v -> {
            if ((exoPlayer_three != null)) {
                exoPlayer_three.stop();
                exoPlayer_three.release();
                pb_three.setVisibility(View.GONE);
                add_btn_three.setVisibility(View.VISIBLE);
                findViewById(R.id.vw_player_three).setVisibility(View.INVISIBLE);
                findViewById(R.id.player_three).setVisibility(View.GONE);
                iv_volume_three.setImageResource(R.drawable.ic_volume_off);
                iv_volume_three.setVisibility(View.GONE);
                Toast.makeText(MultipleScreenActivity.this,"Removed", Toast.LENGTH_LONG).show();
            }
            return true;
        });
        findViewById(R.id.vw_player_four).setOnLongClickListener(v -> {
            if ((exoPlayer_four != null)) {
                exoPlayer_four.stop();
                exoPlayer_four.release();
                pb_four.setVisibility(View.GONE);
                add_btn_four.setVisibility(View.VISIBLE);
                findViewById(R.id.vw_player_four).setVisibility(View.INVISIBLE);
                findViewById(R.id.player_four).setVisibility(View.GONE);
                iv_volume_four.setImageResource(R.drawable.ic_volume_off);
                iv_volume_four.setVisibility(View.GONE);
                Toast.makeText(MultipleScreenActivity.this,"Removed", Toast.LENGTH_LONG).show();
            }
            return true;
        });
    }

    private void setVolume(int player) {
        if (player == 1){
            if (exoPlayer_one != null) {
                exoPlayer_one.setVolume(1);
            }
            if (exoPlayer_tow != null) {
                exoPlayer_tow.setVolume(0);
            }
            if (exoPlayer_three != null) {
                exoPlayer_three.setVolume(0);
            }
            if (exoPlayer_four != null) {
                exoPlayer_four.setVolume(0);
            }
        } else if (player == 2){
            if (exoPlayer_one != null) {
                exoPlayer_one.setVolume(0);
            }
            if (exoPlayer_tow != null) {
                exoPlayer_tow.setVolume(1);
            }
            if (exoPlayer_three != null) {
                exoPlayer_three.setVolume(0);
            }
            if (exoPlayer_four != null) {
                exoPlayer_four.setVolume(0);
            }
        } else if (player == 3){
            if (exoPlayer_one != null) {
                exoPlayer_one.setVolume(0);
            }
            if (exoPlayer_tow != null) {
                exoPlayer_tow.setVolume(0);
            }
            if (exoPlayer_three != null) {
                exoPlayer_three.setVolume(1);
            }
            if (exoPlayer_four != null) {
                exoPlayer_four.setVolume(0);
            }
        } else if (player == 4){
            if (exoPlayer_one != null) {
                exoPlayer_one.setVolume(0);
            }
            if (exoPlayer_tow != null) {
                exoPlayer_tow.setVolume(0);
            }
            if (exoPlayer_three != null) {
                exoPlayer_three.setVolume(0);
            }
            if (exoPlayer_four != null) {
                exoPlayer_four.setVolume(1);
            }
        }
        setVolumeIcon(player);
    }

    private void setVolumeIcon(int player) {
        if (player == 1){
            iv_volume_one.setImageResource(exoPlayer_one != null && exoPlayer_one.getVolume() == 0f ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            iv_volume_tow.setImageResource(R.drawable.ic_volume_off);
            iv_volume_three.setImageResource(R.drawable.ic_volume_off);
            iv_volume_four.setImageResource(R.drawable.ic_volume_off);
        } else if (player == 2){
            iv_volume_one.setImageResource(R.drawable.ic_volume_off);
            iv_volume_tow.setImageResource(exoPlayer_tow != null && exoPlayer_tow.getVolume() == 0f ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            iv_volume_three.setImageResource(R.drawable.ic_volume_off);
            iv_volume_four.setImageResource(R.drawable.ic_volume_off);
        } else if (player == 3){
            iv_volume_one.setImageResource(R.drawable.ic_volume_off);
            iv_volume_tow.setImageResource(R.drawable.ic_volume_off);
            iv_volume_three.setImageResource(exoPlayer_three != null && exoPlayer_three.getVolume() == 0f ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            iv_volume_four.setImageResource(R.drawable.ic_volume_off);
        } else if (player == 4){
            iv_volume_one.setImageResource(R.drawable.ic_volume_off);
            iv_volume_tow.setImageResource(R.drawable.ic_volume_off);
            iv_volume_three.setImageResource(R.drawable.ic_volume_off);
            iv_volume_four.setImageResource(exoPlayer_four != null && exoPlayer_four.getVolume() == 0f ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
        } else {
            iv_volume_one.setImageResource(R.drawable.ic_volume_off);
            iv_volume_tow.setImageResource(R.drawable.ic_volume_off);
            iv_volume_three.setImageResource(R.drawable.ic_volume_off);
            iv_volume_four.setImageResource(R.drawable.ic_volume_off);
        }
    }

    private void setScreen(int screen) {
        if (screen == 1){
            screenDialog.dismissDialog();
            findViewById(R.id.ll_screen_one_two).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_screen_three_four).setVisibility(View.GONE);
        } else if (screen == 2){
            screenDialog.dismissDialog();
            findViewById(R.id.ll_screen_one_two).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_screen_three_four).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_player_two).setVisibility(View.GONE);
            findViewById(R.id.rl_player_four).setVisibility(View.GONE);
        } else if (screen == 3){
            screenDialog.dismissDialog();
            findViewById(R.id.ll_screen_one_two).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_screen_three_four).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_player_two).setVisibility(View.GONE);
            findViewById(R.id.rl_player_four).setVisibility(View.VISIBLE);
        } else if (screen == 4){
            screenDialog.dismissDialog();
            findViewById(R.id.ll_screen_one_two).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_screen_three_four).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_player_two).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_player_four).setVisibility(View.GONE);
        } else if (screen == 5){
            screenDialog.dismissDialog();
            findViewById(R.id.ll_screen_one_two).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_screen_three_four).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_player_two).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_player_four).setVisibility(View.VISIBLE);
        } else {
            screenDialog.dismissDialog();
            findViewById(R.id.ll_screen_one_two).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_screen_three_four).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_player_two).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_player_four).setVisibility(View.VISIBLE);
        }
    }

    private String getChannelUrl(String streamId) {
        if (streamId != null && !streamId.isEmpty()){
            String channelUrl;
            if (Boolean.TRUE.equals(sharedPref.getIsXuiUser())){
                channelUrl = sharedPref.getServerURL()+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+streamId+".m3u8";
            } else {
                channelUrl = sharedPref.getServerURL()+"live/"+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+streamId+".m3u8";
            }
            return channelUrl;
        } else {
            return streamId;
        }
    }

    private void setPlayerOne(String channelUrl) {
        if (channelUrl != null && !channelUrl.isEmpty()){
            add_btn_one.setVisibility(View.GONE);
            if (exoPlayer_one != null) {
                exoPlayer_one.stop();
                exoPlayer_one.release();
            }
            exoPlayer_one = new SimpleExoPlayer.Builder(this).build();
            PlayerView playerView = findViewById(R.id.player_one);
            playerView.setPlayer(exoPlayer_one);
            playerView.setUseController(true);
            playerView.requestFocus();
            exoPlayer_one.addListener(new Player.Listener(){
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == Player.STATE_READY) {
                        findViewById(R.id.vw_player_one).setVisibility(View.VISIBLE);
                        findViewById(R.id.player_one).setVisibility(View.VISIBLE);
                        iv_volume_one.setVisibility(View.VISIBLE);
                        is_player = false;
                    }
                    if (playbackState == PlaybackStateEvent.STATE_PLAYING) {
                        pb_one.setVisibility(View.GONE);
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        pb_one.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPlayerError(@NonNull PlaybackException error) {
                    exoPlayer_one.stop();
                    exoPlayer_one.release();
                    pb_one.setVisibility(View.GONE);
                    add_btn_one.setVisibility(View.VISIBLE);
                    findViewById(R.id.vw_player_one).setVisibility(View.INVISIBLE);
                    findViewById(R.id.player_one).setVisibility(View.GONE);
                    Toast.makeText(MultipleScreenActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Player.Listener.super.onPlayerError(error);
                }
            });
            Uri uri = Uri.parse(channelUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer_one.setMediaSource(mediaSource);
            exoPlayer_one.prepare();
            exoPlayer_one.setPlayWhenReady(true);
            exoPlayer_one.setVolume(0);
            if (Boolean.TRUE.equals(is_player)){
                setVolume(1);
            }
        }
    }
    private void setPlayerTow(String channelUrl) {
        if (channelUrl != null && !channelUrl.isEmpty()){
            add_btn_tow.setVisibility(View.GONE);
            if (exoPlayer_tow != null) {
                exoPlayer_tow.stop();
                exoPlayer_tow.release();
            }
            exoPlayer_tow = new SimpleExoPlayer.Builder(this).build();
            PlayerView playerView = findViewById(R.id.player_tow);
            playerView.setPlayer(exoPlayer_tow);
            playerView.setUseController(true);
            playerView.requestFocus();
            exoPlayer_tow.addListener(new Player.Listener(){
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == Player.STATE_READY) {
                        findViewById(R.id.vw_player_tow).setVisibility(View.VISIBLE);
                        findViewById(R.id.player_tow).setVisibility(View.VISIBLE);
                        iv_volume_tow.setVisibility(View.VISIBLE);
                    }
                    if (playbackState == PlaybackStateEvent.STATE_PLAYING) {
                        pb_tow.setVisibility(View.GONE);
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        pb_tow.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPlayerError(@NonNull PlaybackException error) {
                    exoPlayer_tow.stop();
                    exoPlayer_one.release();
                    pb_tow.setVisibility(View.GONE);
                    add_btn_tow.setVisibility(View.VISIBLE);
                    findViewById(R.id.vw_player_tow).setVisibility(View.INVISIBLE);
                    findViewById(R.id.player_tow).setVisibility(View.GONE);
                    Toast.makeText(MultipleScreenActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Player.Listener.super.onPlayerError(error);
                }
            });
            Uri uri = Uri.parse(channelUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer_tow.setMediaSource(mediaSource);
            exoPlayer_tow.prepare();
            exoPlayer_tow.setPlayWhenReady(true);
            exoPlayer_tow.setVolume(0);
        }
    }
    private void setPlayerThree(String channelUrl) {
        if (channelUrl != null && !channelUrl.isEmpty()){
            add_btn_three.setVisibility(View.GONE);
            if (exoPlayer_three != null) {
                exoPlayer_three.stop();
                exoPlayer_three.release();
            }
            exoPlayer_three = new SimpleExoPlayer.Builder(this).build();
            PlayerView playerView = findViewById(R.id.player_three);
            playerView.setPlayer(exoPlayer_three);
            playerView.setUseController(true);
            playerView.requestFocus();
            exoPlayer_three.addListener(new Player.Listener(){
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == Player.STATE_READY) {
                        findViewById(R.id.vw_player_three).setVisibility(View.VISIBLE);
                        iv_volume_three.setVisibility(View.VISIBLE);
                        findViewById(R.id.player_three).setVisibility(View.VISIBLE);
                    }
                    if (playbackState == PlaybackStateEvent.STATE_PLAYING) {
                        pb_three.setVisibility(View.GONE);
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        pb_three.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPlayerError(@NonNull PlaybackException error) {
                    exoPlayer_three.stop();
                    exoPlayer_one.release();
                    pb_three.setVisibility(View.GONE);
                    add_btn_three.setVisibility(View.VISIBLE);
                    findViewById(R.id.vw_player_three).setVisibility(View.INVISIBLE);
                    findViewById(R.id.player_three).setVisibility(View.GONE);
                    Toast.makeText(MultipleScreenActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Player.Listener.super.onPlayerError(error);
                }
            });
            Uri uri = Uri.parse(channelUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer_three.setMediaSource(mediaSource);
            exoPlayer_three.prepare();
            exoPlayer_three.setPlayWhenReady(true);
            exoPlayer_three.setVolume(0);
        }
    }
    private void setPlayerFour(String channelUrl) {
        if (channelUrl != null && !channelUrl.isEmpty()){
            add_btn_four.setVisibility(View.GONE);
            if (exoPlayer_four != null) {
                exoPlayer_four.stop();
                exoPlayer_four.release();
            }
            exoPlayer_four = new SimpleExoPlayer.Builder(this).build();
            PlayerView playerView = findViewById(R.id.player_four);
            playerView.setPlayer(exoPlayer_four);
            playerView.setUseController(true);
            playerView.requestFocus();
            exoPlayer_four.addListener(new Player.Listener(){
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == Player.STATE_READY) {
                        findViewById(R.id.vw_player_four).setVisibility(View.VISIBLE);
                        iv_volume_four.setVisibility(View.VISIBLE);
                        findViewById(R.id.player_four).setVisibility(View.VISIBLE);
                    }
                    if (playbackState == PlaybackStateEvent.STATE_PLAYING) {
                        pb_four.setVisibility(View.GONE);
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        pb_four.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPlayerError(@NonNull PlaybackException error) {
                    exoPlayer_four.stop();
                    exoPlayer_one.release();
                    pb_four.setVisibility(View.GONE);
                    add_btn_four.setVisibility(View.VISIBLE);
                    findViewById(R.id.vw_player_four).setVisibility(View.INVISIBLE);
                    findViewById(R.id.player_four).setVisibility(View.GONE);
                    Toast.makeText(MultipleScreenActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Player.Listener.super.onPlayerError(error);
                }
            });
            Uri uri = Uri.parse(channelUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer_four.setMediaSource(mediaSource);
            exoPlayer_four.prepare();
            exoPlayer_four.setPlayWhenReady(true);
            exoPlayer_four.setVolume(0);
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
        return new DefaultDataSourceFactory(MultipleScreenActivity.this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(MultipleScreenActivity.this, "ExoPlayerDemo"))
                .setTransferListener(bandwidthMeter)
                .setAllowCrossProtocolRedirects(true)
                .setKeepPostFor302Redirects(true);
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_multiple_screen;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer_one != null && exoPlayer_one.getPlayWhenReady()) {
            exoPlayer_one.setPlayWhenReady(false);
            exoPlayer_one.getPlaybackState();
        }
        if (exoPlayer_tow != null && exoPlayer_tow.getPlayWhenReady()) {
            exoPlayer_tow.setPlayWhenReady(false);
            exoPlayer_tow.getPlaybackState();
        }
        if (exoPlayer_three != null && exoPlayer_three.getPlayWhenReady()) {
            exoPlayer_three.setPlayWhenReady(false);
            exoPlayer_three.getPlaybackState();
        }
        if (exoPlayer_four != null && exoPlayer_four.getPlayWhenReady()) {
            exoPlayer_four.setPlayWhenReady(false);
            exoPlayer_four.getPlaybackState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer_one != null && exoPlayer_one.getPlayWhenReady()) {
            exoPlayer_one.setPlayWhenReady(false);
            exoPlayer_one.getPlaybackState();
        }
        if (exoPlayer_tow != null && exoPlayer_tow.getPlayWhenReady()) {
            exoPlayer_tow.setPlayWhenReady(false);
            exoPlayer_tow.getPlaybackState();
        }
        if (exoPlayer_three != null && exoPlayer_three.getPlayWhenReady()) {
            exoPlayer_three.setPlayWhenReady(false);
            exoPlayer_three.getPlaybackState();
        }
        if (exoPlayer_four != null && exoPlayer_four.getPlayWhenReady()) {
            exoPlayer_four.setPlayWhenReady(false);
            exoPlayer_four.getPlaybackState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer_one != null) {
            exoPlayer_one.setPlayWhenReady(true);
            exoPlayer_one.getPlaybackState();
        }
        if (exoPlayer_tow != null) {
            exoPlayer_tow.setPlayWhenReady(true);
            exoPlayer_tow.getPlaybackState();
        }
        if (exoPlayer_three != null) {
            exoPlayer_three.setPlayWhenReady(true);
            exoPlayer_three.getPlaybackState();
        }
        if (exoPlayer_four != null) {
            exoPlayer_four.setPlayWhenReady(true);
            exoPlayer_four.getPlaybackState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer_one != null) {
            exoPlayer_one.setPlayWhenReady(false);
            exoPlayer_one.stop();
            exoPlayer_one.release();
        }
        if (exoPlayer_tow != null) {
            exoPlayer_tow.setPlayWhenReady(false);
            exoPlayer_tow.stop();
            exoPlayer_tow.release();
        }
        if (exoPlayer_three != null) {
            exoPlayer_three.setPlayWhenReady(false);
            exoPlayer_three.stop();
            exoPlayer_three.release();
        }
        if (exoPlayer_four != null) {
            exoPlayer_four.setPlayWhenReady(false);
            exoPlayer_four.stop();
            exoPlayer_four.release();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            String streamID = data.getStringExtra("stream_id");
            if (streamID != null && !streamID.isEmpty()){
                setPlayerOne(getChannelUrl(streamID));
            }
        } else if (requestCode == 102 && resultCode == Activity.RESULT_OK && data != null) {
            String streamID = data.getStringExtra("stream_id");
            if (streamID != null && !streamID.isEmpty()){
                setPlayerTow(getChannelUrl(streamID));
            }
        } else if (requestCode == 103 && resultCode == Activity.RESULT_OK && data != null) {
            String streamID = data.getStringExtra("stream_id");
            if (streamID != null && !streamID.isEmpty()){
                setPlayerThree(getChannelUrl(streamID));
            }
        } else if (requestCode == 104 && resultCode == Activity.RESULT_OK && data != null) {
            String streamID = data.getStringExtra("stream_id");
            if (streamID != null && !streamID.isEmpty()){
                setPlayerFour(getChannelUrl(streamID));
            }
        }
    }
}