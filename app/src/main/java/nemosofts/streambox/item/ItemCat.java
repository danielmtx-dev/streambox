package nemosofts.streambox.item;

import java.io.Serializable;

public class ItemCat implements Serializable {
	
	private final String id;
	private final String name;

	public ItemCat(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
