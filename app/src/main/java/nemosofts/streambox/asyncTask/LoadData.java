package nemosofts.streambox.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;

import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.Util.SharedPref;
import nemosofts.streambox.Util.helper.Helper;
import nemosofts.streambox.Util.helper.JSHelper;
import nemosofts.streambox.callback.Callback;
import nemosofts.streambox.interfaces.DataListener;

public class LoadData extends AsyncTask<String, String, String> {

    private final JSHelper jsHelper;
    private final Helper helper;
    private final SharedPref sharedPref;
    private final DataListener listener;
    private JSONArray arrayLive;
    private JSONArray arraySeries;
    private JSONArray arrayMovies;

    public LoadData(Context ctx, DataListener listener) {
        this.listener = listener;
        helper = new Helper(ctx);
        sharedPref = new SharedPref(ctx);
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
            if (jsHelper.getUpdateDate().isEmpty()){
                jsHelper.setUpdateDate();
                return "1";
            } else {
                // 1 Hours
                if (Boolean.TRUE.equals(ApplicationUtil.calculateUpdateHours(jsHelper.getUpdateDate(), 1))){
                    jsHelper.setUpdateDate();

                    try {
                        if (!sharedPref.getCurrent(Callback.TAG_SERIES).isEmpty()){
                            String json_series = ApplicationUtil.responsePost(sharedPref.getAPI(), helper.getAPIRequest("get_series",sharedPref.getUserName(), sharedPref.getPassword()));
                            arraySeries = new JSONArray(json_series);
                            if (arraySeries.length() != 0 && arraySeries.length() != jsHelper.getSeriesSize()){
                                jsHelper.addToSeriesData(json_series);
                                Callback.successSeries = "1";
                            } else {
                                jsHelper.removeAllSeries();
                                sharedPref.setCurrentDateEmpty(Callback.TAG_SERIES);
                                Callback.successSeries = "2";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (!sharedPref.getCurrent(Callback.TAG_MOVIE).isEmpty()){
                            String json_movie = ApplicationUtil.responsePost(sharedPref.getAPI(), helper.getAPIRequest("get_vod_streams",sharedPref.getUserName(), sharedPref.getPassword()));
                            arrayMovies = new JSONArray(json_movie);
                            if (arrayMovies.length() != 0 && arrayMovies.length() != jsHelper.getMoviesSize()){
                                jsHelper.addToMovieData(json_movie);
                                Callback.successMovies = "1";
                            } else {
                                jsHelper.removeAllMovies();
                                sharedPref.setCurrentDateEmpty(Callback.TAG_MOVIE);
                                Callback.successMovies = "2";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (!sharedPref.getCurrent(Callback.TAG_TV).isEmpty()){
                            String json_live = ApplicationUtil.responsePost(sharedPref.getAPI(), helper.getAPIRequest("get_live_streams",sharedPref.getUserName(), sharedPref.getPassword()));
                            arrayLive = new JSONArray(json_live);
                            if (arrayLive.length() != 0 && arrayLive.length() != jsHelper.getLiveSize()){
                                jsHelper.addToLiveData(json_live);
                                Callback.successLive = "1";
                            } else if (arrayLive.length() == 0){
                                jsHelper.removeAllLive();
                                sharedPref.setCurrentDateEmpty(Callback.TAG_TV);
                                Callback.successLive = "2";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return "1";
                } else {
                    return "2";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, arrayLive, arraySeries, arrayMovies);
        super.onPostExecute(s);
    }
}