package androidnews.kiloproject.bean.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BlockArrayBean implements Parcelable {
    private List<BlockItem> typeArray = new ArrayList<>();

    public List<BlockItem> getTypeArray() {
        return typeArray;
    }

    public void setTypeArray(List<BlockItem> typeArray) {
        this.typeArray = typeArray;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.typeArray);
    }

    public BlockArrayBean() {
    }

    protected BlockArrayBean(Parcel in) {
        this.typeArray = new ArrayList<BlockItem>();
        in.readList(this.typeArray, BlockItem.class.getClassLoader());
    }

    public static final Creator<BlockArrayBean> CREATOR = new Creator<BlockArrayBean>() {
        @Override
        public BlockArrayBean createFromParcel(Parcel source) {
            return new BlockArrayBean(source);
        }

        @Override
        public BlockArrayBean[] newArray(int size) {
            return new BlockArrayBean[size];
        }
    };
}
