package androidnews.kiloproject.bean.data;

import android.os.Parcel;
import android.os.Parcelable;

public class BlockItem implements Parcelable {
    int type;
    String text;

    public static final int TYPE_SOURCE = 999;
    public static final int TYPE_KEYWORDS = 998;

    public BlockItem(int type, String text) {
        this.type = type;
        this.text = text;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.text);
    }

    protected BlockItem(Parcel in) {
        this.type = in.readInt();
        this.text = in.readString();
    }

    public static final Parcelable.Creator<BlockItem> CREATOR = new Parcelable.Creator<BlockItem>() {
        @Override
        public BlockItem createFromParcel(Parcel source) {
            return new BlockItem(source);
        }

        @Override
        public BlockItem[] newArray(int size) {
            return new BlockItem[size];
        }
    };
}
