package androidnews.kiloproject.bean.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class IntArrayBean implements Parcelable {
    private List<Integer> typeArray;

    public List<Integer> getTypeArray() {
        return typeArray;
    }

    public void setTypeArray(List<Integer> typeArray) {
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

    public IntArrayBean() {
    }

    public IntArrayBean(List<Integer> typeArray) {
        this.typeArray = typeArray;
    }

    protected IntArrayBean(Parcel in) {
        this.typeArray = new ArrayList<Integer>();
        in.readList(this.typeArray, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<IntArrayBean> CREATOR = new Parcelable.Creator<IntArrayBean>() {
        @Override
        public IntArrayBean createFromParcel(Parcel source) {
            return new IntArrayBean(source);
        }

        @Override
        public IntArrayBean[] newArray(int size) {
            return new IntArrayBean[size];
        }
    };
}
