package androidnews.kiloproject.bean.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TypeArrayBean implements Parcelable {
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

    public TypeArrayBean() {
    }

    public TypeArrayBean(List<Integer> typeArray) {
        this.typeArray = typeArray;
    }

    protected TypeArrayBean(Parcel in) {
        this.typeArray = new ArrayList<Integer>();
        in.readList(this.typeArray, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<TypeArrayBean> CREATOR = new Parcelable.Creator<TypeArrayBean>() {
        @Override
        public TypeArrayBean createFromParcel(Parcel source) {
            return new TypeArrayBean(source);
        }

        @Override
        public TypeArrayBean[] newArray(int size) {
            return new TypeArrayBean[size];
        }
    };
}
