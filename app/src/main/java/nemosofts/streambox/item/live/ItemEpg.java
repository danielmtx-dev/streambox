package nemosofts.streambox.item.live;

import java.io.Serializable;

public class ItemEpg implements Serializable {

	private final String start;
	private final String end;
	private final String title;
	private final String start_timestamp;
	private final String stop_timestamp;
	private final String now_playing;
	private final String has_archive;

	public ItemEpg(String start, String end, String title, String start_timestamp, String stop_timestamp, String now_playing, String has_archive) {
		this.start = start;
		this.end = end;
		this.title = title;
		this.start_timestamp = start_timestamp;
		this.stop_timestamp = stop_timestamp;
		this.now_playing = now_playing;
		this.has_archive = has_archive;
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

	public String getTitle() {
		return title;
	}

	public String getStartTimestamp() {
		return start_timestamp;
	}

	public String getStopTimestamp() {
		return stop_timestamp;
	}

	public String getNowPlaying() {
		return now_playing;
	}

	public String getHasArchive() {
		return has_archive;
	}
}
