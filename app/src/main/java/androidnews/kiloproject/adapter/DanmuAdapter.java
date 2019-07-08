package androidnews.kiloproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.orzangleli.xdanmuku.XAdapter;

import java.util.Random;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.data.DanmuEntity;

public class DanmuAdapter extends XAdapter<DanmuEntity> {
    private Context mContext;

    public DanmuAdapter(Context c) {
        super();
        mContext = c;
    }

    @Override
    public View getView(DanmuEntity danmuEntity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_danmu_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.textView.setText(danmuEntity.getUserName() + " : " + danmuEntity.getContent());
        return convertView;
    }

    @Override
    public int[] getViewTypeArray() {
        int type[] = {0};
        return type;
    }

    @Override
    public int getSingleLineHeight() {
        //将所有类型弹幕的布局拿出来，找到高度最大值，作为弹道高度
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_danmu_item, null);
        //指定行高
        view.measure(0, 0);
//        return Math.max(view.getMeasuredHeight(),view2.getMeasuredHeight());
        return view.getMeasuredHeight();
    }


    class ViewHolder {
        public TextView textView;
    }
}
