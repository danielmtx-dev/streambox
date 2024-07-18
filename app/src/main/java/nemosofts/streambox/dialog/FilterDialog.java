package nemosofts.streambox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.Objects;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.helper.JSHelper;

public class FilterDialog {

    private final Dialog dialog;
    private final JSHelper jsHelper;
    private Boolean flag = false;
    private final FilterDialogListener listener;

    public FilterDialog(Context context, int pageType, FilterDialogListener filterListener) {
        this.listener = filterListener;
        jsHelper = new JSHelper(context);
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter);
        dialog.findViewById(R.id.iv_close_btn).setOnClickListener(view -> {
            dismissDialog();
        });

        TextView oldest = dialog.findViewById(R.id.tv_oldest_to_first);
        TextView newest = dialog.findViewById(R.id.tv_newest_to_first);

        if (Boolean.TRUE.equals(pageType == 1)){
            flag = jsHelper.getIsLiveOrder();
        } else if (Boolean.TRUE.equals(pageType == 2)){
            flag = jsHelper.getIsMovieOrder();
        }  else if (Boolean.TRUE.equals(pageType == 3)){
            flag = jsHelper.getIsSeriesOrder();
        }

        if (Boolean.TRUE.equals(flag)){
            oldest.setTextColor(ContextCompat.getColor(context, R.color.color_select));
            newest.setTextColor(ContextCompat.getColor(context, R.color.md_classic_sub_title));
        } else {
            oldest.setTextColor(ContextCompat.getColor(context, R.color.md_classic_sub_title));
            newest.setTextColor(ContextCompat.getColor(context, R.color.color_select));
        }

        oldest.setOnClickListener(view -> {
            flag = true;
            oldest.setTextColor(ContextCompat.getColor(context, R.color.color_select));
            newest.setTextColor(ContextCompat.getColor(context, R.color.md_classic_sub_title));
        });
        newest.setOnClickListener(view -> {
            flag = false;
            oldest.setTextColor(ContextCompat.getColor(context, R.color.md_classic_sub_title));
            newest.setTextColor(ContextCompat.getColor(context, R.color.color_select));
        });

        dialog.findViewById(R.id.btn_cancel_filter).setOnClickListener(view -> {
            if (Boolean.TRUE.equals(pageType == 1)){
                jsHelper.setIsLiveOrder(false);
            } else if (Boolean.TRUE.equals(pageType == 2)){
                jsHelper.setIsMovieOrder(false);
            }  else if (Boolean.TRUE.equals(pageType == 3)){
                jsHelper.setIsSeriesOrder(false);
            }
            listener.onSubmit();
            dismissDialog();
        });
        dialog.findViewById(R.id.btn_submit_filter).setOnClickListener(view -> {
            if (Boolean.TRUE.equals(pageType == 1)){
                jsHelper.setIsLiveOrder(flag);
            } else if (Boolean.TRUE.equals(pageType == 2)){
                jsHelper.setIsMovieOrder(flag);
            }  else if (Boolean.TRUE.equals(pageType == 3)){
                jsHelper.setIsSeriesOrder(flag);
            }
            listener.onSubmit();
            dismissDialog();
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public interface FilterDialogListener {
        void onSubmit();
    }
}
