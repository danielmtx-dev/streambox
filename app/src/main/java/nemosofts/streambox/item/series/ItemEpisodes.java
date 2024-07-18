package nemosofts.streambox.item.series;

import java.io.Serializable;

public class ItemEpisodes implements Serializable {

	private final String id;
	private final String title;
	private final String container_extension;
	private final String season;
	private final String plot;
	private final String duration;
	private final String rating;
	private final String cover_big;

	public ItemEpisodes(String id, String title, String container_extension, String season, String plot, String duration, String rating, String cover_big) {
		this.id = id;
		this.title = title;
		this.container_extension = container_extension;
		this.season = season;
		this.plot = plot;
		this.duration = duration;
		this.rating = rating;
		this.cover_big = cover_big;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContainerExtension() {
		return container_extension;
	}

	public String getSeason() {
		return season;
	}

	public String getPlot() {
		return plot;
	}

	public String getDuration() {
		return duration;
	}

	public String getRating() {
		return rating;
	}

	public String getCoverBig() {
		return cover_big;
	}
}
