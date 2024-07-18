package nemosofts.streambox.dialog;

import android.app.Activity;
import android.app.Dialog;

import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

import nemosofts.streambox.R;

public class ScreenDialog {

    private Dialog dialog;
    private final Activity activity;
    private final ScreenDialogListener listener;

    public ScreenDialog(Activity activity, ScreenDialogListener filterListener) {
        this.listener = filterListener;
        this.activity = activity;
    }

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_screen);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.iv_screen_one).setOnClickListener(v -> listener.onSubmit(1));
        dialog.findViewById(R.id.iv_screen_two).setOnClickListener(v -> listener.onSubmit(2));
        dialog.findViewById(R.id.iv_screen_three).setOnClickListener(v -> listener.onSubmit(3));
        dialog.findViewById(R.id.iv_screen_four).setOnClickListener(v -> listener.onSubmit(4));
        dialog.findViewById(R.id.iv_screen_five).setOnClickListener(v -> listener.onSubmit(5));
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public interface ScreenDialogListener {
        void onSubmit(int screen);
    }
}
