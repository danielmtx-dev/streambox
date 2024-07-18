package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import androidx.nemosofts.Envato;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.interfaces.AboutListener;
import nemosofts.streambox.item.ItemDns;

public class LoadAbout extends AsyncTask<String, String, String> {

    private final DBHelper dbHelper;
    private final Envato envato;
    private final Helper helper;
    private final SharedPref sharedPref;
    private final AboutListener aboutListener;
    private String verifyStatus = "0", message = "";

    public LoadAbout(Context context, AboutListener aboutListener) {
        this.aboutListener = aboutListener;
        helper = new Helper(context);
        sharedPref = new SharedPref(context);
        envato = new Envato(context);
        dbHelper = new DBHelper(context);
    }

    @Override
    protected void onPreExecute() {
        aboutListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, helper.getAPIRequestNSofts(Callback.METHOD_APP_DETAILS));
            JSONObject mainJson = new JSONObject(json);

            try {
                JSONObject jsonObject = mainJson.getJSONObject(Callback.TAG_ROOT);

                if (jsonObject.has("details")) {
                    JSONArray jsonArrayDetails = jsonObject.getJSONArray("details");

                    for (int i = 0; i < jsonArrayDetails.length(); i++) {
                        JSONObject c = jsonArrayDetails.getJSONObject(i);

                        // App Details
                        String email = c.getString("app_email");
                        String author = c.getString("app_author");
                        String contact = c.getString("app_contact");
                        String website = c.getString("app_website");
                        String description = c.getString("app_description");
                        String developed_by = c.getString("app_developed_by");
                        sharedPref.setAboutDetails(email, author, contact, website, description, developed_by);

                        // Envato
                        String apikey = c.getString("envato_api_key");
                        if (!apikey.isEmpty()){
                            envato.setEnvatoKEY(apikey);
                        } else {
                            sharedPref.setAboutDetails(false);
                        }

                        // isSupported
                        Boolean is_rtl = Boolean.parseBoolean(c.getString("is_rtl"));
                        Boolean is_maintenance = Boolean.parseBoolean(c.getString("is_maintenance"));
                        Boolean is_screenshot = Boolean.parseBoolean(c.getString("is_screenshot"));
                        Boolean is_apk = Boolean.parseBoolean(c.getString("is_apk"));
                        Boolean is_vpn = Boolean.parseBoolean(c.getString("is_vpn"));
                        Boolean is_xui_dns = Boolean.parseBoolean(c.getString("is_xui_dns"));
                        sharedPref.setIsSupported(is_rtl, is_maintenance, is_screenshot, is_apk, is_vpn, is_xui_dns);

                        // AppUpdate
                        Callback.isAppUpdate = Boolean.parseBoolean(c.getString("app_update_status"));
                        if(!c.getString("app_new_version").equals("")) {
                            Callback.app_new_version = Integer.parseInt(c.getString("app_new_version"));
                        }
                        Callback.app_update_desc = c.getString("app_update_desc");
                        Callback.app_redirect_url = c.getString("app_redirect_url");

                        // Custom Ads
                        Callback.isCustomAds = Boolean.parseBoolean(c.getString("custom_ads"));
                        if(!c.getString("custom_ads_clicks").equals("")) {
                            Callback.customAdShow = Integer.parseInt(c.getString("custom_ads_clicks"));
                        }

                        if (c.has("is_theme") && (!c.getString("is_theme").isEmpty())) {
                            int theme = Integer.parseInt(c.getString("is_theme"));
                            sharedPref.setIsTheme(theme);
                        }
                    }
                }

                if (jsonObject.has("xui_dns")) {
                    dbHelper.removeAllDNS();
                    JSONArray jsonArrayXui = jsonObject.getJSONArray("xui_dns");
                    if (jsonArrayXui.length() > 0) {
                        for (int i = 0; i < jsonArrayXui.length(); i++) {
                            JSONObject jsonobject = jsonArrayXui.getJSONObject(i);

                            String dns_title = jsonobject.getString("dns_title");
                            String dns_base = jsonobject.getString("dns_base");

                            ItemDns objItem = new ItemDns(dns_title, dns_base);
                            dbHelper.addToDNS(objItem);
                        }
                    }
                }

                if (jsonObject.has("popup_ads")) {
                    JSONArray jsonArrayDetails = jsonObject.getJSONArray("popup_ads");
                    for (int i = 0; i < jsonArrayDetails.length(); i++) {
                        JSONObject c = jsonArrayDetails.getJSONObject(i);

                        Callback.ads_title = c.getString("ads_title");
                        Callback.ads_image = c.getString("ads_image");
                        Callback.ads_redirect_type = c.getString("ads_redirect_type");
                        Callback.ads_redirect_url = c.getString("ads_redirect_url");
                    }
                }
                return "1";
            } catch (Exception e) {
                JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                verifyStatus = jsonObject.getString(Callback.TAG_SUCCESS);
                message = jsonObject.getString(Callback.TAG_MSG);
                e.printStackTrace();
                return "0";
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        aboutListener.onEnd(s, verifyStatus, message);
        super.onPostExecute(s);
    }
}