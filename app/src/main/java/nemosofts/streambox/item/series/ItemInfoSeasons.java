package nemosofts.streambox.item.series;

import java.io.Serializable;

public class ItemInfoSeasons implements Serializable {

	private final String name;
	private final String cover;
	private final String plot;
	private final String director;
	private final String genre;
	private final String releaseDate;
	private final String rating;
	private final String rating_5based;
	private final String youtube_trailer;

	public ItemInfoSeasons(String name, String cover, String plot, String director, String genre, String releaseDate, String rating, String rating_5based, String youtube_trailer) {
		this.name = name;
		this.cover = cover;
		this.plot = plot;
		this.director = director;
		this.genre = genre;
		this.releaseDate = releaseDate;
		this.rating = rating;
		this.rating_5based = rating_5based;
		this.youtube_trailer = youtube_trailer;
	}

	public String getName() {
		return name;
	}

	public String getCover() {
		return cover;
	}

	public String getPlot() {
		return plot;
	}

	public String getDirector() {
		return director;
	}

	public String getGenre() {
		return genre;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public String getRating() {
		return rating;
	}

	public String getRating5based() {
		return rating_5based;
	}

	public String getYoutubeTrailer() {
		return youtube_trailer;
	}

}
