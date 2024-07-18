package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.AdManagerInter;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.recycler.EndlessRecyclerViewScrollListener;
import nemosofts.streambox.adapter.AdapterCategory;
import nemosofts.streambox.adapter.AdapterMovie;
import nemosofts.streambox.asyncTask.GetCategory;
import nemosofts.streambox.asyncTask.GetMovies;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.dialog.FilterDialog;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.interfaces.GetCategoryListener;
import nemosofts.streambox.interfaces.GetMovieListener;
import nemosofts.streambox.item.ItemCat;
import nemosofts.streambox.item.movie.ItemMovies;
import nemosofts.streambox.view.NSoftsProgressDialog;

public class MovieActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private NSoftsProgressDialog progressDialog;
    // Category
    private AdapterCategory adapter_category;
    private RecyclerView rv_cat;
    private ArrayList<ItemCat> arrayListCat;
    // Movies
    private Boolean isOver = false, isScroll = false, isLoading = false;
    private int page = 1;
    private String cat_id = "0";
    private AdapterMovie adapter;
    private ArrayList<ItemMovies> arrayList;
    private RecyclerView rv;
    private ProgressBar pb;
    private Boolean is_fav = false;
    private GetMovies loadMovies;
    private int pos = 1;

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

        new AdManagerInter(this);

        progressDialog = new NSoftsProgressDialog(MovieActivity.this);
        progressDialog.setCancelable(false);

        arrayList = new ArrayList<>();
        arrayListCat = new ArrayList<>();

        TextView page_title = findViewById(R.id.tv_page_title);
        page_title.setText(getString(R.string.movies_home));

        pb = findViewById(R.id.pb);
        frameLayout = findViewById(R.id.fl_empty);
        rv = findViewById(R.id.rv);
        GridLayoutManager grid = new GridLayoutManager(this, 1);
        grid.setSpanCount(5);
        rv.setLayoutManager(grid);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (Boolean.FALSE.equals(is_fav) && (Boolean.FALSE.equals(isOver) && (Boolean.FALSE.equals(isLoading)))) {
                    isLoading = true;
                    new Handler().postDelayed(() -> {
                        isScroll = true;
                        getData();
                    }, 0);
                }
            }
        });

        rv_cat = findViewById(R.id.rv_cat);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_cat.setLayoutManager(llm);
        rv_cat.setItemAnimator(new DefaultItemAnimator());
        rv_cat.setHasFixedSize(true);

        findViewById(R.id.iv_filter).setOnClickListener(v -> new FilterDialog(this, 2, () -> new Handler().postDelayed(() -> recreate_data(pos), 0)));

        new Handler().postDelayed(this::getDataCat, 0);
    }

    private void getDataCat() {
        GetCategory category = new GetCategory(this, 2, new GetCategoryListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, ArrayList<ItemCat> itemCat) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    if (itemCat.isEmpty()) {
                        setEmpty();
                    } else {
                        arrayListCat.add(new ItemCat("",getString(R.string.favourite)));
                        arrayListCat.addAll(itemCat);
                        cat_id = itemCat.get(0).getId();
                        setAdapterToCatListview();
                    }
                } else {
                    setEmpty();
                }
            }
        });
        category.execute();
    }

    private void getData() {
        loadMovies = new GetMovies(this, page,  cat_id, is_fav, new GetMovieListener() {
            @Override
            public void onStart() {
                if (arrayList.isEmpty()){
                    pb.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onEnd(String success, ArrayList<ItemMovies> arrayListMovies) {
                if (Boolean.FALSE.equals(isOver)){
                    pb.setVisibility(View.GONE);
                    if (success.equals("1")) {
                        if (arrayListMovies.isEmpty()) {
                            isOver = true;
                            setEmpty();
                        } else {
                            page = page + 1;
                            arrayList.addAll(arrayListMovies);
                            setAdapterToListview();
                        }
                    } else {
                        setEmpty();
                    }
                    isLoading = false;
                }
            }
        });
        loadMovies.execute();
    }

    public void setAdapterToCatListview() {
        adapter_category = new AdapterCategory(this, arrayListCat, (item, position) -> new Handler().postDelayed(() -> recreate_data(position), 0));
        rv_cat.setAdapter(adapter_category);
        adapter_category.select(1);
        pos = 1;
        getData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void recreate_data(int position) {
        pos = position;
        cat_id = arrayListCat.get(position).getId();
        adapter_category.select(position);
        if (loadMovies != null){
            loadMovies.cancel(true);
        }
        if (!arrayList.isEmpty()){
            arrayList.clear();
        }
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
        isOver = true;
        new Handler().postDelayed(() -> {
            if (!arrayList.isEmpty()){
                arrayList.clear();
            }
            is_fav = position == 0;
            isOver = false;
            isScroll = false;
            isLoading = false;
            page = 1;
            getData();
        }, 0);
    }

    @SuppressLint("UnsafeOptInUsageError")
    public void setAdapterToListview() {
        if(Boolean.FALSE.equals(isScroll)) {
            adapter = new AdapterMovie(this, arrayList, (itemCat, position) -> {
                Intent intent = new Intent(this, DetailsMovieActivity.class);
                intent.putExtra("stream_id", arrayList.get(position).getStreamID());
                intent.putExtra("stream_name", arrayList.get(position).getName());
                intent.putExtra("stream_icon", arrayList.get(position).getStreamIcon());
                intent.putExtra("stream_rating", arrayList.get(position).getRating());
                startActivity(intent);
            });
            rv.setAdapter(adapter);
            setEmpty();
        } else {
            adapter.notifyItemInserted(arrayList.size()-1);
        }
    }

    @SuppressLint("InflateParams")
    private void setEmpty() {
        if (!arrayList.isEmpty()) {
            rv.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(getString(R.string.err_no_data_found));

            frameLayout.addView(myView);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_live_tv;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
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