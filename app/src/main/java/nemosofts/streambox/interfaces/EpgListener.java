package nemosofts.streambox.interfaces;

import java.util.ArrayList;

import nemosofts.streambox.item.live.ItemEpg;

public interface EpgListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemEpg> epgArrayList);
}