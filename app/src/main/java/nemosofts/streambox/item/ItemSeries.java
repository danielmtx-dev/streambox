package nemosofts.streambox.item;

import java.io.Serializable;

public class ItemSeries implements Serializable {

	private final String name;
	private final String series_id;
	private final String cover;
	private final String rating;

	public ItemSeries(String name, String series_id, String cover, String rating) {
		this.name = name;
		this.series_id = series_id;
		this.cover = cover;
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public String getSeriesID() {
		return series_id;
	}

	public String getCover() {
		return cover;
	}

	public String getRating() {
		return rating;
	}
}
