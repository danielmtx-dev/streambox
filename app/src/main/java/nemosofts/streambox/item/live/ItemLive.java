package nemosofts.streambox.item.live;

import java.io.Serializable;

public class ItemLive implements Serializable {

	private final String name;
	private final String stream_id;
	private final String stream_icon;

	public ItemLive(String name, String stream_id, String stream_icon) {
		this.name = name;
		this.stream_id = stream_id;
		this.stream_icon = stream_icon;
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

}
