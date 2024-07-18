package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.metrics.PlaybackStateEvent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.Util.helper.JSHelper;
import nemosofts.streambox.adapter.epg.AdapterEpg;
import nemosofts.streambox.adapter.epg.AdapterLiveEpg;
import nemosofts.streambox.adapter.epg.ItemPost;
import nemosofts.streambox.asyncTask.LoadEpg;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.interfaces.EpgListener;
import nemosofts.streambox.item.live.ItemEpg;
import nemosofts.streambox.item.live.ItemLive;

@UnstableApi
public class EPGActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private JSHelper jsHelper;
    private String cat_id = "0";
    private RecyclerView rv_live;
    private ArrayList<ItemLive> arrayList;
    private SimpleExoPlayer exoPlayer;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;

    ArrayList<ItemPost> arrayListPost = new ArrayList<>();
    ArrayList<ItemEpg> arrayListEpg = new ArrayList<>();
    private AdapterLiveEpg adapter;

    ProgressBar pb;

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

        findViewById(R.id.theme_bg).setBackgroundResource(ApplicationUtil.openThemeBg(this));

        findViewById(R.id.iv_back_page).setOnClickListener(view -> onBackPressed());

        cat_id = getIntent().getStringExtra("cat_id");

        jsHelper = new JSHelper(this);
        sharedPref = new SharedPref(this);
        helper = new Helper(this);

        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(this).build();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        PlayerView playerView = findViewById(R.id.exoPlayerView);
        playerView.setPlayer(exoPlayer);
        playerView.setUseController(true);
        playerView.requestFocus();

        exoPlayer.addListener(new Player.Listener(){

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                playerView.setKeepScreenOn(isPlaying);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == PlaybackStateEvent.STATE_PLAYING) {
                    findViewById(R.id.pb_player).setVisibility(View.GONE);
                } else if (playbackState == Player.STATE_BUFFERING) {
                    findViewById(R.id.pb_player).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                exoPlayer.stop();
                findViewById(R.id.pb_player).setVisibility(View.GONE);
                Toast.makeText(EPGActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Player.Listener.super.onPlayerError(error);
            }
        });

        arrayList = new ArrayList<>();

        pb = findViewById(R.id.pb);
        rv_live = findViewById(R.id.rv_live);
        rv_live.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_live.setLayoutManager(llm);
        rv_live.setNestedScrollingEnabled(false);

        getData();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_epg;
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
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    itemLives.addAll(jsHelper.getLive(cat_id, false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (itemLives.isEmpty()) {
                    findViewById(R.id.ll_epg).setVisibility(View.GONE);
                    findViewById(R.id.ll_epg_empty).setVisibility(View.VISIBLE);
                } else {
                    arrayList.addAll(itemLives);
                    setAdapterToListview();
                }
            }
        }.execute();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterToListview() {
        adapter = new AdapterLiveEpg(this, arrayList, (itemCat, position) -> {
            adapter.select(position);
            setMediaSource(position);
        });
        rv_live.setAdapter(adapter);
        setMediaSource(0);
        adapter.select(0);
    }

    private void setMediaSource(int playPos) {
        String channelUrl = "";
        if (Boolean.TRUE.equals(sharedPref.getIsXuiUser())){
            channelUrl = sharedPref.getServerURL()+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+arrayList.get(playPos).getStreamID()+".m3u8";
        } else {
            channelUrl = sharedPref.getServerURL()+"live/"+sharedPref.getUserName()+"/"+sharedPref.getPassword()+"/"+arrayList.get(playPos).getStreamID()+".m3u8";
        }
        Uri uri = Uri.parse(channelUrl);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        if (!arrayListPost.isEmpty()){
            arrayListPost.clear();
        }
        if (!arrayListEpg.isEmpty()){
            arrayListEpg.clear();
        }

        ItemPost itemPost = new ItemPost("1","logo");
        ArrayList<ItemLive> arrayListLive = new ArrayList<>();
        arrayListLive.add(arrayList.get(playPos));
        itemPost.setArrayListLive(arrayListLive);
        arrayListPost.add(itemPost);

        getEpgData(playPos);
    }

    private void getEpgData(int playPos) {
        if (helper.isNetworkAvailable()){
            LoadEpg loadSeriesID = new LoadEpg(this, new EpgListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, ArrayList<ItemEpg> epgArrayList) {
                    pb.setVisibility(View.GONE);
                    if (!epgArrayList.isEmpty()){
                        arrayListEpg.addAll(epgArrayList);
                    }
                    setEpg();
                }
            }, helper.getAPIRequestID("get_simple_data_table","stream_id", arrayList.get(playPos).getStreamID(), sharedPref.getUserName(), sharedPref.getPassword()));
            loadSeriesID.execute();
        } else {
            Toast.makeText(EPGActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void setEpg() {
        ItemPost itemPost2 = new ItemPost("2","listings");
        if (!arrayListEpg.isEmpty()){
            itemPost2.setArrayListEpg(arrayListEpg);
        } else {
            ArrayList<ItemEpg> arrayListEp = new ArrayList<>();
            arrayListEp.add(new ItemEpg("","", ApplicationUtil.encodeBase64("No Data Found"),"","","",""));
            itemPost2.setArrayListEpg(arrayListEp);
        }
        arrayListPost.add(itemPost2);

        RecyclerView rv_home = findViewById(R.id.rv_epg);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_home.setLayoutManager(llm);
        rv_home.setItemAnimator(new DefaultItemAnimator());
        rv_home.setHasFixedSize(true);
        AdapterEpg adapterHome = new AdapterEpg(this, arrayListPost);
        rv_home.setAdapter(adapterHome);
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
        return new DefaultDataSourceFactory(EPGActivity.this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(EPGActivity.this, "ExoPlayerDemo"))
                .setTransferListener(bandwidthMeter)
                .setAllowCrossProtocolRedirects(true)
                .setKeepPostFor302Redirects(true);
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
}