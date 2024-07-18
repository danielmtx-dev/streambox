package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.nemosofts.envato.EnvatoProduct;
import androidx.nemosofts.envato.interfaces.EnvatoListener;

import org.json.JSONArray;

import nemosofts.streambox.BuildConfig;
import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.NetworkUtils;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.asyncTask.LoadAbout;
import nemosofts.streambox.asyncTask.LoadData;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.interfaces.AboutListener;
import nemosofts.streambox.interfaces.DataListener;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements EnvatoListener {

    Helper helper;
    SharedPref sharedPref;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Boolean.TRUE.equals(Callback.isLandscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        IsStatusBar.ifSupported(this);
        hideStatusBar();

        findViewById(R.id.theme_bg).setBackgroundResource(ApplicationUtil.openThemeBg(this));

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        pb = findViewById(R.id.pb_splash);

        loadAboutData();
    }

    private void loadAboutData() {
        if (helper.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message){
                    pb.setVisibility(View.GONE);
                    if (success.equals("1")){
                        setSaveData();
                    } else {
                        if (Boolean.TRUE.equals(sharedPref.getIsAboutDetails())){
                            setSaveData();
                        } else {
                            errorDialog(getString(R.string.err_server_error), getString(R.string.err_server_not_connected));
                        }
                    }
                }
            });
            loadAbout.execute();
        } else {
            if (Boolean.TRUE.equals(sharedPref.getIsAboutDetails())){
                setSaveData();
            } else {
                errorDialog(getString(R.string.err_internet_not_connected), getString(R.string.err_connect_net_try));
            }
        }
    }

    private void loadSettings() {
        if (Boolean.FALSE.equals(sharedPref.getIsAboutDetails())){
            sharedPref.setAboutDetails(true);
        }
        if (Boolean.TRUE.equals(Callback.isAppUpdate) && Callback.app_new_version != BuildConfig.VERSION_CODE){
            openDialogActivity(Callback.DIALOG_TYPE_UPDATE);
        } else if(Boolean.TRUE.equals(sharedPref.getIsMaintenance())){
            openDialogActivity(Callback.DIALOG_TYPE_MAINTENANCE);
        } else {
            if (sharedPref.getLoginType().equals(Callback.TAG_LOGIN_SINGLE_STREAM)){
                new Handler().postDelayed(this::openSingleStream, 2000);
            } else if (sharedPref.getLoginType().equals(Callback.TAG_LOGIN_ONE_UI)){
                if (Boolean.TRUE.equals(sharedPref.getIsFirst())) {
                    new Handler().postDelayed(this::openSelectPlayer, 2000);
                } else {
                    if (Boolean.FALSE.equals(sharedPref.getIsAutoLogin())) {
                        new Handler().postDelayed(this::openSelectPlayer, 2000);
                    } else {
                        get_data();
                    }
                }
            } else {
                new Handler().postDelayed(this::openSelectPlayer, 2000);
            }
        }
    }

    private void openDialogActivity(String type) {
        Intent intent = new Intent(SplashActivity.this, DialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", type);
        startActivity(intent);
        finish();
    }

    private void get_data() {
        if (NetworkUtils.isConnected(this)){
            LoadData loadData = new LoadData(this, new DataListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, JSONArray arrayLive, JSONArray arraySeries, JSONArray arrayMovies) {
                    pb.setVisibility(View.GONE);
                    if (success.equals("1")){
                        Toast.makeText(SplashActivity.this,"Added", Toast.LENGTH_LONG).show();
                    }
                    ApplicationUtil.openThemeActivity(SplashActivity.this);
                }
            });
            loadData.execute();
        } else {
            ApplicationUtil.openThemeActivity(SplashActivity.this);
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void openSelectPlayer() {
        Intent intent = new Intent(SplashActivity.this, SelectPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "");
        startActivity(intent);
        finish();
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void openSingleStream() {
        Intent intent = new Intent(SplashActivity.this, SingleStreamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStartPairing() {
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected() {
        pb.setVisibility(View.GONE);
        loadSettings();
    }

    @Override
    public void onUnauthorized(String message) {
        pb.setVisibility(View.GONE);
        errorDialog(getString(R.string.err_unauthorized_access), message);
    }

    @Override
    public void onReconnect() {
        Toast.makeText(SplashActivity.this, "Please wait a minute", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        pb.setVisibility(View.GONE);
        errorDialog(getString(R.string.err_server_error), getString(R.string.err_server_not_connected));
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        if (title.equals(getString(R.string.err_internet_not_connected))) {
            alertDialog.setNegativeButton(getString(R.string.retry), (dialog, which) -> loadSettings());
        }
        alertDialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    finish();
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER){
                    if (title.equals(getString(R.string.err_internet_not_connected))) {
                        loadSettings();
                    } else {
                        finish();
                    }
                }
                return false;
            }
            return false;
        });
        alertDialog.setPositiveButton(getString(R.string.exit), (dialog, which) -> finish());
        alertDialog.show();
    }

    private void setSaveData() {
        new EnvatoProduct(this, this).execute();
    }
}