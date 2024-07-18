package nemosofts.streambox.adapter.epg;

import java.io.Serializable;
import java.util.ArrayList;

import nemosofts.streambox.item.live.ItemEpg;
import nemosofts.streambox.item.live.ItemLive;

public class ItemPost implements Serializable{

	String id, type;
	ArrayList<ItemLive> arrayListLive = new ArrayList<>();
	ArrayList<ItemEpg> arrayListEpg = new ArrayList<>();

	public ItemPost(String id, String type) {
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public ArrayList<ItemLive> getArrayListLive() {
		return arrayListLive;
	}
	public void setArrayListLive(ArrayList<ItemLive> arrayListLive) {
		this.arrayListLive = arrayListLive;
	}

	public ArrayList<ItemEpg> getArrayListEpg() {
		return arrayListEpg;
	}
	public void setArrayListEpg(ArrayList<ItemEpg> arrayListEpg) {
		this.arrayListEpg = arrayListEpg;
	}
}