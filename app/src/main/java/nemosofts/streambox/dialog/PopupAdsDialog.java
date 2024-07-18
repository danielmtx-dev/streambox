package nemosofts.streambox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import nemosofts.streambox.R;
import nemosofts.streambox.activity.WebActivity;
import nemosofts.streambox.callback.Callback;

public class PopupAdsDialog {

    private Dialog dialog;

    public PopupAdsDialog(Context context) {
        if (!Callback.ads_image.isEmpty()){
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_popup_ads);
            dialog.findViewById(R.id.iv_close_ads).setOnClickListener(view -> dismissDialog());
            if (Callback.ads_redirect_url.isEmpty()){
                dialog.findViewById(R.id.tv_btn_ads).setVisibility(View.GONE);
            }
            dialog.findViewById(R.id.tv_btn_ads).setOnClickListener(view -> {
                dismissDialog();
                if (Callback.ads_redirect_type.equals("external")){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Callback.ads_redirect_url));
                    context.startActivity(browserIntent);
                } else {
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra("web_url", Callback.ads_redirect_url);
                    intent.putExtra("page_title", Callback.ads_title);
                    ActivityCompat.startActivity(context, intent, null);
                }
            });
            TextView tv_ads_title = dialog.findViewById(R.id.tv_ads_title);
            ImageView ads = dialog.findViewById(R.id.iv_ads);
            ProgressBar pb_ads  = dialog.findViewById(R.id.pb_ads);
            pb_ads.setVisibility(View.VISIBLE);
            try {
                tv_ads_title.setText(Callback.ads_title);
                Picasso.get()
                        .load(Callback.ads_image)
                        .placeholder(R.color.black)
                        .into(ads, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                pb_ads.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                pb_ads.setVisibility(View.GONE);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
