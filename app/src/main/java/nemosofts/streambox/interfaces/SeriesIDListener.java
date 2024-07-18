package nemosofts.streambox.interfaces;

import java.util.ArrayList;

import nemosofts.streambox.item.series.ItemEpisodes;
import nemosofts.streambox.item.series.ItemInfoSeasons;
import nemosofts.streambox.item.series.ItemSeasons;

public interface SeriesIDListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemInfoSeasons> arrayListInfo , ArrayList<ItemSeasons> arrayListSeasons, ArrayList<ItemEpisodes> arrayListEpisodes);
}