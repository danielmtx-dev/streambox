package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.AdManagerInter;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.adapter.AdapterCat;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.item.ItemCat;
import nemosofts.streambox.view.NSoftsProgressDialog;

public class CategoriesActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView rv;
    private ArrayList<ItemCat> arrayList;
    private FrameLayout frameLayout;
    private NSoftsProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Boolean.TRUE.equals(Callback.isLandscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        IsStatusBar.ifSupported(this);
        IsScreenshot.ifSupported(this);

        findViewById(R.id.theme_bg).setBackgroundResource(ApplicationUtil.openThemeBg(this));

        findViewById(R.id.iv_back_page).setOnClickListener(view -> onBackPressed());

        progressDialog = new NSoftsProgressDialog(CategoriesActivity.this);
        progressDialog.setCancelable(false);

        dbHelper = new DBHelper(this);

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        rv = findViewById(R.id.rv);
        GridLayoutManager grid = new GridLayoutManager(this, 3);
        grid.setSpanCount(3);
        rv.setLayoutManager(grid);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        getData();

        new AdManagerInter(this);
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_categories;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @SuppressLint("StaticFieldLeak")
    private void getData() {
        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    arrayList.addAll(dbHelper.getCategoryLive(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                if (!arrayList.isEmpty()){
                    setAdapterToListview();
                } else {
                    setEmpty();
                }
            }
        }.execute();
    }

    @SuppressLint("UnsafeOptInUsageError")
    public void setAdapterToListview() {
        AdapterCat adapter = new AdapterCat(arrayList, (itemCat, position) -> {
            Intent intent = new Intent(this, EPGActivity.class);
            intent.putExtra("cat_id", arrayList.get(position).getId());
            intent.putExtra("cat_name", arrayList.get(position).getName());
            startActivity(intent);
        });
        rv.setAdapter(adapter);
        setEmpty();
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

            frameLayout.addView(myView);
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
    public void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.cancel();
        }
        try {
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}