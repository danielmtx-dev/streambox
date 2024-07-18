package nemosofts.streambox.item;

import androidx.annotation.DrawableRes;

import java.io.Serializable;

public class ItemSetting implements Serializable {

	private final String id;
	private final String name;
	private final String subTitle;
	@DrawableRes
	public final int drawableResId;

	public ItemSetting(String id, String name, String subTitle, @DrawableRes int drawableResId) {
		this.id = id;
		this.name = name;
		this.subTitle = subTitle;
		this.drawableResId = drawableResId;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public int getDrawableData() {
		return drawableResId;
	}
}
