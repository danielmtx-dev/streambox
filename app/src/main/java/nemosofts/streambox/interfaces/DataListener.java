package nemosofts.streambox.interfaces;

import org.json.JSONArray;

public interface DataListener {
    void onStart();
    void onEnd(String success, JSONArray arrayLive, JSONArray arraySeries, JSONArray arrayMovies);
}