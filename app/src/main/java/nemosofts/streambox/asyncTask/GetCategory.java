package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.helper.JSHelper;
import nemosofts.streambox.interfaces.GetCategoryListener;
import nemosofts.streambox.item.ItemCat;

public class GetCategory extends AsyncTask<String, String, String> {

    private final DBHelper dbHelper;
    private final JSHelper jsHelper;
    private final GetCategoryListener listener;
    private final ArrayList<ItemCat> itemCat = new ArrayList<>();
    private final int pageType;

    public GetCategory(Context ctx, int pageType, GetCategoryListener listener) {
        this.listener = listener;
        this.pageType = pageType;
        jsHelper = new JSHelper(ctx);
        dbHelper = new DBHelper(ctx);
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            if (Boolean.TRUE.equals(pageType == 1)){
                itemCat.addAll(dbHelper.getCategoryLive(null));
            } else if (Boolean.TRUE.equals(pageType == 2)){
                itemCat.addAll(jsHelper.getCategoryMovie());
            }  else if (Boolean.TRUE.equals(pageType == 3)){
                itemCat.addAll(jsHelper.getCategorySeries());
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s,itemCat);
        super.onPostExecute(s);
    }
}