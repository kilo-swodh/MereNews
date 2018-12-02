package androidnews.kiloproject.bean.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

public class BlockItem extends LitePalSupport{
    private long id;
    private int type;
    private String text;

    public static final int TYPE_SOURCE = 999;
    public static final int TYPE_KEYWORDS = 998;

    public BlockItem(int type, String text) {
        this.type = type;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
