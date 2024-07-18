package nemosofts.streambox.Util.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import nemosofts.streambox.Util.ApplicationUtil;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Helper {

    private final Context ctx;
    public Helper(Context ctx) {
        this.ctx = ctx;
    }

    @SuppressLint("MissingPermission")
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public RequestBody getAPIRequestLogin(String username, String password) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .build();
    }

    public RequestBody getAPIRequest(String action, String username, String password) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .addFormDataPart("action", action)
                .build();
    }

    public RequestBody getAPIRequestID(String action,String type,  String series_id, String username, String password) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .addFormDataPart("action", action)
                .addFormDataPart(type, series_id)
                .build();
    }

    public RequestBody getAPIRequestNSofts(String helper_name) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(gson);
        jsObj.addProperty("helper_name", helper_name);
        jsObj.addProperty("application_id", ctx.getPackageName());
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString()))
                .build();
    }
}
