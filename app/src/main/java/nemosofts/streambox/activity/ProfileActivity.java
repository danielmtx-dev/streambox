package nemosofts.streambox.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;

public class ProfileActivity extends AppCompatActivity {

    TextView tv_active, tv_active_none;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Boolean.TRUE.equals(Callback.isLandscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        IsStatusBar.ifSupported(this);
        IsScreenshot.ifSupported(this);
        IsRTL.ifSupported(this);

        SharedPref sharedPref = new SharedPref(this);

        findViewById(R.id.theme_bg).setBackgroundResource(ApplicationUtil.openThemeBg(this));

        findViewById(R.id.iv_back_page).setOnClickListener(v -> onBackPressed());

        tv_active =  findViewById(R.id.tv_active);
        tv_active_none =  findViewById(R.id.tv_active_none);

        TextView profile_name =  findViewById(R.id.tv_profile_name);
        profile_name.setText(sharedPref.getUserName());

        TextView active_connections =  findViewById(R.id.tv_active_connections);
        active_connections.setText(sharedPref.getActiveConnections() +" / "+ sharedPref.getMaxConnections());

        TextView card_expiry =  findViewById(R.id.tv_card_expiry);
        card_expiry.setText(ApplicationUtil.convertIntToDate(sharedPref.getExpDate(), "MMMM dd, yyyy"));

        TextView card_any_name =  findViewById(R.id.card_any_name);
        card_any_name.setText(sharedPref.getAnyName());

        if (sharedPref.getIsStatus().equals("Active")){
            tv_active.setVisibility(View.VISIBLE);
            tv_active_none.setVisibility(View.GONE);
        } else {
            tv_active.setVisibility(View.GONE);
            tv_active_none.setVisibility(View.VISIBLE);
            tv_active_none.setText(sharedPref.getIsStatus());
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_profile;
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
        super.onBackPressed();
    }
}