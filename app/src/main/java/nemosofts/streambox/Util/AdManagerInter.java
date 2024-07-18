package nemosofts.streambox.Util;

import android.content.Context;
import android.content.Intent;

import nemosofts.streambox.activity.InterstitialActivity;
import nemosofts.streambox.asyncTask.LoadInterstitial;
import nemosofts.streambox.callback.Callback;

public class AdManagerInter {

    public AdManagerInter(Context ctx) {
        if (!Callback.interstitial_ads_image.isEmpty() && !Callback.interstitial_ds_redirect_url.isEmpty()){
            Callback.customAdCount = Callback.customAdCount + 1;
            if (Boolean.TRUE.equals(Callback.isCustomAds) && Callback.customAdCount % Callback.customAdShow == 0){
                ctx.startActivity(new Intent(ctx, InterstitialActivity.class));
            }
        } else {
            if (Boolean.TRUE.equals(Callback.is_load_ads)){
                new LoadInterstitial(ctx).execute();
            }
        }
    }
}