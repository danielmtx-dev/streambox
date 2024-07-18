package nemosofts.streambox.asyncTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.callback.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class LoadInterstitial extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    public LoadInterstitial(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        Callback.is_load_ads = false;
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, getAPIRequestNSofts());
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Callback.interstitial_ads_image = obj.getString("ads_image");
                Callback.interstitial_ads_redirect_type = obj.getString("ads_redirect_type");
                Callback.interstitial_ds_redirect_url = obj.getString("ads_redirect_url");
                Callback.is_load_ads = true;

            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @NonNull
    private RequestBody getAPIRequestNSofts() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(gson);
        jsObj.addProperty("helper_name", Callback.METHOD_APP_INT);
        jsObj.addProperty("application_id", ctx.getPackageName());
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString()))
                .build();
    }
}