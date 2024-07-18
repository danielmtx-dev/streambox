package nemosofts.streambox.ifSupported;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import nemosofts.streambox.Util.SharedPref;


public class IsScreenshot {

    public static void ifSupported(Activity mContext) {
        if (Boolean.TRUE.equals(new SharedPref(mContext).getIsScreenshot())) {
            try {
                Window window = mContext.getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
