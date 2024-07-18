package nemosofts.streambox.interfaces;

import java.util.ArrayList;

import nemosofts.streambox.item.movie.ItemInfoMovies;
import nemosofts.streambox.item.movie.ItemMoviesData;

public interface MovieIDListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemInfoMovies> arrayListInfo , ArrayList<ItemMoviesData> arrayListMoviesData);
}