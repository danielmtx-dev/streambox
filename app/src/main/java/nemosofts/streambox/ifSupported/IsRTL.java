package nemosofts.streambox.ifSupported;

import android.app.Activity;
import android.view.View;
import android.view.Window;

import nemosofts.streambox.Util.SharedPref;

public class IsRTL {

    public static void ifSupported(Activity mContext) {
        if (Boolean.TRUE.equals(new SharedPref(mContext).getIsRTL())) {
            try {
                Window window = mContext.getWindow();
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
