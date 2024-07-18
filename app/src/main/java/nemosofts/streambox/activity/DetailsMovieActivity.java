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
import androidx.media3.common.util.UnstableApi;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.view.BlurImage;
import androidx.nemosofts.view.youtubeExtractor.VideoMeta;
import androidx.nemosofts.view.youtubeExtractor.YouTubeExtractor;
import androidx.nemosofts.view.youtubeExtractor.YtFile;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.AdManagerInter;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.asyncTask.LoadMovieID;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.interfaces.MovieIDListener;
import nemosofts.streambox.item.movie.ItemInfoMovies;
import nemosofts.streambox.item.movie.ItemMoviesData;
import nemosofts.streambox.view.NSoftsProgressDialog;

@UnstableApi
public class DetailsMovieActivity extends AppCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private SharedPref sharedPref;
    private ItemInfoMovies itemMovies;
    private ItemMoviesData itemData;
    private ImageView iv_poster;
    private ImageView iv_star_1, iv_star_2, iv_star_3, iv_star_4, iv_star_5;
    private TextView tv_page_title, tv_directed, tv_release, tv_duration, tv_genre, tv_cast, tv_plot;
    private TextView tv_play_trailer;
    private NSoftsProgressDialog progressDialog;
    private String stream_id, stream_name, stream_icon, stream_rating;
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

        stream_id = getIntent().getStringExtra("stream_id");
        stream_name = getIntent().getStringExtra("stream_name");
        stream_icon = getIntent().getStringExtra("stream_icon");
        stream_rating = getIntent().getStringExtra("stream_rating");

        progressDialog = new NSoftsProgressDialog(DetailsMovieActivity.this);
        progressDialog.setCancelable(false);

        helper = new Helper(this);
        dbHelper = new DBHelper(this);
        sharedPref = new SharedPref(this);

        iv_poster = findViewById(R.id.iv_poster);
        tv_page_title = findViewById(R.id.tv_page_title);
        iv_fav = findViewById(R.id.iv_fav);

        tv_directed = findViewById(R.id.tv_directed);
        tv_release = findViewById(R.id.tv_release);
        tv_duration = findViewById(R.id.tv_duration);
        tv_genre = findViewById(R.id.tv_genre);
        tv_cast = findViewById(R.id.tv_cast);
        tv_plot = findViewById(R.id.tv_plot);

        iv_star_1 = findViewById(R.id.iv_star_1);
        iv_star_2 = findViewById(R.id.iv_star_2);
        iv_star_3 = findViewById(R.id.iv_star_3);
        iv_star_4 = findViewById(R.id.iv_star_4);
        iv_star_5 = findViewById(R.id.iv_star_5);
        tv_play_trailer = findViewById(R.id.tv_play_trailer);

        tv_play_trailer.setOnClickListener(v -> showYouTubeExtractor());
        findViewById(R.id.tv_play_movie).setOnClickListener(v -> play());

        iv_fav.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(dbHelper.checkFavMovie(stream_id))){
                dbHelper.removeFavMovie(stream_id);
                iv_fav.setImageResource(R.drawable.ic_favorite_border);
                Toast.makeText(this, getString(R.string.fav_remove_success), Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addToFavMovie(stream_id, stream_name, stream_icon, stream_rating);
                iv_fav.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(this, getString(R.string.fav_success), Toast.LENGTH_SHORT).show();
            }
        });

        getData();

        new AdManagerInter(this);
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_details_movie;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    private void getData() {
        if (helper.isNetworkAvailable()){
            LoadMovieID loadSeriesID = new LoadMovieID(this, new MovieIDListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, ArrayList<ItemInfoMovies> arrayListInfo, ArrayList<ItemMoviesData> arrayListMoviesData) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (!arrayListInfo.isEmpty()){
                            itemMovies = arrayListInfo.get(0);
                        } else {
                            itemMovies = new ItemInfoMovies(stream_name,"N/A",stream_icon,"",
                                    "","","N/A","N/A","N/A","","","",
                                    "N/A","","","",stream_rating);
                        }
                        setInfo();
                        if (!arrayListMoviesData.isEmpty()){
                            itemData =  arrayListMoviesData.get(0);
                        }
                    }  else {
                        Toast.makeText(DetailsMovieActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }, helper.getAPIRequestID("get_vod_info","vod_id" ,stream_id, sharedPref.getUserName(), sharedPref.getPassword()));
            loadSeriesID.execute();
        } else {
            Toast.makeText(DetailsMovieActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void setInfo() {
        Picasso.get()
                .load(itemMovies.getMovieImage().isEmpty() ? "null" : itemMovies.getMovieImage())
                .placeholder(R.drawable.material_design_default)
                .into(iv_poster);

        setRating(itemMovies.getRating());

        iv_fav.setImageResource(Boolean.TRUE.equals(dbHelper.checkFavMovie(stream_id)) ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

        tv_page_title.setText(itemMovies.getName());
        tv_directed.setText(itemMovies.getDirector().isEmpty() || itemMovies.getDirector().equals("null") ? "N/A" : itemMovies.getDirector());
        tv_release.setText(itemMovies.getReleaseDate());
        tv_genre.setText(itemMovies.getGenre());
        tv_cast.setText(itemMovies.getCast());
        tv_duration.setText(ApplicationUtil.TimeFormat(itemMovies.getEpisodeRunTime()));
        tv_plot.setText(itemMovies.getPlot());

        tv_play_trailer.setVisibility(itemMovies.getYoutubeTrailer().isEmpty() ? View.GONE : View.VISIBLE);
        setBlur();
    }

    private void setBlur() {
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
                    .load(itemMovies.getMovieImage().isEmpty() ? "null" : itemMovies.getMovieImage())
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

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void play() {
        if (itemData != null){
            Intent intent = new Intent(DetailsMovieActivity.this, PlayerMovieActivity.class);
            intent.putExtra("stream_id", itemData.getStreamID());
            intent.putExtra("movie_name", itemData.getName());
            intent.putExtra("container", itemData.getContainerExtension());
            startActivity(intent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void showYouTubeExtractor() {
        progressDialog.show();
        String youtube = "https://www.youtube.com/watch?v="+itemMovies.getYoutubeTrailer();
        new YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                progressDialog.dismiss();
                if (ytFiles != null) {
                    try {
                        String downloadUrl = ytFiles.get(22).getUrl();
                        Intent intent = new Intent(DetailsMovieActivity.this, PlayerSingleURLActivity.class);
                        intent.putExtra("channel_title", itemMovies.getName());
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