package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.adapter.AdapterSetting;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.item.ItemSetting;
import nemosofts.streambox.view.NSoftsProgressDialog;

public class SettingActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView rv;
    private ArrayList<ItemSetting> arrayList;
    private AdapterSetting adapter;
    private NSoftsProgressDialog progressDialog;
    private String cache_size;

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

        progressDialog = new NSoftsProgressDialog(SettingActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        findViewById(R.id.iv_back_page).setOnClickListener(view -> onBackPressed());

        dbHelper = new DBHelper(this);

        arrayList = new ArrayList<>();

        initializeCache();

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        GridLayoutManager grid = new GridLayoutManager(this, 4);
        grid.setSpanCount(4);
        rv.setLayoutManager(grid);

        addData();
        setAdapterToListview();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_setting;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    private void setAdapterToListview() {
        adapter = new AdapterSetting(this, arrayList, (itemSerials, position) -> setOnClick(position));
        rv.setAdapter(adapter);
    }

    private void setOnClick(int position) {
        try {
            switch (position){
                case 0 :
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    break;
                case 1 :
                    startActivity(new Intent(SettingActivity.this, NotificationsActivity.class));
                    break;
                case 2 :
                    clear_cache();
                    break;
                case 3 :
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                    break;
                case 4 :
                    startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
                    break;
                case 5 :
                    startActivity(new Intent(SettingActivity.this, NetworkSpeedActivity.class));
                    break;
                case 6 :
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addData() {
        if (!arrayList.isEmpty()){
            arrayList.clear();
        }
        arrayList.add(new ItemSetting("1", getString(R.string.wifi_setting), "", R.drawable.ic_wifi));
        arrayList.add(new ItemSetting("2", getString(R.string.notifications), "", R.drawable.ic_round_notifications));
        arrayList.add(new ItemSetting("3", getString(R.string.clear_cache), cache_size, R.drawable.ic_clean_code));
        arrayList.add(new ItemSetting("4", getString(R.string.date_time), "", R.drawable.ic_timer));
        arrayList.add(new ItemSetting("5", getString(R.string.profile), "", R.drawable.ic_profile));
        arrayList.add(new ItemSetting("6", getString(R.string.speed_test), "", R.drawable.ic_speed));
        arrayList.add(new ItemSetting("7", getString(R.string.device_settings), "", R.drawable.ic_player_setting));
    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());
        cache_size = ApplicationUtil.readableFileSize(size);
    }

    private long getDirSize(File dir) {
        long size = 0;
        try {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file != null && file.isDirectory()) {
                    size += getDirSize(file);
                } else if (file != null && file.isFile()) {
                    size += file.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    @SuppressLint("StaticFieldLeak")
    private void clear_cache() {
        if (!cache_size.equals("0 MB")){
            new AsyncTask<String, String, String>() {
                @Override
                protected void onPreExecute() {
                    progressDialog.show();
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(String... strings) {
                    try {
                        FileUtils.deleteQuietly(getCacheDir());
                        FileUtils.deleteQuietly(getExternalCacheDir());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                protected void onPostExecute(String s) {
                    progressDialog.dismiss();
                    cache_size = "0 MB";
                    addData();
                    if (adapter != null){
                        adapter.notifyDataSetChanged();
                    }
                    super.onPostExecute(s);
                }
            }.execute();
        }
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
}