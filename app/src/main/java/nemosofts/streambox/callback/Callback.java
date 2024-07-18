package nemosofts.streambox.callback;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nemosofts.streambox.BuildConfig;
import nemosofts.streambox.item.live.ItemLive;
import nemosofts.streambox.item.series.ItemEpisodes;

@SuppressLint("StaticFieldLeak")
public class Callback implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Boolean isLandscape = true;

    // API URL
    public static String API_URL = BuildConfig.BASE_URL+"api.php";

    // TAG API
    public static final String TAG_ROOT = BuildConfig.API_NAME;
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MSG = "MSG";

    // Method
    public static final String METHOD_APP_DETAILS = "app_details";
    public static final String METHOD_APP_INT = "get_interstitial";

    public static String TAG_TV = "date_tv";
    public static String TAG_MOVIE = "date_movies";
    public static String TAG_SERIES = "date_series";

    public static String TAG_LOGIN = "none";
    public static String TAG_LOGIN_ONE_UI = "one_ui";
    public static String TAG_LOGIN_SINGLE_STREAM = "single_stream";

    public static int playPosLive = 0;
    public static ArrayList<ItemLive> arrayListLive = new ArrayList<>();

    public static int playPosEpisodes = 0;
    public static ArrayList<ItemEpisodes> arrayListEpisodes = new ArrayList<>();

    public static Boolean isPlayed = false;
    public static int playPos = 0;
    public static List<ItemLive> arrayList_play = new ArrayList<>();

    public static String successLive = "0";
    public static String successSeries = "0";
    public static String successMovies = "0";

    public static Boolean isAppUpdate = false;
    public static int app_new_version = 1;
    public static String app_update_desc = "", app_redirect_url = "";

    public static final String DIALOG_TYPE_UPDATE = "upgrade",
            DIALOG_TYPE_MAINTENANCE = "maintenance",
            DIALOG_TYPE_DEVELOPER = "developer",
            DIALOG_TYPE_VPN = "vpn";

    public static String ads_title = "";
    public static String ads_image = "";
    public static String ads_redirect_type = "";
    public static String ads_redirect_url = "";

    public static String interstitial_ads_image = "";
    public static String interstitial_ads_redirect_type = "external";
    public static String interstitial_ds_redirect_url = "";

    public static Boolean isCustomAds = false;
    public static int customAdCount = 0, customAdShow = 15;
    public static Boolean is_load_ads = true;

    public static Boolean isAppOpen = false;
}
