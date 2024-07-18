package nemosofts.streambox.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import nemosofts.streambox.BuildConfig;
import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.dialog.ExitDialog;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;

@UnstableApi
public class SelectPlayerActivity extends AppCompatActivity {

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

        findViewById(R.id.rl_login_xtream).setOnClickListener(view -> {
            Intent intent = new Intent(SelectPlayerActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
            startActivity(intent);
            finish();
        });
        findViewById(R.id.rl_single_stream).setOnClickListener(view -> {
            Intent intent = new Intent(SelectPlayerActivity.this, AddSingleURLActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
            startActivity(intent);
            finish();

        });
        findViewById(R.id.rl_list_users).setOnClickListener(view -> {
            Intent intent = new Intent(SelectPlayerActivity.this, UsersListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
            startActivity(intent);
            finish();
        });
        findViewById(R.id.tv_terms).setOnClickListener(view -> {
            Intent intent = new Intent(SelectPlayerActivity.this, WebActivity.class);
            intent.putExtra("web_url", BuildConfig.BASE_URL+"terms.php");
            intent.putExtra("page_title", getResources().getString(R.string.terms_and_conditions));
            ActivityCompat.startActivity(SelectPlayerActivity.this, intent, null);
        });



    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_select_player;
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
        new ExitDialog(this);
    }
}