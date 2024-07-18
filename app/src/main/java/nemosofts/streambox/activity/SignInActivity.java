package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.adapter.AdapterDNS;
import nemosofts.streambox.asyncTask.LoadLogin;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.dialog.ExitDialog;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.interfaces.LoginListener;
import nemosofts.streambox.item.ItemDns;
import nemosofts.streambox.item.ItemUsersDB;
import nemosofts.streambox.view.NSoftsProgressDialog;

public class SignInActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Helper helper;
    private SharedPref sharedPref;
    private EditText et_any_name;
    private EditText et_user_name;
    private EditText et_login_password;
    private EditText et_url;
    private NSoftsProgressDialog progressDialog;
    private Boolean isVisibility = false;
    private LinearLayout ll_url;
    private AdapterDNS adapter;

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

        helper = new Helper(this);
        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);

        progressDialog = new NSoftsProgressDialog(SignInActivity.this);
        progressDialog.setCancelable(false);

        ll_url  = findViewById(R.id.ll_url);
        et_any_name = findViewById(R.id.et_any_name);
        et_user_name = findViewById(R.id.et_user_name);
        et_login_password = findViewById(R.id.et_login_password);
        et_url = findViewById(R.id.et_url);


        ImageView iv_visibility = findViewById(R.id.iv_visibility);
        iv_visibility.setImageResource(Boolean.TRUE.equals(isVisibility) ? R.drawable.ic_login_visibility : R.drawable.ic_login_visibility_off);
        iv_visibility.setOnClickListener(v -> {
            isVisibility = !isVisibility;
            iv_visibility.setImageResource(Boolean.TRUE.equals(isVisibility) ? R.drawable.ic_login_visibility : R.drawable.ic_login_visibility_off);
            et_login_password.setTransformationMethod(Boolean.TRUE.equals(isVisibility) ? HideReturnsTransformationMethod.getInstance()  : PasswordTransformationMethod.getInstance());
        });

        setListener();

        RecyclerView rv_dns = findViewById(R.id.rv_dns);
        rv_dns.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_dns.setLayoutManager(llm);
        rv_dns.setNestedScrollingEnabled(false);

        ArrayList<ItemDns> arrayList = new ArrayList<>();
        arrayList.add(new ItemDns("External",""));
        if (Boolean.TRUE.equals(sharedPref.getIsXUI_DNS())){
            try {
                ArrayList<ItemDns> arrayList2 = new ArrayList<>(dbHelper.loadDNS());
                if (!arrayList2.isEmpty()){
                    arrayList.addAll(arrayList2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter = new AdapterDNS(arrayList, (itemDns, position) -> {
            ll_url.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            adapter.setSelected(position);
        });
        rv_dns.setAdapter(adapter);
        adapter.setSelected(0);
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_sign_in;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    private void setListener() {
        findViewById(R.id.tv_add_user_btn).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.rl_list_users).setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, UsersListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("SetTextI18n")
    private void attemptLogin() {
        et_user_name.setError(null);
        et_login_password.setError(null);
        et_any_name.setError(null);

        if (ll_url.getVisibility() == View.GONE){
            if (adapter.getSelectedBase().isEmpty()){
                et_url.setText("https://nemosofts.com");
            } else {
                et_url.setText(adapter.getSelectedBase());
            }
        }

        // Store values at the time of the login attempt.
        String any_name = et_any_name.getText().toString();
        String user_name = et_user_name.getText().toString();
        String password = et_login_password.getText().toString();
        String url_data = et_url.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            et_login_password.setError(getString(R.string.err_password_sort));
            focusView = et_login_password;
            cancel = true;
        }
        if (et_login_password.getText().toString().endsWith(" ")) {
            et_login_password.setError(getString(R.string.err_pass_end_space));
            focusView = et_login_password;
            cancel = true;
        }

        // Check for a valid user name.
        if (TextUtils.isEmpty(user_name)) {
            et_user_name.setError(getString(R.string.err_cannot_empty));
            focusView = et_user_name;
            cancel = true;
        } else {
            if (TextUtils.isEmpty(any_name)) {
                et_any_name.setText(et_user_name.getText().toString());
            }
        }

        if (TextUtils.isEmpty(url_data)) {
            et_url.setError(getString(R.string.err_cannot_empty));
            focusView = et_url;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loadLogin();
        }
    }

    private void loadLogin() {
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
                            dbHelper.addToUserDB(new ItemUsersDB("", et_any_name.getText().toString(), et_user_name.getText().toString(),
                                        et_login_password.getText().toString(), et_url.getText().toString())
                            );
                            sharedPref.setLoginDetails(
                                    username,password,message,auth,status, exp_date, is_trial, active_cons,created_at,max_connections,
                                    xui,version,revision,url,port,https_port,server_protocol,rtmp_port,timestamp_now,time_now,timezone
                            );
                            sharedPref.setLoginType(Callback.TAG_LOGIN_ONE_UI);
                            sharedPref.setAnyName(et_any_name.getText().toString());
                            sharedPref.setIsFirst(false);
                            sharedPref.setIsLogged(true);
                            sharedPref.setIsAutoLogin(true);

                            Callback.isCustomAds = false;
                            Callback.customAdCount = 0;
                            Callback.customAdShow = 15;
                            Callback.is_load_ads = true;

                            Toast.makeText(SignInActivity.this, "Login successfully.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ApplicationUtil.openThemeActivity(SignInActivity.this);
                    }  else {
                        Toast.makeText(SignInActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            },et_url.getText().toString(), helper.getAPIRequestLogin(et_user_name.getText().toString(), et_login_password.getText().toString()));
            login.execute();
        }  else {
            Toast.makeText(this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    @Contract(pure = true)
    private boolean isPasswordValid(@NonNull String password) {
        return password.length() > 0;
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
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.cancel();
        }
        super.onDestroy();
    }
}