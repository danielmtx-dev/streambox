package nemosofts.streambox.item.movie;

import java.io.Serializable;

public class ItemInfoMovies implements Serializable {

	private final String name;
	private final String o_name;
	private final String movie_image;
	private final String release_date;
	private final String episode_run_time;
	private final String youtube_trailer;
	private final String director;
	private final String actors;
	private final String cast;
	private final String description;
	private final String plot;
	private final String age;
	private final String country;
	private final String genre;
	private final String duration_secs;
	private final String duration;
	private final String rating;

	public ItemInfoMovies(String name, String o_name, String movie_image, String release_date, String episode_run_time, String youtube_trailer, String director, String actors, String cast, String description, String plot, String age, String country, String genre, String duration_secs, String duration, String rating) {
		this.name = name;
		this.o_name = o_name;
		this.movie_image = movie_image;
		this.release_date = release_date;
		this.episode_run_time = episode_run_time;
		this.youtube_trailer = youtube_trailer;
		this.director = director;
		this.actors = actors;
		this.cast = cast;
		this.description = description;
		this.plot = plot;
		this.age = age;
		this.country = country;
		this.genre = genre;
		this.duration_secs = duration_secs;
		this.duration = duration;
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public String getOName() {
		return o_name;
	}

	public String getMovieImage() {
		return movie_image;
	}

	public String getReleaseDate() {
		return release_date;
	}

	public String getEpisodeRunTime() {
		return episode_run_time;
	}

	public String getYoutubeTrailer() {
		return youtube_trailer;
	}

	public String getDirector() {
		return director;
	}

	public String getActors() {
		return actors;
	}

	public String getCast() {
		return cast;
	}

	public String getDescription() {
		return description;
	}

	public String getPlot() {
		return plot;
	}

	public String getAge() {
		return age;
	}

	public String getCountry() {
		return country;
	}

	public String getGenre() {
		return genre;
	}

	public String getDurationSecs() {
		return duration_secs;
	}

	public String getDuration() {
		return duration;
	}

	public String getRating() {
		return rating;
	}
}
