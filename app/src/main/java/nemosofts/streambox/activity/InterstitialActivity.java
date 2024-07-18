package nemosofts.streambox.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import com.squareup.picasso.Picasso;

import nemosofts.streambox.R;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.ifSupported.IsRTL;
import nemosofts.streambox.ifSupported.IsScreenshot;
import nemosofts.streambox.ifSupported.IsStatusBar;

public class InterstitialActivity extends AppCompatActivity {

    private ProgressBar pb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Boolean.TRUE.equals(Callback.isLandscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        IsStatusBar.ifSupported(this);
        IsScreenshot.ifSupported(this);
        IsRTL.ifSupported(this);

        pb = findViewById(R.id.pb_ads);
        ImageView iv_ads = findViewById(R.id.iv_ads);

        Picasso.get()
                .load(Callback.interstitial_ads_image)
                .into(iv_ads, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        pb.setVisibility(View.GONE);
                    }
                });

        iv_ads.setOnClickListener(view -> {
            String url = Callback.interstitial_ds_redirect_url;
            if (!url.startsWith("http://") && !url.startsWith("https://")){
                url = "http://" + url;
            }
            if (Callback.interstitial_ads_redirect_type.equals("external")){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            } else {
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("web_url", url);
                intent.putExtra("page_title", "Ads");
                ActivityCompat.startActivity(this, intent, null);
            }
        });

        findViewById(R.id.iv_ads_close).setOnClickListener(view -> {
            Callback.interstitial_ads_image = "";
            Callback.interstitial_ads_redirect_type = "external";
            Callback.interstitial_ds_redirect_url = "";
            finish();
        });
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_interstitial;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}