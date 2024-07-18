package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.dash.DefaultDashChunkSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.smoothstreaming.DefaultSsChunkSource;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.AdManagerInter;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.NetworkUtils;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.JSHelper;
import nemosofts.streambox.adapter.AdapterRadio;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.item.live.ItemLive;
import nemosofts.streambox.view.NSoftsProgressDialog;

@UnstableApi
public class RadioActivity extends AppCompatActivity implements View.OnClickListener, Player.Listener {

    private SharedPref sharedPref;
    private JSHelper jsHelper;
    private FrameLayout frameLayout;
    private RecyclerView rv;
    private ArrayList<ItemLive> arrayList;
    private NSoftsProgressDialog progressDialog;

    ImageView iv_min_previous, iv_min_play, iv_min_next;
    ProgressBar pb_min;
    TextView tv_radio_cat;

    Boolean isNewSong = false;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;
    public static ExoPlayer exoPlayer = null;
    AudioManager mAudioManager;
    PowerManager.WakeLock mWakeLock;

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

        findViewById(R.id.iv_back_page).setOnClickListener(view -> onBackPressed());

        new AdManagerInter(this);

        progressDialog = new NSoftsProgressDialog(RadioActivity.this);
        progressDialog.setCancelable(false);

        jsHelper = new JSHelper(this);
        sharedPref = new SharedPref(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        try {
            registerReceiver(onCallIncome, new IntentFilter("android.intent.action.PHONE_STATE"));
            registerReceiver(onHeadPhoneDetect, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(this).build();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.addListener(this);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.setReferenceCounted(false);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build();
        exoPlayer.setAudioAttributes(audioAttributes, true);

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        rv = findViewById(R.id.rv);

       tv_radio_cat = findViewById(R.id.tv_radio_cat_name);

        iv_min_previous = findViewById(R.id.iv_min_previous);
        iv_min_play = findViewById(R.id.iv_min_play);
        iv_min_next = findViewById(R.id.iv_min_next);
        pb_min = findViewById(R.id.pb_min);

        iv_min_play.setOnClickListener(this);
        iv_min_next.setOnClickListener(this);
        iv_min_previous.setOnClickListener(this);

        GridLayoutManager grid = new GridLayoutManager(this, 6);
        grid.setSpanCount(6);
        rv.setLayoutManager(grid);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        new Handler().postDelayed(this::getData, 0);
    }

    public void isBuffering(Boolean isBuffer) {
        if (isBuffer != null){
            if (Boolean.FALSE.equals(isBuffer)) {
                pb_min.setVisibility(View.INVISIBLE);
                changePlayPauseIcon(true);
            } else {
                pb_min.setVisibility(View.VISIBLE);
            }
            iv_min_next.setEnabled(!isBuffer);
            iv_min_previous.setEnabled(!isBuffer);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void changePlayPauseIcon(Boolean isPlay) {
        if (Boolean.FALSE.equals(isPlay)) {
            iv_min_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        } else {
            iv_min_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.iv_min_play:
                playPause();
                break;
            case R.id.iv_min_next:
                next();
                break;
            case R.id.iv_min_previous:
                previous();
                break;
            default:
                break;
        }
    }

    private void startNewSong() {
        setPlayer();
        isNewSong = true;
        isBuffering(true);
        String finalUrl = "";
        if (Boolean.TRUE.equals(sharedPref.getIsXuiUser())){
            finalUrl = sharedPref.getServerURL()+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+Callback.arrayList_play.get(Callback.playPos).getStreamID()+".m3u8";
        } else {
            finalUrl = sharedPref.getServerURL()+"live/"+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+Callback.arrayList_play.get(Callback.playPos).getStreamID()+".m3u8";
        }
        Uri uri = Uri.parse(finalUrl);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

    private void togglePlay() {
        exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
        changePlayPauseIcon(exoPlayer.getPlayWhenReady());
    }

    public void playPause() {
        if (!Callback.arrayList_play.isEmpty()) {
            if (Boolean.TRUE.equals(Callback.isPlayed)) {
                togglePlay();
            } else {
                startNewSong();
            }
        } else {
            Toast.makeText(RadioActivity.this, getString(R.string.error_no_radio_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void next() {
        if (!Callback.arrayList_play.isEmpty()) {
            if (NetworkUtils.isConnected(this)) {
                isNewSong = true;
                isBuffering(true);
                if (Callback.playPos < (Callback.arrayList_play.size() - 1)) {
                    Callback.playPos = Callback.playPos + 1;
                } else {
                    Callback.playPos = 0;
                }
                startNewSong();
            } else {
                Toast.makeText(RadioActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RadioActivity.this, getString(R.string.error_no_radio_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void previous() {
        if (!Callback.arrayList_play.isEmpty()) {
            if (NetworkUtils.isConnected(this)) {
                isNewSong = true;
                isBuffering(true);
                if (Callback.playPos > 0) {
                    Callback.playPos = Callback.playPos - 1;
                } else {
                    Callback.playPos = Callback.arrayList_play.size() - 1;
                }
                startNewSong();
            } else {
                Toast.makeText(RadioActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RadioActivity.this, getString(R.string.error_no_radio_selected), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_radio;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @SuppressLint("StaticFieldLeak")
    private void getData() {
        new AsyncTask<String, String, String>() {

            final ArrayList<ItemLive> itemLives = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    itemLives.addAll(jsHelper.getLive("", true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                if (itemLives.isEmpty()) {
                    setEmpty();
                } else {
                    arrayList.addAll(itemLives);
                    setAdapterToListview();
                }
            }
        }.execute();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterToListview() {
        Callback.playPos = 0;
        Callback.arrayList_play.addAll(arrayList);
        setPlayer();
        AdapterRadio adapter = new AdapterRadio(this, arrayList, (itemRadio, position) -> {
            Callback.playPos = position;
            if (!Callback.arrayList_play.isEmpty()){
                Callback.arrayList_play.clear();
            }
            Callback.arrayList_play.addAll(arrayList);
            startNewSong();
        });
        rv.setAdapter(adapter);
        setEmpty();
    }

    private void setPlayer() {
        ImageView iv_radio = findViewById(R.id.iv_radio_logo);
        TextView tv_radio_name = findViewById(R.id.tv_radio_name);
        tv_radio_name.setText(Callback.arrayList_play.get(Callback.playPos).getName());

        try {
            Picasso.get()
                    .load(Callback.arrayList_play.get(Callback.playPos).getStreamIcon().isEmpty() ? "null" : Callback.arrayList_play.get(Callback.playPos).getStreamIcon())
                    .resize(300, 300)
                    .centerCrop()
                    .placeholder(R.drawable.logo)
                    .into(iv_radio);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEmpty() {
        if (!arrayList.isEmpty()) {
            rv.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            @SuppressLint("InflateParams") View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(getString(R.string.err_no_data_found));

            frameLayout.addView(myView);
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

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
        if (playbackState == Player.STATE_READY) {
            exoPlayer.play();
            if (Boolean.TRUE.equals(isNewSong)) {
                isNewSong = false;
                Callback.isPlayed = true;
                isBuffering(false);
            }
        }
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        Player.Listener.super.onIsPlayingChanged(isPlaying);
        changePlayPauseIcon(isPlaying);
        if (isPlaying) {
            if (!mWakeLock.isHeld()) {
                mWakeLock.acquire(60000);
            }
        } else {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
    }

    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        Player.Listener.super.onPlayerError(error);
        Toast.makeText(getApplicationContext(), "Failed : " + error.getErrorCodeName(), Toast.LENGTH_SHORT).show();
        exoPlayer.setPlayWhenReady(false);
        isBuffering(false);
        changePlayPauseIcon(false);
    }

    @Override
    public void onMediaMetadataChanged(@NonNull MediaMetadata mediaMetadata) {
        try {
            String metadata = String.valueOf(mediaMetadata.title);
            tv_radio_cat.setText(metadata.isEmpty() || metadata.equals("null") ? getString(R.string.app_name) : metadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Player.Listener.super.onMediaMetadataChanged(mediaMetadata);
    }

    BroadcastReceiver onCallIncome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String a = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            try {
                if (exoPlayer.getPlayWhenReady()) {
                    assert a != null;
                    if (a.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) || a.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        exoPlayer.setPlayWhenReady(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    BroadcastReceiver onHeadPhoneDetect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        try {
            if (exoPlayer.getPlayWhenReady()) {
                togglePlay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    };

    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = focusChange -> {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                try {
                    if (exoPlayer.getPlayWhenReady()) {
                        togglePlay();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    };

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
        try {
            Callback.isPlayed = false;

            exoPlayer.setPlayWhenReady(false);
            changePlayPauseIcon(false);
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;

            try {
                mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
                unregisterReceiver(onCallIncome);
                unregisterReceiver(onHeadPhoneDetect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        try {
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.cancel();
            }
            try {
                Callback.isPlayed = false;

                exoPlayer.setPlayWhenReady(false);
                changePlayPauseIcon(false);
                exoPlayer.stop();
                exoPlayer.release();
                exoPlayer = null;

                try {
                    mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
                    unregisterReceiver(onCallIncome);
                    unregisterReceiver(onHeadPhoneDetect);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}