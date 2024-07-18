package nemosofts.streambox.item;

import java.io.Serializable;

public class ItemSingleURL implements Serializable {

	private final String id;
	private final String any_name;
	private final String single_url;

	public ItemSingleURL(String id, String any_name, String single_url) {
		this.id = id;
		this.any_name = any_name;
		this.single_url = single_url;
	}

	public String getId() {
		return id;
	}

	public String getAnyName() {
		return any_name;
	}

	public String getSingleURL() {
		return single_url;
	}
}
