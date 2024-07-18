package nemosofts.streambox.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

import nemosofts.streambox.R;

public class ExitDialog {

    public ExitDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_exit);
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_do_exit).setOnClickListener(view -> {
            dialog.dismiss();
            activity.finish();
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

}
