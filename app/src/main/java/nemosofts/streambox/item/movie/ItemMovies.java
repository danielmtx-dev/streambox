package nemosofts.streambox.item.movie;

import java.io.Serializable;

public class ItemMovies implements Serializable {

	private final String name;
	private final String stream_id;
	private final String stream_icon;
	private final String rating;

	public ItemMovies(String name, String stream_id, String stream_icon, String rating) {
		this.name = name;
		this.stream_id = stream_id;
		this.stream_icon = stream_icon;
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public String getStreamID() {
		return stream_id;
	}

	public String getStreamIcon() {
		return stream_icon;
	}

	public String getRating() {
		return rating;
	}

}
