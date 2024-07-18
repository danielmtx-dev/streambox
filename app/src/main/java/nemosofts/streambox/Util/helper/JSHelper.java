package nemosofts.streambox.Util.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import nemosofts.streambox.item.ItemCat;
import nemosofts.streambox.item.ItemSeries;
import nemosofts.streambox.item.live.ItemLive;
import nemosofts.streambox.item.movie.ItemMovies;

public class JSHelper {

    private final SharedPreferences sp;
    private final SharedPreferences.Editor ed;

    public JSHelper(@NonNull Context ctx) {
        sp = ctx.getSharedPreferences("json_helper", Context.MODE_PRIVATE);
        ed = sp.edit();
    }

    // Live ----------------------------------------------------------------------------------------
    public int getLiveSize() {
        try {
            String json = sp.getString("json_live", null);
            JSONArray jsonarray = new JSONArray(json);
            return jsonarray.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public ArrayList<ItemLive> getLive(String cat_id, Boolean isRadio) {
        ArrayList<ItemLive> arrayList = new ArrayList<>();
        try {
            String json = sp.getString("json_live", null);
            JSONArray jsonarray = new JSONArray(json);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String category_id= jsonobject.getString("category_id");
                if (!cat_id.isEmpty()){
                    if (category_id.equals(cat_id) && (jsonobject.getString("stream_type").equals("live"))){
                        String name = jsonobject.getString("name");
                        String stream_id = jsonobject.getString("stream_id");
                        String stream_icon = jsonobject.getString("stream_icon");

                        ItemLive objItem = new ItemLive(name,stream_id,stream_icon);
                        arrayList.add(objItem);
                    }
                } else {
                    if (Boolean.TRUE.equals(isRadio)){
                        if (!jsonobject.getString("stream_type").equals("live")){
                            String name = jsonobject.getString("name");
                            String stream_id = jsonobject.getString("stream_id");
                            String stream_icon = jsonobject.getString("stream_icon");

                            ItemLive objItem = new ItemLive(name,stream_id,stream_icon);
                            arrayList.add(objItem);
                        }
                    } else {
                        String name = jsonobject.getString("name");
                        String stream_id = jsonobject.getString("stream_id");
                        String stream_icon = jsonobject.getString("stream_icon");

                        ItemLive objItem = new ItemLive(name,stream_id,stream_icon);
                        arrayList.add(objItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToLiveData(String json) {
        ed.putString("json_live", json);
        ed.apply();
    }

    // Movies --------------------------------------------------------------------------------------
    public int getMoviesSize() {
        try {
            String json = sp.getString("json_movie", null);
            JSONArray jsonarray = new JSONArray(json);
            return jsonarray.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public ArrayList<ItemMovies> getMovies(String cat_id) {
        ArrayList<ItemMovies> arrayList = new ArrayList<>();
        try {
            String json = sp.getString("json_movie", null);
            JSONArray jsonarray = new JSONArray(json);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String category_id= jsonobject.getString("category_id");
                if (!cat_id.isEmpty()){
                    if (category_id.equals(cat_id)){
                        String name = jsonobject.getString("name");
                        String stream_id = jsonobject.getString("stream_id");
                        String stream_icon = jsonobject.getString("stream_icon");
                        String rating = jsonobject.getString("rating");

                        ItemMovies objItem = new ItemMovies(name, stream_id, stream_icon, rating);
                        arrayList.add(objItem);
                    }
                } else {
                    String name = jsonobject.getString("name");
                    String stream_id = jsonobject.getString("stream_id");
                    String stream_icon = jsonobject.getString("stream_icon");
                    String rating = jsonobject.getString("rating");

                    ItemMovies objItem = new ItemMovies(name, stream_id, stream_icon, rating);
                    arrayList.add(objItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToMovieData(String json) {
        ed.putString("json_movie", json);
        ed.apply();
    }

    public ArrayList<ItemCat> getCategoryMovie() {
        ArrayList<ItemCat> arrayList = new ArrayList<>();
        try {
            String json = sp.getString("json_movie_cat", null);
            JSONArray arrayCategory = new JSONArray(json);
            for (int i = 0; i < arrayCategory.length(); i++) {
                JSONObject objectCategory = arrayCategory.getJSONObject(i);

                String id = objectCategory.getString("category_id");
                String name = objectCategory.getString("category_name");

                ItemCat objItem = new ItemCat(id, name);
                arrayList.add(objItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToMovieCatData(String json) {
        ed.putString("json_movie_cat", json);
        ed.apply();
    }

    // Series --------------------------------------------------------------------------------------
    public int getSeriesSize() {
        try {
            String json = sp.getString("json_series", null);
            JSONArray jsonarray = new JSONArray(json);
            return jsonarray.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public ArrayList<ItemSeries> getSeries(String cat_id) {
        ArrayList<ItemSeries> arrayList = new ArrayList<>();
        try {
            String json = sp.getString("json_series", null);
            JSONArray jsonarray = new JSONArray(json);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String category_id= jsonobject.getString("category_id");
                if (!cat_id.isEmpty()){
                    if (category_id.equals(cat_id)){
                        String name= jsonobject.getString("name");
                        String series_id= jsonobject.getString("series_id");
                        String cover= jsonobject.getString("cover");
                        String rating= jsonobject.getString("rating");

                        ItemSeries objItem = new ItemSeries(name, series_id, cover, rating);
                        arrayList.add(objItem);
                    }
                } else {

                    String name= jsonobject.getString("name");
                    String series_id= jsonobject.getString("series_id");
                    String cover= jsonobject.getString("cover");
                    String rating= jsonobject.getString("rating");

                    ItemSeries objItem = new ItemSeries(name, series_id, cover, rating);
                    arrayList.add(objItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToSeriesData(String json) {
        ed.putString("json_series", json);
        ed.apply();
    }

    public ArrayList<ItemCat> getCategorySeries() {
        ArrayList<ItemCat> arrayList = new ArrayList<>();
        try {
            String json = sp.getString("json_series_cat", null);
            JSONArray arrayCategory = new JSONArray(json);
            for (int i = 0; i < arrayCategory.length(); i++) {
                JSONObject objectCategory = arrayCategory.getJSONObject(i);

                String id = objectCategory.getString("category_id");
                String name = objectCategory.getString("category_name");

                ItemCat objItem = new ItemCat(id, name);
                arrayList.add(objItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToSeriesCatData(String json) {
        ed.putString("json_series_cat", json);
        ed.apply();
    }
    
    // Remove --------------------------------------------------------------------------------------
    public void removeAllData() {
        ed.putString("json_live", null);

        ed.putString("json_movie", null);
        ed.putString("json_movie_cat", null);

        ed.putString("json_series", null);
        ed.putString("json_series_cat", null);

        ed.apply();
    }
    public void removeAllSeries() {
        ed.putString("json_series", null);
        ed.putString("json_series_cat", null);
        ed.apply();
    }
    public void removeAllMovies() {
        ed.putString("json_movie", null);
        ed.putString("json_movie_cat", null);
        ed.apply();
    }
    public void removeAllLive() {
        ed.putString("json_live", null);
        ed.apply();
    }

    public Boolean getIsLiveOrder() {
        return sp.getBoolean("live_order", false);
    }
    public void setIsLiveOrder(Boolean flag) {
        ed.putBoolean("live_order", flag);
        ed.apply();
    }

    public Boolean getIsMovieOrder() {
        return sp.getBoolean("movie_order", false);
    }
    public void setIsMovieOrder(Boolean flag) {
        ed.putBoolean("movie_order", flag);
        ed.apply();
    }

    public Boolean getIsSeriesOrder() {
        return sp.getBoolean("series_order", false);
    }
    public void setIsSeriesOrder(Boolean flag) {
        ed.putBoolean("series_order", flag);
        ed.apply();
    }

    public String getUpdateDate() {
        return sp.getString("update_date", "");
    }
    public void setUpdateDate(){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateTime = simpleDateFormat.format(calendar.getTime());
        ed.putString("update_date", currentDateTime);
        ed.apply();
    }
}