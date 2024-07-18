package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.streambox.interfaces.SeriesIDListener;
import nemosofts.streambox.item.series.ItemEpisodes;
import nemosofts.streambox.item.series.ItemInfoSeasons;
import nemosofts.streambox.item.series.ItemSeasons;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import okhttp3.RequestBody;

public class LoadSeriesID extends AsyncTask<String, String, String> {

    private final SharedPref sharedPref;
    private final SeriesIDListener listener;
    private final RequestBody requestBody;
    private final ArrayList<ItemInfoSeasons> arrayListInfo = new ArrayList<>();
    private final ArrayList<ItemSeasons> arrayListSeries = new ArrayList<>();
    private final ArrayList<ItemEpisodes> arrayListEpisodes = new ArrayList<>();

    public LoadSeriesID(Context ctx, SeriesIDListener listener, RequestBody requestBody) {
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
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has("info")) {

                JSONObject c =  jsonObject.getJSONObject("info");

                String name = c.getString("name");
                String cover = c.getString("cover");
                String plot = c.getString("plot");
                String director = c.getString("director");
                String genre = c.getString("genre");
                String releaseDate = c.getString("releaseDate");
                String rating = c.getString("rating");
                String rating_5based = c.getString("rating_5based");
                String youtube_trailer = c.getString("youtube_trailer");

                ItemInfoSeasons objItem = new ItemInfoSeasons(name, cover, plot, director, genre, releaseDate, rating, rating_5based, youtube_trailer);
                arrayListInfo.add(objItem);
            }

            if (jsonObject.has("seasons")) {

                JSONArray c =  jsonObject.getJSONArray("seasons");
                for (int i = 0; i < c.length(); i++) {
                    JSONObject objectCategory = c.getJSONObject(i);

                    String id = objectCategory.getString("id");
                    String name = objectCategory.getString("name");
                    String season_number = objectCategory.getString("season_number");

                    ItemSeasons objItem = new ItemSeasons(id,name,season_number);
                    arrayListSeries.add(objItem);
                }
            }

            if (jsonObject.has("episodes")) {

                JSONObject c =  jsonObject.getJSONObject("episodes");

                for (int h = 0; h < 20; h++) {

                    if (c.has(String.valueOf(h))) {

                        JSONArray cm = c.getJSONArray(String.valueOf(h));
                        for (int i = 0; i < cm.length(); i++) {
                            JSONObject object = cm.getJSONObject(i);

                            String id = object.getString("id");
                            String title = object.getString("title");
                            String container_extension = object.getString("container_extension");
                            String season = object.getString("season");

                            // info
                            JSONObject object2 = object.getJSONObject("info");
                            String plot = object2.getString("plot");
                            String duration = object2.getString("duration");
                            String movie_image = object2.getString("movie_image");
                            String rating = object2.getString("rating");

                            ItemEpisodes episodes = new ItemEpisodes(id, title, container_extension, season, plot, duration, rating, movie_image);
                            arrayListEpisodes.add(episodes);
                        }
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
        listener.onEnd(s, arrayListInfo, arrayListSeries, arrayListEpisodes);
        super.onPostExecute(s);
    }
}