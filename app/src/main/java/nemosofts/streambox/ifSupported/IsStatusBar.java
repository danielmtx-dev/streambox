package nemosofts.streambox.ifSupported;

import android.app.Activity;
import android.view.View;

public class IsStatusBar {
    public static void ifSupported(Activity mContext) {
        try {
            View decorView = mContext.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
