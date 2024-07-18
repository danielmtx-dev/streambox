package nemosofts.streambox.Util;

import static android.content.Context.UI_MODE_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nemosofts.streambox.BuildConfig;
import nemosofts.streambox.R;
import nemosofts.streambox.Util.player.CustomPlayerView;
import nemosofts.streambox.activity.UI.BlackPantherActivity;
import nemosofts.streambox.activity.UI.GlossyActivity;
import nemosofts.streambox.activity.UI.OneUIActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApplicationUtil {

    public static final String FEATURE_FIRE_TV = "amazon.hardware.fire_tv";

    @NonNull
    public static String responsePost(String url, RequestBody requestBody) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(25000, TimeUnit.MILLISECONDS)
                .writeTimeout(25000, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @NonNull
    public static String toBase64(@NonNull String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }

    @NonNull
    public static String decodeBase64(String encoded) {
        byte[] decodedBytes = Base64.decode(encoded, Base64.DEFAULT);
        return new String(decodedBytes);
    }

    @NonNull
    public static String encodeBase64(@NonNull String encoded) {
        byte[] decodedBytes = Base64.encode(encoded.getBytes(), Base64.DEFAULT);
        return new String(decodedBytes);
    }

    public static int getColumnWidth(@NonNull Context ctx, int column, int grid_padding) {
        Resources r = ctx.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, grid_padding, r.getDisplayMetrics());
        return (int) ((getScreenWidth(ctx) - ((column + 1) * padding)) / column);
    }

    private static int getScreenWidth(@NonNull Context ctx) {
        int columnWidth;
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    @NonNull
    public static String convertIntToDate(@NonNull String convert_date, String pattern) {
        if (!convert_date.isEmpty()){
            if (convert_date.equals("0")){
                return " Unlimited";
            } else {
                long timestamp = Long.parseLong(convert_date);
                Date date = new Date(timestamp * 1000);
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
                return " "+dateFormat.format(date);
            }
        } else {
            return " none";
        }
    }

    @NonNull
    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @NonNull
    public static String TimeFormat(String time) {
        try {
            if (!time.isEmpty()){
                int totalMinutes = Integer.parseInt(time);
                int hours = totalMinutes / 60;
                int minutes = totalMinutes % 60;
                return formatTime(hours, minutes);
            } else {
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @NonNull
    private static String formatTime(int hours, int minutes) {
        if (hours != 0){
            return hours + "h " + minutes + "m";
        } else {
            if (minutes != 0){
                return minutes + "m";
            } else {
                return "0";
            }
        }
    }

    @NonNull
    public static String formatTimeToTime(@NonNull String timeString) {
        String[] timeParts = timeString.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        if (hours != 0){
            return hours + "h " + minutes + "m " + seconds + "s";
        } else {
            if (minutes != 0){
                return minutes + "m "  + seconds + "s";
            } else {
                return "0";
            }
        }
    }

    public static Boolean calculateUpdateHours(@NonNull String inputDateStr, int updateHours){
        boolean is_update = false;
        if (!inputDateStr.isEmpty()){
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date inputDate = dateFormat.parse(inputDateStr);
                Date currentDate = new Date();
                assert inputDate != null;
                long timeDifferenceInMillis = currentDate.getTime() - inputDate.getTime();
                long seconds = timeDifferenceInMillis / 1000;
                int hours = (int) (seconds / 3600);
                is_update = hours > updateHours;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return is_update;
    }


    public static String calculateTimeSpan(@NonNull String inputDateStr){
        String time = "not available";
        if (!inputDateStr.isEmpty()){
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date inputDate = dateFormat.parse(inputDateStr);
                Date currentDate = new Date();
                assert inputDate != null;
                long timeDifferenceInMillis = currentDate.getTime() - inputDate.getTime();
                long seconds = timeDifferenceInMillis / 1000;

                int year = (int) (seconds / 31556926);
                int months = (int) (seconds / 2629743);
                int week = (int) (seconds / 604800);
                int day = (int) (seconds / 86400);
                int hours = (int) (seconds / 3600);
                int min = (int) ((seconds - (hours * 3600)) / 60);
                int secs = (int) (seconds % 60);

                if (seconds < 60){
                    time = secs + " sec ago";
                } else if (seconds < 3600){
                    time = (min == 1) ? min+" min ago" : min + " mins ago";
                } else if (seconds < 86400){
                    time = (hours == 1) ? hours+" hour ago" : hours + " hours ago";
                } else if (seconds < 604800){
                    time = (day == 1) ? day+" day ago" : day + " days ago";
                } else if (seconds < 2629743){
                    time = (week == 1) ? week+" week ago" : week + " weeks ago";
                } else if (seconds < 31556926){
                    time = (months == 1) ? months+" month ago" : months + " months ago";
                }  else {
                    time = (year == 1) ? year+" year ago" : year + " years ago";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            time = "";
        }
        return time;
    }

    @NonNull
    @Contract(pure = true)
    public static String averageRating(String rating) {
        if (rating != null){
            switch (rating) {
                case "1":
                case "1.1":
                case "1.2":
                case "1.3":
                case "1.4":
                case "1.5":
                case "1.6":
                case "1.7":
                case "1.8":
                case "1.9":
                    return "1";
                case "2":
                case "2.1":
                case "2.2":
                case "2.3":
                case "2.4":
                case "2.5":
                case "2.6":
                case "2.7":
                case "2.8":
                case "2.9":
                    return "2";
                case "3":
                case "3.1":
                case "3.2":
                case "3.3":
                case "3.4":
                case "3.5":
                case "3.6":
                case "3.7":
                case "3.8":
                case "3.9":
                    return "3";
                case "4":
                case "4.1":
                case "4.2":
                case "4.3":
                case "4.4":
                case "4.5":
                case "4.6":
                case "4.7":
                case "4.8":
                case "4.9":
                    return "4";
                case "5":
                case "5.1":
                case "5.2":
                case "5.3":
                case "5.4":
                case "5.5":
                case "5.6":
                case "5.7":
                case "5.8":
                case "5.9":
                    return "5";
                default:
                    return "0";
            }
        } else {
            return "0";
        }
    }

    // Tablet
    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 720;
    }

    // TvBox
    public static boolean isTvBox(Context context) {
        final PackageManager pm = context.getPackageManager();

        // TV for sure
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true;
        }

        if (pm.hasSystemFeature(FEATURE_FIRE_TV)) {
            return true;
        }

        // Missing Files app (DocumentsUI) means box (some boxes still have non functional app or stub)
        if (!hasSAFChooser(pm)) {
            return true;
        }

        // Legacy storage no longer works on Android 11 (level 30)
        if (Build.VERSION.SDK_INT < 30) {
            // (Some boxes still report touchscreen feature)
            if (!pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
                return true;
            }

            if (pm.hasSystemFeature("android.hardware.hdmi.cec")) {
                return true;
            }

            if (Build.MANUFACTURER.equalsIgnoreCase("zidoo")) {
                return true;
            }
        }
        // Default: No TV - use SAF
        return false;
    }
    public static boolean hasSAFChooser(final PackageManager pm) {
        final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        return intent.resolveActivity(pm) != null;
    }

    // Player
    @SuppressLint("UnsafeOptInUsageError")
    public static void showText(final CustomPlayerView playerView, final String text, final long timeout) {
        playerView.removeCallbacks(playerView.textClearRunnable);
        playerView.clearIcon();
        playerView.setCustomErrorMessage(text);
        playerView.postDelayed(playerView.textClearRunnable, timeout);
    }

    public static void showText(final CustomPlayerView playerView, final String text) {
        showText(playerView, text, 1200);
    }

    public static void log(final String text) {
        if (BuildConfig.DEBUG) {
            Log.d("StreamBox", text);
        }
    }

    public static void openThemeActivity(Activity activity) {
        int theme = new SharedPref(activity).getIsTheme();
        Intent intent;
        if (theme == 2){
            intent = new Intent(activity, GlossyActivity.class);
        }  else  if (theme == 3){
            intent = new Intent(activity, BlackPantherActivity.class);
        } else {
            intent = new Intent(activity, OneUIActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static int openThemeBg(Activity activity) {
        int theme = new SharedPref(activity).getIsTheme();
        if (theme == 2){
            return R.drawable.bg_ui_glossy;
        } else if (theme == 3){
            return R.drawable.bg_dark_panther;
        } else {
            return R.drawable.bg_dark;
        }
    }
}
