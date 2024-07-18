package nemosofts.streambox.item;

import java.io.Serializable;

public class ItemUsersDB implements Serializable {

	private final String id;
	private final String any_name;
	private final String user_name;
	private final String user_pass;
	private final String user_url;

	public ItemUsersDB(String id, String any_name, String user_name, String user_pass, String user_url) {
		this.id = id;
		this.any_name = any_name;
		this.user_name = user_name;
		this.user_pass = user_pass;
		this.user_url = user_url;
	}

	public String getId() {
		return id;
	}

	public String getAnyName() {
		return any_name;
	}

	public String getUseName() {
		return user_name;
	}

	public String getUserPass() {
		return user_pass;
	}

	public String getUserURL() {
		return user_url;
	}
}
