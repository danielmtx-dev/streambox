package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import java.util.Objects;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.dialog.DModeDialog;
import nemosofts.streambox.dialog.MaintenanceDialog;
import nemosofts.streambox.dialog.UpgradeDialog;
import nemosofts.streambox.dialog.VpnDialog;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;

public class DialogActivity extends AppCompatActivity {

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

        String from = getIntent().getStringExtra("from");
        switch (Objects.requireNonNull(from)) {
            case Callback.DIALOG_TYPE_UPDATE:
                new UpgradeDialog(this, new UpgradeDialog.UpgradeListener() {
                    @Override
                    public void onCancel() {
                        openMainActivity();
                    }

                    @Override
                    public void onDo() {

                    }
                });
                break;
            case Callback.DIALOG_TYPE_MAINTENANCE:
                new MaintenanceDialog(this);
                break;
            case Callback.DIALOG_TYPE_DEVELOPER:
                new DModeDialog(this);
                break;
            case Callback.DIALOG_TYPE_VPN:
                new VpnDialog(this);
                break;
            default:
                openMainActivity();
                break;
        }
    }

    private void openMainActivity() {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.getLoginType().equals(Callback.TAG_LOGIN_SINGLE_STREAM)){
            new Handler().postDelayed(this::openSingleStream, 2000);
        } else if (sharedPref.getLoginType().equals(Callback.TAG_LOGIN_ONE_UI)){
            if (Boolean.TRUE.equals(sharedPref.getIsFirst())) {
                new Handler().postDelayed(this::openSelectPlayer, 2000);
            } else {
                if (Boolean.FALSE.equals(sharedPref.getIsAutoLogin())) {
                    new Handler().postDelayed(this::openSelectPlayer, 2000);
                } else {
                    ApplicationUtil.openThemeActivity(DialogActivity.this);
                }
            }
        } else {
            new Handler().postDelayed(this::openSelectPlayer, 2000);
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void openSelectPlayer() {
        Intent intent = new Intent(DialogActivity.this, SelectPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "");
        startActivity(intent);
        finish();
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void openSingleStream() {
        Intent intent = new Intent(DialogActivity.this, SingleStreamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_splash;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}