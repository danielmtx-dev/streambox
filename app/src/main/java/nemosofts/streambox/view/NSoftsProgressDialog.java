package nemosofts.streambox.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import java.util.Objects;

import nemosofts.streambox.R;

public class NSoftsProgressDialog extends Dialog {

    public NSoftsProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress_dialog);
        Objects.requireNonNull(this.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

}
