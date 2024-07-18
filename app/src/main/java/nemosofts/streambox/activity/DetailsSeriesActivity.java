package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.view.BlurImage;
import androidx.nemosofts.view.youtubeExtractor.VideoMeta;
import androidx.nemosofts.view.youtubeExtractor.YouTubeExtractor;
import androidx.nemosofts.view.youtubeExtractor.YtFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.AdManagerInter;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.adapter.AdapterEpisodes;
import nemosofts.streambox.adapter.AdapterSeason;
import nemosofts.streambox.asyncTask.LoadSeriesID;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.interfaces.SeriesIDListener;
import nemosofts.streambox.item.series.ItemEpisodes;
import nemosofts.streambox.item.series.ItemInfoSeasons;
import nemosofts.streambox.item.series.ItemSeasons;
import nemosofts.streambox.view.NSoftsProgressDialog;

public class DetailsSeriesActivity extends AppCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private SharedPref sharedPref;
    private NSoftsProgressDialog progressDialog;
    private String series_id = "0";
    private TextView tv_page_title, tv_directed, tv_release, tv_genre, tv_plot;
    private ImageView iv_series;
    private ImageView iv_star_1, iv_star_2, iv_star_3, iv_star_4, iv_star_5;
    private TextView tv_play_trailer;
    private ArrayList<ItemSeasons> arraySeasons;
    private ArrayList<ItemEpisodes> arrayAllEpisodes;
    private ArrayList<ItemEpisodes> arrayEpisodes;
    private RecyclerView rv_episodes;
    private AdapterEpisodes adapterEpisodes;
    private String season_id="0";
    private String youtube ="https://www.youtube.com/watch?v=",youtube_title="" ;
    private ImageView iv_fav;
    private int theme_bg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Boolean.TRUE.equals(Callback.isLandscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        IsStatusBar.ifSupported(this);
        IsScreenshot.ifSupported(this);
        IsRTL.ifSupported(this);

        theme_bg = ApplicationUtil.openThemeBg(this);

        ImageView iv_bg_blur = findViewById(R.id.iv_bg_blur);
        iv_bg_blur.setImageResource(theme_bg);

        ImageView iv_alpha = findViewById(R.id.iv_alpha);
        iv_alpha.setImageResource(theme_bg);

        findViewById(R.id.iv_back_page).setOnClickListener(view -> onBackPressed());

        series_id = getIntent().getStringExtra("series_id");
        String series_name = getIntent().getStringExtra("series_name");
        String series_rating = getIntent().getStringExtra("series_rating");
        String series_cover = getIntent().getStringExtra("series_cover");

        helper = new Helper(this);
        dbHelper = new DBHelper(this);
        sharedPref = new SharedPref(this);

        arraySeasons = new ArrayList<>();
        arrayAllEpisodes = new ArrayList<>();
        arrayEpisodes = new ArrayList<>();

        progressDialog = new NSoftsProgressDialog(DetailsSeriesActivity.this);
        progressDialog.setCancelable(false);

        tv_page_title = findViewById(R.id.tv_page_title);
        iv_series = findViewById(R.id.iv_series);
        tv_directed = findViewById(R.id.tv_directed);
        tv_release = findViewById(R.id.tv_release);
        tv_genre = findViewById(R.id.tv_genre);
        tv_plot = findViewById(R.id.tv_plot);
        tv_play_trailer = findViewById(R.id.tv_play_trailer);
        iv_fav = findViewById(R.id.iv_fav);

        iv_star_1 = findViewById(R.id.iv_star_1);
        iv_star_2 = findViewById(R.id.iv_star_2);
        iv_star_3 = findViewById(R.id.iv_star_3);
        iv_star_4 = findViewById(R.id.iv_star_4);
        iv_star_5 = findViewById(R.id.iv_star_5);

        rv_episodes = findViewById(R.id.rv_episodes);
        rv_episodes.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_episodes.setLayoutManager(llm);
        rv_episodes.setNestedScrollingEnabled(false);

        iv_fav.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(dbHelper.checkFavSeries(series_id))){
                dbHelper.removeFavSeries(series_id);
                iv_fav.setImageResource(R.drawable.ic_favorite_border);
                Toast.makeText(this, getString(R.string.fav_remove_success), Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addToFavSeries(series_name, series_id, series_cover, series_rating);
                iv_fav.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(this, getString(R.string.fav_success), Toast.LENGTH_SHORT).show();
            }
        });

        getData();

        tv_play_trailer.setOnClickListener(v -> showYouTubeExtractor());

        new AdManagerInter(this);
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_details_series;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    private void getData() {
        if (helper.isNetworkAvailable()){
            LoadSeriesID loadSeriesID = new LoadSeriesID(this, new SeriesIDListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, ArrayList<ItemInfoSeasons> arrayListInfo, ArrayList<ItemSeasons> arrayListSeasons, ArrayList<ItemEpisodes> arrayListEpisodes) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (!arrayListInfo.isEmpty()){
                            setInfo(arrayListInfo.get(0));
                        }
                        if (!arrayListEpisodes.isEmpty()){
                            arrayAllEpisodes.addAll(arrayListEpisodes);
                        }
                        if (!arrayListSeasons.isEmpty()){
                            arraySeasons.addAll(arrayListSeasons);
                            setSeasonsAdapter();
                        }
                    }  else {
                        Toast.makeText(DetailsSeriesActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }, helper.getAPIRequestID("get_series_info","series_id", series_id, sharedPref.getUserName(), sharedPref.getPassword()));
            loadSeriesID.execute();
        } else {
            Toast.makeText(DetailsSeriesActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void setSeasonsAdapter() {
        RecyclerView rv_seasons = findViewById(R.id.rv_seasons);
        rv_seasons.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_seasons.setLayoutManager(llm);
        rv_seasons.setNestedScrollingEnabled(false);
        AdapterSeason adapterColors = new AdapterSeason(this, arraySeasons, (itemSeasons, position) -> {
            season_id = arraySeasons.get(position).getSeasonNumber();
            setSeasonAdapter();
        });
        rv_seasons.setAdapter(adapterColors);
        season_id = arraySeasons.get(0).getSeasonNumber();
        setSeasonAdapter();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setSeasonAdapter() {
        if (!arrayAllEpisodes.isEmpty()){

            if (!arrayEpisodes.isEmpty()){
                arrayEpisodes.clear();
            }
            for (int i = 0; i < arrayAllEpisodes.size(); i++) {
                if (arrayAllEpisodes.get(i).getSeason().equals(season_id)){
                    arrayEpisodes.add(arrayAllEpisodes.get(i));
                }
            }

            if (!arrayEpisodes.isEmpty()){
                adapterEpisodes = new AdapterEpisodes(arrayEpisodes, (itemEpisodes, position) -> {
                    @SuppressLint("UnsafeOptInUsageError") Intent intent = new Intent(DetailsSeriesActivity.this, PlayerEpisodesActivity.class);
                    Callback.playPosEpisodes = position;
                    if (!Callback.arrayListEpisodes.isEmpty()) {
                        Callback.arrayListEpisodes.clear();
                    }
                    Callback.arrayListEpisodes.addAll(arrayEpisodes);
                    startActivity(intent);
                });
                rv_episodes.setAdapter(adapterEpisodes);
            } else if (adapterEpisodes != null){
                adapterEpisodes.notifyDataSetChanged();
            }
        }

    }

    private void setInfo(@NonNull ItemInfoSeasons itemInfoSeasons) {
        tv_page_title.setText(itemInfoSeasons.getName());
        tv_directed.setText(itemInfoSeasons.getDirector().isEmpty() || itemInfoSeasons.getDirector().equals("null") ? "N/A" : itemInfoSeasons.getDirector());
        tv_release.setText(itemInfoSeasons.getReleaseDate());
        tv_genre.setText(itemInfoSeasons.getGenre().isEmpty() || itemInfoSeasons.getGenre().equals("null") ? "N/A" : itemInfoSeasons.getGenre());
        tv_plot.setText(itemInfoSeasons.getPlot());

        iv_fav.setImageResource(Boolean.TRUE.equals(dbHelper.checkFavSeries(series_id)) ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

        Picasso.get()
                .load(itemInfoSeasons.getCover().isEmpty() ? "null" : itemInfoSeasons.getCover())
                .placeholder(R.drawable.material_design_default)
                .into(iv_series);

        setRating(itemInfoSeasons.getRating5based());
        setBlur(itemInfoSeasons.getCover().isEmpty() ? "null" : itemInfoSeasons.getCover());

        if (itemInfoSeasons.getYoutubeTrailer().isEmpty()){
            tv_play_trailer.setVisibility(View.GONE);
        } else {
            tv_play_trailer.setVisibility(View.VISIBLE);
            youtube = "https://www.youtube.com/watch?v="+itemInfoSeasons.getYoutubeTrailer();
            youtube_title = itemInfoSeasons.getName();
        }
    }

    private void setBlur(String cover) {
        ImageView imageViewBackground = findViewById(R.id.iv_bg_blur);
        try {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        int blur_amount = 80;
                        imageViewBackground.setImageBitmap(BlurImage.fastBlur(bitmap, 1f, blur_amount));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    imageViewBackground.setImageResource(theme_bg);
                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            imageViewBackground.setTag(target);
            Picasso.get()
                    .load(cover)
                    .placeholder(theme_bg)
                    .into(target);

        } catch (Exception e) {
            e.printStackTrace();
            imageViewBackground.setImageResource(theme_bg);
        }
    }

    private void setRating(@NonNull String rating) {
        String average = ApplicationUtil.averageRating(rating);
        switch (average) {
            case "1":
                iv_star_1.setImageResource(R.drawable.ic_star);
                iv_star_2.setImageResource(R.drawable.ic_star_border);
                iv_star_3.setImageResource(R.drawable.ic_star_border);
                iv_star_4.setImageResource(R.drawable.ic_star_border);
                iv_star_5.setImageResource(R.drawable.ic_star_border);
                break;
            case "2":
                iv_star_1.setImageResource(R.drawable.ic_star);
                iv_star_2.setImageResource(R.drawable.ic_star);
                iv_star_3.setImageResource(R.drawable.ic_star_border);
                iv_star_4.setImageResource(R.drawable.ic_star_border);
                iv_star_5.setImageResource(R.drawable.ic_star_border);
                break;
            case "3":
                iv_star_1.setImageResource(R.drawable.ic_star);
                iv_star_2.setImageResource(R.drawable.ic_star);
                iv_star_3.setImageResource(R.drawable.ic_star);
                iv_star_4.setImageResource(R.drawable.ic_star_border);
                iv_star_5.setImageResource(R.drawable.ic_star_border);
                break;
            case "4":
                iv_star_1.setImageResource(R.drawable.ic_star);
                iv_star_2.setImageResource(R.drawable.ic_star);
                iv_star_3.setImageResource(R.drawable.ic_star);
                iv_star_4.setImageResource(R.drawable.ic_star);
                iv_star_5.setImageResource(R.drawable.ic_star_border);
                break;
            case "5":
                iv_star_1.setImageResource(R.drawable.ic_star);
                iv_star_2.setImageResource(R.drawable.ic_star);
                iv_star_3.setImageResource(R.drawable.ic_star);
                iv_star_4.setImageResource(R.drawable.ic_star);
                iv_star_5.setImageResource(R.drawable.ic_star);
                break;
            default:
                iv_star_1.setImageResource(R.drawable.ic_star_border);
                iv_star_2.setImageResource(R.drawable.ic_star_border);
                iv_star_3.setImageResource(R.drawable.ic_star_border);
                iv_star_4.setImageResource(R.drawable.ic_star_border);
                iv_star_5.setImageResource(R.drawable.ic_star_border);
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void showYouTubeExtractor() {
        progressDialog.show();
        new YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                progressDialog.dismiss();
                if (ytFiles != null) {
                    try {
                        String downloadUrl = ytFiles.get(22).getUrl();
                        @SuppressLint("UnsafeOptInUsageError") Intent intent = new Intent(DetailsSeriesActivity.this, PlayerSingleURLActivity.class);
                        intent.putExtra("channel_title", youtube_title);
                        intent.putExtra("channel_url", downloadUrl);
                        startActivity(intent);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            protected void onCancelled(SparseArray<YtFile> ytFileSparseArray) {
                super.onCancelled(ytFileSparseArray);
                progressDialog.dismiss();
            }
        }.extract(youtube, true, true);
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
    public void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.cancel();
        }
        super.onDestroy();
    }
}