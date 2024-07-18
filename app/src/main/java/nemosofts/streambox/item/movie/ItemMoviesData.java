package nemosofts.streambox.item.movie;

import java.io.Serializable;

public class ItemMoviesData implements Serializable {

	private final String stream_id;
	private final String name;
	private final String container_extension;

	public ItemMoviesData(String stream_id, String name, String container_extension) {
		this.stream_id = stream_id;
		this.name = name;
		this.container_extension = container_extension;
	}

	public String getStreamID() {
		return stream_id;
	}

	public String getName() {
		return name;
	}

	public String getContainerExtension() {
		return container_extension;
	}
}
