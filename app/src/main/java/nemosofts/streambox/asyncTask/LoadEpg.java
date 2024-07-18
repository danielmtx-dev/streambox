package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.interfaces.EpgListener;
import nemosofts.streambox.item.live.ItemEpg;
import okhttp3.RequestBody;

public class LoadEpg extends AsyncTask<String, String, String> {

    private final SharedPref sharedPref;
    private final RequestBody requestBody;
    private final EpgListener listener;
    private final ArrayList<ItemEpg> arrayList = new ArrayList<>();

    public LoadEpg(Context ctx, EpgListener listener, RequestBody requestBody) {
        this.listener = listener;
        this.requestBody = requestBody;
        sharedPref = new SharedPref(ctx);
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            String json = ApplicationUtil.responsePost(sharedPref.getAPI(), requestBody);
            JSONArray jsonarray = new JSONArray(json);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String start= jsonobject.getString("start");
                String end= jsonobject.getString("end");
                String title= jsonobject.getString("title");
                String start_timestamp= jsonobject.getString("start_timestamp");
                String stop_timestamp= jsonobject.getString("stop_timestamp");
                String now_playing= jsonobject.getString("now_playing");
                String has_archive= jsonobject.getString("has_archive");

                ItemEpg objItem = new ItemEpg(start, end, title, start_timestamp, stop_timestamp, now_playing, has_archive);
                arrayList.add(objItem);
            }

            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, arrayList);
        super.onPostExecute(s);
    }

}