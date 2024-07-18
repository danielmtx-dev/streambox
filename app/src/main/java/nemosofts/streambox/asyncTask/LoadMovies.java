package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;

import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.Util.helper.JSHelper;
import nemosofts.streambox.interfaces.SuccessListener;

public class LoadMovies extends AsyncTask<String, String, String> {

    private final Helper helper;
    private final JSHelper jsHelper;
    private final SharedPref sharedPref;
    private final SuccessListener listener;

    public LoadMovies(Context ctx, SuccessListener listener) {
        this.listener = listener;
        sharedPref = new SharedPref(ctx);
        helper = new Helper(ctx);
        jsHelper = new JSHelper(ctx);
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            String json_category = ApplicationUtil.responsePost(sharedPref.getAPI(), helper.getAPIRequest("get_vod_categories", sharedPref.getUserName(), sharedPref.getPassword()));
            JSONArray arrayCategory = new JSONArray(json_category);
            if (arrayCategory.length() != 0){
                jsHelper.addToMovieCatData(json_category);
            }

            String json = ApplicationUtil.responsePost(sharedPref.getAPI(), helper.getAPIRequest("get_vod_streams",sharedPref.getUserName(), sharedPref.getPassword()));
            JSONArray jsonarray = new JSONArray(json);
            if (jsonarray.length() != 0){
                jsHelper.addToMovieData(json);
            }

            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s);
        super.onPostExecute(s);
    }
}