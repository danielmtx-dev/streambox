package nemosofts.streambox.item;

import java.io.Serializable;

public class ItemDns implements Serializable {

	private final String dns_title;
	private final String dns_base;

	public ItemDns(String dns_title, String dns_base) {
		this.dns_title = dns_title;
		this.dns_base = dns_base;
	}

	public String getTitle() {
		return dns_title;
	}

	public String getBase() {
		return dns_base;
	}

}
