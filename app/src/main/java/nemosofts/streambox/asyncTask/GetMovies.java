package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;

import nemosofts.streambox.Util.helper.DBHelper;
import nemosofts.streambox.Util.helper.JSHelper;
import nemosofts.streambox.interfaces.GetMovieListener;
import nemosofts.streambox.item.movie.ItemMovies;

public class GetMovies extends AsyncTask<String, String, String> {

    private final DBHelper dbHelper;
    private final JSHelper jsHelper;
    private final GetMovieListener listener;
    private final ArrayList<ItemMovies> itemMovies = new ArrayList<>();
    private final Boolean is_fav;
    private final String cat_id;
    private int page = 0;
    int itemsPerPage = 10;

    public GetMovies(Context ctx, int page, String cat_id, Boolean is_fav, GetMovieListener listener) {
        this.listener = listener;
        this.is_fav = is_fav;
        this.cat_id = cat_id;
        this.page = page;
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
            if (Boolean.TRUE.equals(is_fav)){
                itemMovies.addAll(dbHelper.getFavMovies());
            } else {
                final ArrayList<ItemMovies> arrayList = new ArrayList<>(jsHelper.getMovies(cat_id));
                if (Boolean.TRUE.equals(jsHelper.getIsMovieOrder())){
                    Collections.reverse(arrayList);
                }
                if (!arrayList.isEmpty()){
                    int startIndex = (page - 1) * itemsPerPage;
                    int endIndex = Math.min(startIndex + itemsPerPage, arrayList.size());
                    for (int i = startIndex; i < endIndex; i++) {
                        itemMovies.add(arrayList.get(i));
                    }
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
        listener.onEnd(s,itemMovies);
        super.onPostExecute(s);
    }
}