package nemosofts.streambox.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.NetworkUtils;
import nemosofts.streambox.Util.SpeedTest;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;
import nemosofts.streambox.view.NSoftsProgressDialog;

public class NetworkSpeedActivity extends AppCompatActivity {

    private TextView tv_time, tv_speed;
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

        findViewById(R.id.iv_back_page).setOnClickListener(view -> onBackPressed());

        progressDialog = new NSoftsProgressDialog(NetworkSpeedActivity.this);
        progressDialog.setCancelable(false);

        tv_time = findViewById(R.id.tv_time);
        tv_speed = findViewById(R.id.tv_speed);

        tv_speed.setOnClickListener(v -> runSpeedTest());
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_network_speed;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    private void runSpeedTest() {
        if (NetworkUtils.isConnected(this)){
            SpeedTest speedTest = new SpeedTest(new SpeedTest.OnSpeedCheckListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                    tv_time.setText("");
                    tv_time.setVisibility(View.GONE);
                    tv_speed.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String speedMbps) {
                    progressDialog.dismiss();
                    if (success.equals("1")){
                        tv_time.setText(speedMbps);
                        tv_time.setVisibility(View.VISIBLE);
                        tv_speed.setVisibility(View.GONE);
                    } else {
                        tv_time.setVisibility(View.GONE);
                        tv_speed.setVisibility(View.VISIBLE);
                        Toast.makeText(NetworkSpeedActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            speedTest.execute();
        } else {
            Toast.makeText(NetworkSpeedActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
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
}