package nemosofts.streambox.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nemosofts.streambox.callback.Callback;


public class SharedPref {

    private final EncryptData encryptData;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static final String TAG_FIRST_OPEN = "first_open", SHARED_PREF_AUTOLOGIN = "autologin", TAG_IS_LOGGED = "islogged";

    public SharedPref(@NonNull Context ctx) {
        encryptData = new EncryptData(ctx);
        sharedPreferences = ctx.getSharedPreferences("setting_apps", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean(TAG_FIRST_OPEN, true);
    }
    public void setIsFirst(Boolean flag) {
        editor.putBoolean(TAG_FIRST_OPEN, flag);
        editor.apply();
    }

    public boolean isLogged() {
        return sharedPreferences.getBoolean(TAG_IS_LOGGED, false);
    }
    public void setIsLogged(Boolean isLogged) {
        editor.putBoolean(TAG_IS_LOGGED, isLogged);
        editor.apply();
    }

    public Boolean getIsAutoLogin() { return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false); }
    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }

    public void setLoginDetails(
            // user_info
            String username, String password, String message, int auth,
            String status, String exp_date, String is_trial, String active_cons, String created_at, String max_connections,
            // server_info
            boolean xui, String version, int revision, String url, String port, String https_port,
            String server_protocol, String rtmp_port, int timestamp_now, String time_now, String timezone) {

        // user_info
        editor.putString("username", encryptData.encrypt(username));
        editor.putString("password", encryptData.encrypt(password));
        editor.putString("message", message);
        editor.putInt("auth", auth);
        editor.putString("status",status);
        editor.putString("exp_date", exp_date);
        editor.putString("is_trial", is_trial);
        editor.putString("active_cons", active_cons);
        editor.putString("created_at", created_at);
        editor.putString("max_connections", max_connections);

        // server_info
        editor.putBoolean("is_xui", xui);
        editor.putString("version", version);
        editor.putInt("revision", revision);
        editor.putString("url_data", url);
        editor.putString("port", port);
        editor.putString("https_port", https_port);
        editor.putString("server_protocol", server_protocol);
        editor.putString("rtmp_port", rtmp_port);
        editor.putInt("timestamp_now", timestamp_now);
        editor.putString("time_now", time_now);
        editor.putString("timezone", timezone);

        editor.apply();
    }

    public Boolean getIsXuiUser() {
        return sharedPreferences.getBoolean("is_xui", true);
    }

    public String getIsStatus() {
        return sharedPreferences.getString("status", "");
    }

    public void setAnyName(String any_name){
        editor.putString("any_name", encryptData.encrypt(any_name));
        editor.apply();
    }
    public String getAnyName() {
        return encryptData.decrypt(sharedPreferences.getString("any_name", ""));
    }

    public String getActiveConnections() {
        return sharedPreferences.getString("active_cons", "");
    }

    public String getMaxConnections() {
        return sharedPreferences.getString("max_connections", "");
    }

    public String getUserName() {
        return encryptData.decrypt(sharedPreferences.getString("username", ""));
    }

    public String getPassword() {
        return encryptData.decrypt(sharedPreferences.getString("password",""));
    }

    public String getExpDate() {
        return sharedPreferences.getString("exp_date","0");
    }

    public String getServerURL() {
        String server_protocol =  sharedPreferences.getString("server_protocol","http");
        String url =  sharedPreferences.getString("url_data","");
        String http_port = sharedPreferences.getString("port","");
        String https_port = sharedPreferences.getString("https_port","");
        if (server_protocol.equals("http")){
            return server_protocol+"://"+url+ ":"+http_port+"/";
        } else {
            return server_protocol+"://"+url+ ":"+https_port+"/";
        }
    }

    public String getAPI() {
        String server_protocol =  sharedPreferences.getString("server_protocol","http");
        String url =  sharedPreferences.getString("url_data","");
        String http_port = sharedPreferences.getString("port","");
        String https_port = sharedPreferences.getString("https_port","");
        if (server_protocol.equals("http")){
            return server_protocol+"://"+url+ ":"+http_port+"/player_api.php";
        } else {
            return server_protocol+"://"+url+ ":"+https_port+"/player_api.php";
        }
    }

    public void setCurrentDate(String type){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateTime = simpleDateFormat.format(calendar.getTime());
        editor.putString(type, currentDateTime);
        editor.apply();
    }

    public void setCurrentDateEmpty(String type){
        editor.putString(type, "");
        editor.apply();
    }

    @NonNull
    public String getCurrent(String type) {
        return sharedPreferences.getString(type, "");
    }


    public String getLoginType() {
        return sharedPreferences.getString("login_type", Callback.TAG_LOGIN);
    }
    public void setLoginType(String type){
        editor.putString("login_type", type);
        editor.apply();
    }

    // AboutDetails --------------------------------------------------------------------------------
    public void setAboutDetails(String email, String author, String contact, String website, String description, String developed) {
        editor.putString("app_email", email);
        editor.putString("app_author", author);
        editor.putString("app_contact", contact);
        editor.putString("app_website", website);
        editor.putString("app_description", description);
        editor.putString("app_developedBy", developed);
        editor.apply();
    }

    public String getAppEmail() {
        return sharedPreferences.getString("app_email", "");
    }

    public String getAppAuthor() {
        return sharedPreferences.getString("app_author", "");
    }

    public String getAppContact() {
        return sharedPreferences.getString("app_contact", "");
    }

    public String getAppWebsite() {
        return sharedPreferences.getString("app_website", "");
    }

    public String getAppDescription() {
        return sharedPreferences.getString("app_description", "");
    }

    public String getAppDevelopedBy() {
        return sharedPreferences.getString("app_developedBy", "");
    }

    public Boolean getIsAboutDetails() {
        return sharedPreferences.getBoolean("is_about", false);
    }
    public void setAboutDetails(Boolean flag){
        editor.putBoolean("is_about", flag);
        editor.apply();
    }

    // isSupported ---------------------------------------------------------------------------------
    public void setIsSupported(Boolean isRtl, Boolean isMaintenance, Boolean isScreenshot, Boolean isApk, Boolean isVpn, Boolean isXuiDns) {
        editor.putBoolean("is_rtl", isRtl);
        editor.putBoolean("is_maintenance", isMaintenance);
        editor.putBoolean("is_screenshot", isScreenshot);
        editor.putBoolean("is_apk", isApk);
        editor.putBoolean("is_vpn", isVpn);
        editor.putBoolean("is_xui_dns", isXuiDns);
        editor.apply();
    }

    public Boolean getIsRTL() {
        return sharedPreferences.getBoolean("is_rtl", false);
    }

    public Boolean getIsMaintenance() {
        return sharedPreferences.getBoolean("is_maintenance", false);
    }

    public Boolean getIsScreenshot() {
        return sharedPreferences.getBoolean("is_screenshot", false);
    }

    public Boolean getIsAPK() {
        return sharedPreferences.getBoolean("is_apk", false);
    }

    public Boolean getIsVPN() {
        return sharedPreferences.getBoolean("is_vpn", false);
    }

    public Boolean getIsXUI_DNS(){
        return sharedPreferences.getBoolean("is_xui_dns", false);
    }


    public int getIsTheme() {
        return sharedPreferences.getInt("is_theme", 0);
    }
    public void setIsTheme(int flag){
        editor.putInt("is_theme", flag);
        editor.apply();
    }

}