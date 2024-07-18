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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.NetworkUtils;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.adapter.AdapterSingleURL;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.dialog.ExitDialog;
import nemosofts.streambox.dialog.PopupAdsDialog;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.item.ItemSingleURL;

@UnstableApi
public class SingleStreamActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView rv;
    private ArrayList<ItemSingleURL> arrayList;
    private FrameLayout frameLayout;

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

        dbHelper = new DBHelper(this);

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        rv = findViewById(R.id.rv);

        GridLayoutManager grid = new GridLayoutManager(this, 2);
        grid.setSpanCount(2);
        rv.setLayoutManager(grid);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        getData();
        setListener();
        getNetworkInfo();

        new PopupAdsDialog(this);
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_single_stream;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    private void getNetworkInfo() {
        ImageView iv_wifi = findViewById(R.id.iv_wifi);
        if (NetworkUtils.isConnected(this)) {
            if (NetworkUtils.isConnectedMobile(this)){
                iv_wifi.setImageResource(R.drawable.bar_selector_none);
            } else if (NetworkUtils.isConnectedWifi(this)){
                iv_wifi.setImageResource(R.drawable.ic_wifi);
            } else if (NetworkUtils.isConnectedEthernet(this)){
                iv_wifi.setImageResource(R.drawable.ic_ethernet);
            }
        } else {
            iv_wifi.setImageResource(R.drawable.ic_wifi_off);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getData() {
        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    arrayList.addAll(dbHelper.loadSingleURL());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (!arrayList.isEmpty()){
                    setAdapter();
                } else {
                    setEmpty();
                }
            }
        }.execute();
    }

    private void setListener() {
        findViewById(R.id.ll_url_add).setOnClickListener(v -> {
            Intent intent = new Intent(SingleStreamActivity.this, SelectPlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
            startActivity(intent);
            finish();
        });
    }

    public void setAdapter() {
        AdapterSingleURL adapter;
        adapter = new AdapterSingleURL(this,arrayList, (itemCat, position) -> {
            if (NetworkUtils.isConnected(this)){
                new SharedPref(this).setLoginType(Callback.TAG_LOGIN_SINGLE_STREAM);
                Intent intent = new Intent(SingleStreamActivity.this, PlayerSingleURLActivity.class);
                intent.putExtra("channel_title", arrayList.get(position).getAnyName());
                intent.putExtra("channel_url", arrayList.get(position).getSingleURL());
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);
        setEmpty();
    }

    @SuppressLint("MissingInflatedId")
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

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> {

            });

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
        new ExitDialog(this);
    }

    @Override
    public void onDestroy() {
        try {
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}