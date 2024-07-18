package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.Util.helper.JSHelper;
import nemosofts.streambox.interfaces.LiveListener;
import nemosofts.streambox.item.ItemCat;

public class LoadLive extends AsyncTask<String, String, String> {

    private final JSHelper jsHelper;
    private final Helper helper;
    private final DBHelper dbHelper;
    private final SharedPref sharedPref;
    private final LiveListener listener;

    public LoadLive(Context ctx, LiveListener listener) {
        this.listener = listener;
        sharedPref = new SharedPref(ctx);
        dbHelper = new DBHelper(ctx);
        helper = new Helper(ctx);
        jsHelper = new JSHelper(ctx);
    }

    @Override
    protected void onPreExecute() {
        jsHelper.removeAllLive();
        dbHelper.removeAllCatLive();
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            String json_category = ApplicationUtil.responsePost(sharedPref.getAPI(), helper.getAPIRequest("get_live_categories", sharedPref.getUserName(), sharedPref.getPassword()));
            JSONArray arrayCategory = new JSONArray(json_category);
            for (int i = 0; i < arrayCategory.length(); i++) {
                JSONObject objectCategory = arrayCategory.getJSONObject(i);

                String id = objectCategory.getString("category_id");
                String name = objectCategory.getString("category_name");

                ItemCat objItem = new ItemCat(id, name);
                dbHelper.addToCatLiveList(objItem);
            }

            String json = ApplicationUtil.responsePost(sharedPref.getAPI(), helper.getAPIRequest("get_live_streams",sharedPref.getUserName(), sharedPref.getPassword()));
            JSONArray jsonarray = new JSONArray(json);
            if (jsonarray.length() != 0){
                jsHelper.addToLiveData(json);
            }
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                if (!jsonobject.getString("stream_type").equals("live")){
                    String cat_id = jsonobject.getString("category_id");
                    dbHelper.removeCatLiveID(cat_id);
                }
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

    @Override
    protected void onCancelled(String s) {
        listener.onCancel(s);
        super.onCancelled(s);
    }
}