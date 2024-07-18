package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.dialog.ExitDialog;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.item.ItemSingleURL;
import nemosofts.streambox.view.NSoftsProgressDialog;

@UnstableApi
public class AddSingleURLActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private SharedPref sharedPref;
    private EditText et_any_name;
    private EditText et_url;
    private NSoftsProgressDialog progressDialog;

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

        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);

        progressDialog = new NSoftsProgressDialog(AddSingleURLActivity.this);
        progressDialog.setCancelable(false);

        et_any_name = findViewById(R.id.et_any_name);
        et_url = findViewById(R.id.et_url);

        findViewById(R.id.tv_add_video_btn).setOnClickListener(v -> addURL());
        findViewById(R.id.rl_list_single).setOnClickListener(view -> {
            Intent intent = new Intent(AddSingleURLActivity.this, SingleStreamActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.rl_list_single).setFocusableInTouchMode(false);

        findViewById(R.id.theme_bg).setBackgroundResource(ApplicationUtil.openThemeBg(AddSingleURLActivity.this));

    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_add_single_url;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    private void addURL() {
        et_any_name.setError(null);
        et_url.setError(null);

        // Store values at the time of the login attempt.
        String any_name = et_any_name.getText().toString();
        String video_url = et_url.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid any name.
        if (TextUtils.isEmpty(any_name)) {
            et_any_name.setError(getString(R.string.err_cannot_empty));
            focusView = et_any_name;
            cancel = true;
        }

        // Check for a valid url.
        if (TextUtils.isEmpty(video_url)) {
            et_url.setError(getString(R.string.err_cannot_empty));
            focusView = et_url;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            playVideo();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void playVideo() {
        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    dbHelper.addToSingleURL(new ItemSingleURL("", et_any_name.getText().toString(), et_url.getText().toString()));
                    return "1";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "0";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (s.equals("1")){
                    sharedPref.setLoginType(Callback.TAG_LOGIN_SINGLE_STREAM);
                    new Handler().postDelayed(() -> {
                        progressDialog.dismiss();
                        Intent intent = new Intent(AddSingleURLActivity.this, SingleStreamActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }, 500);
                } else {
                    progressDialog.dismiss();
                }
                super.onPostExecute(s);

            }
        }.execute();
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
        try {
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}