package nemosofts.streambox.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Objects;

import nemosofts.streambox.R;
import nemosofts.streambox.callback.Callback;


public class UpgradeDialog {

    private final Dialog dialog;
    private final UpgradeListener listener;

    public UpgradeDialog(Activity activity , UpgradeListener listener) {
        this.listener = listener;

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_upgrade);
        dialog.setCancelable(false);
        TextView tv_upgrade_desc = dialog.findViewById(R.id.tv_upgrade_desc);
        tv_upgrade_desc.setText(Callback.app_update_desc);
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dismissDialog(false));
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dismissDialog(false));
        dialog.findViewById(R.id.tv_do).setOnClickListener(view -> {
            dismissDialog(true);
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Callback.app_redirect_url)));
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void dismissDialog(Boolean isDo) {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        if (Boolean.TRUE.equals(isDo)){
            listener.onDo();
        } else {
            listener.onCancel();
        }
    }

    public interface UpgradeListener {
        void onCancel();
        void onDo();
    }
}
