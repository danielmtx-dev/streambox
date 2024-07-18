package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.streambox.interfaces.MovieIDListener;
import nemosofts.streambox.item.movie.ItemInfoMovies;
import nemosofts.streambox.item.movie.ItemMoviesData;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import okhttp3.RequestBody;

public class LoadMovieID extends AsyncTask<String, String, String> {

    private final SharedPref sharedPref;
    private final MovieIDListener listener;
    private final RequestBody requestBody;
    private final ArrayList<ItemInfoMovies> arrayListInfo = new ArrayList<>();
    private final ArrayList<ItemMoviesData> arrayListData = new ArrayList<>();

    public LoadMovieID(Context ctx, MovieIDListener listener, RequestBody requestBody) {
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

                String name = "";
                if (c.has("name")){
                    name = c.getString("name");
                }

                String o_name = "";
                if (c.has("o_name")){
                    o_name = c.getString("o_name");
                }

                String movie_image = "";
                if (c.has("movie_image")){
                    movie_image = c.getString("movie_image");
                }

                String release_date = "";
                if (c.has("release_date")){
                    release_date = c.getString("release_date");
                }

                String episode_run_time = "";
                if (c.has("episode_run_time")){
                    episode_run_time = c.getString("episode_run_time");
                }

                String youtube_trailer = "";
                if (c.has("youtube_trailer")){
                    youtube_trailer = c.getString("youtube_trailer");
                }

                String director = "";
                if (c.has("director")){
                    director = c.getString("director");
                }

                String actors = "";
                if (c.has("actors")){
                    actors = c.getString("actors");
                }

                String cast = "";
                if (c.has("cast")){
                    cast = c.getString("cast");
                }

                String description = "";
                if (c.has("description")){
                    description = c.getString("description");
                }

                String plot = "";
                if (c.has("plot")){
                    plot = c.getString("plot");
                }

                String age = "";
                if (c.has("age")){
                    age = c.getString("age");
                }

                String country = "";
                if (c.has("country")){
                    country = c.getString("country");
                }

                String genre = "";
                if (c.has("genre")){
                    genre = c.getString("genre");
                }

                String duration_secs = "";
                if (c.has("duration_secs")){
                    duration_secs = c.getString("duration_secs");
                }

                String duration = "";
                if (c.has("duration")){
                    duration = c.getString("duration");
                }

                String rating = "";
                if (c.has("rating")){
                    rating = c.getString("rating");
                }

                ItemInfoMovies objItem1 = new ItemInfoMovies(name, o_name, movie_image, release_date, episode_run_time, youtube_trailer,
                        director, actors, cast, description, plot, age, country, genre, duration_secs, duration,rating);
                arrayListInfo.add(objItem1);
            }

            if (jsonObject.has("movie_data")) {

                JSONObject c =  jsonObject.getJSONObject("movie_data");

                String stream_id = c.getString("stream_id");
                String name = c.getString("name");
                String container = c.getString("container_extension");

                ItemMoviesData objItem2 = new ItemMoviesData(stream_id, name, container);
                arrayListData.add(objItem2);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, arrayListInfo, arrayListData);
        super.onPostExecute(s);
    }
}