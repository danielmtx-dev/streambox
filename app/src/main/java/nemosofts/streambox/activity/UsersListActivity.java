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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.adapter.AdapterUsers;
import nemosofts.streambox.asyncTask.LoadLogin;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.dialog.ExitDialog;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.interfaces.LoginListener;
import nemosofts.streambox.item.ItemUsersDB;
import nemosofts.streambox.view.NSoftsProgressDialog;

public class UsersListActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private DBHelper dbHelper;
    private RecyclerView rv;
    private ArrayList<ItemUsersDB> arrayList;
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
        IsRTL.ifSupported(this);

        findViewById(R.id.theme_bg).setBackgroundResource(ApplicationUtil.openThemeBg(this));

        helper = new Helper(this);
        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);

        progressDialog = new NSoftsProgressDialog(UsersListActivity.this);
        progressDialog.setCancelable(false);

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        rv = findViewById(R.id.rv);

        GridLayoutManager grid = new GridLayoutManager(this, 2);
        grid.setSpanCount(2);
        rv.setLayoutManager(grid);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        getUserData();
        setListener();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_users_list;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    private void setListener() {
        findViewById(R.id.ll_user_add).setOnClickListener(v -> {
            @SuppressLint("UnsafeOptInUsageError")
            Intent intent = new Intent(UsersListActivity.this, SelectPlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void getUserData() {
        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    arrayList.addAll(dbHelper.loadUsersDB());
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

    public void setAdapter() {
        AdapterUsers adapter = new AdapterUsers(this,arrayList, (itemCat, position) -> loadLogin(arrayList.get(position).getAnyName(), arrayList.get(position).getUseName(),arrayList.get(position).getUserPass(), arrayList.get(position).getUserURL()));
        rv.setAdapter(adapter);
        setEmpty();
    }

    private void loadLogin(String any_name,  String useName, String userPass, String userURL) {
        if (helper.isNetworkAvailable()) {
            LoadLogin login = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String username, String password, String message, int auth, String status, String exp_date, String is_trial, String active_cons, String created_at, String max_connections,
                                  boolean xui, String version, int revision, String url, String port, String https_port, String server_protocol, String rtmp_port, int timestamp_now, String time_now, String timezone) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        try {
                            sharedPref.setLoginDetails(
                                    username,password,message,auth,status, exp_date, is_trial, active_cons,created_at,max_connections,
                                    xui,version,revision,url,port,https_port,server_protocol,rtmp_port,timestamp_now,time_now,timezone
                            );
                            sharedPref.setAnyName(any_name);
                            sharedPref.setIsFirst(false);
                            sharedPref.setIsLogged(true);
                            sharedPref.setIsAutoLogin(true);
                            sharedPref.setLoginType(Callback.TAG_LOGIN_ONE_UI);
                            Toast.makeText(UsersListActivity.this, "Login successfully.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ApplicationUtil.openThemeActivity(UsersListActivity.this);
                    }  else {
                        Toast.makeText(UsersListActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            },userURL, helper.getAPIRequestLogin(useName,userPass));
            login.execute();
        }  else {
            Toast.makeText(this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("InflateParams, UnsafeOptInUsageError")
    private void setEmpty() {
        if (!arrayList.isEmpty()) {
            rv.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_add_user, null);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> {
                Intent intent = new Intent(UsersListActivity.this, SelectPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "");
                startActivity(intent);
                finish();
            });

            frameLayout.addView(myView);
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPressed();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                 Intent intent = new Intent(UsersListActivity.this, SelectPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "");
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        new ExitDialog(this);
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