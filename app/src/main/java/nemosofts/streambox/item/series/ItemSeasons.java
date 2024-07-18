package nemosofts.streambox.item.series;

import java.io.Serializable;

public class ItemSeasons implements Serializable {

	private final String id;
	private final String name;
	private final String season_number;

	public ItemSeasons( String id, String name, String season_number) {
		this.id = id;
		this.name = name;
		this.season_number = season_number;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSeasonNumber() {
		return season_number;
	}
}
