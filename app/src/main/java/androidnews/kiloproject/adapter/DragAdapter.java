package androidnews.kiloproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.data.ChannelItem;
import androidnews.kiloproject.widget.DragGrid;
import de.hdodenhof.circleimageview.CircleImageView;

public class DragAdapter extends BaseAdapter {
    /**
     * TAG
     */
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    public List<ChannelItem> channelList;
    private DragGrid dragGrid;
    /**
     * TextView 频道内容
     */
    private TextView itemText;
    /**
     * 要删除的position
     */
    public int remove_position = -1;
    private boolean isDeleteIcon;
    private RelativeLayout riDelete;//删除按钮
    private OnDelecteItemListener listener;
    private boolean isDeleteing;//是否处于删除状态
    private boolean hideDeleteIcon;
    private OnStartDragingListener startDragingListener;

    public DragAdapter(Context context, List<ChannelItem> channelList, DragGrid dragGrid) {
        this.context = context;
        this.channelList = channelList;
        this.dragGrid = dragGrid;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public ChannelItem getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            try {
                return channelList.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.channel_category_item, null);
        ChannelItem channel = getItem(position);
        if (channel == null || TextUtils.equals(channel.getName(), "fake")) {
            view.setVisibility(View.GONE);
            return view;
        }
        itemText = (TextView) view.findViewById(R.id.text_item);
        riDelete = (RelativeLayout) view.findViewById(R.id.ri_delete);
        CircleImageView iconNews = view.findViewById(R.id.icon_new);
        itemText.setText(channel.getName());

//        if ((position == 0)) {
//			itemText.setTextColor(context.getResources().getColor(R.color.black));
//            itemText.setEnabled(false);
//        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            itemText.setText("");
            itemText.setSelected(true);
            itemText.setEnabled(true);
            riDelete.setVisibility(View.INVISIBLE);
            itemText.setVisibility(View.INVISIBLE);//设置如果当前拖拽的view没有放下，那当前位置的view不可见
            isChanged = false;
        }
        if (!isVisible && (position == -1 + channelList.size())) {//TODO 添加item时的处理
            itemText.setText("");
            itemText.setSelected(true);
            itemText.setEnabled(true);
            riDelete.setVisibility(View.INVISIBLE);
            itemText.setVisibility(View.INVISIBLE);//设置如果当前拖拽的view没有放下，那当前位置的view不可见
        }
        if (remove_position == position) {
            itemText.setText("");
            itemText.setSelected(true);
            itemText.setEnabled(true);
            riDelete.setVisibility(View.INVISIBLE);
            itemText.setVisibility(View.INVISIBLE);//设置如果当前拖拽的view没有放下，那当前位置的view不可见
        }
        //TODO 展示删除按钮
        if (isDeleteIcon) {
            if (!isVisible && (position == -1 + channelList.size())
                    || (remove_position == position) && isDeleteing
                    || (position == dragGrid.getShowing()) && !isItemShow) {
                riDelete.setVisibility(View.INVISIBLE);
            } else {
                riDelete.setVisibility(View.VISIBLE);
            }
        }
        //TODO 判断是否展示新条目表示
        int newItem = channel.getNewItem();
        if (newItem == 1) {
            if (!isVisible && (position == -1 + channelList.size())
                    || (remove_position == position) && isDeleteing
                    || (position == dragGrid.getShowing()) && !isItemShow) {
                iconNews.setVisibility(View.INVISIBLE);
            } else {
                iconNews.setVisibility(View.VISIBLE);
            }
        } else {
            iconNews.setVisibility(View.INVISIBLE);
        }
        if (hideDeleteIcon) {
            riDelete.setVisibility(View.INVISIBLE);
            if (position == channelList.size() - 1) {//到最后重置状态
                hideDeleteIcon = false;
            }
        }
        riDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onDelete(position, convertView, parent);
            }
        });
        return view;
    }

    /**
     * 添加频道列表
     */
    public void addItem(ChannelItem channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        try {
            holdPosition = dropPostion;
            ChannelItem dragItem = getItem(dragPostion);
            LogUtils.d("startPostion=" + dragPostion + ";endPosition=" + dropPostion);
            if (dragPostion < dropPostion) {
                channelList.add(dropPostion + 1, dragItem);
                channelList.remove(dragPostion);
            } else {
                channelList.add(dropPostion, dragItem);
                channelList.remove(dragPostion + 1);
            }
            isChanged = true;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取频道列表
     */
    public List<ChannelItem> getChannnelLst() {
        return channelList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        if (remove_position == -1) return;
        channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<ChannelItem> list) {
        channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }

    //显示删除键的icon
    public void showDeleteIcon(boolean isDeleteIcon) {
        this.isDeleteIcon = isDeleteIcon;
        if (startDragingListener != null)
            startDragingListener.onStartDraging();
    }

    //隐藏删除键
    public void hideDeleteIcon(boolean hideDeleteIcon) {
        this.hideDeleteIcon = hideDeleteIcon;
    }

    public interface OnDelecteItemListener {
        void onDelete(int position, View convertView, ViewGroup parent);
    }

    public void setOnDelecteItemListener(OnDelecteItemListener listener) {
        this.listener = listener;
    }

    /**
     * 是否正在删除状态
     *
     * @param isDeleteing
     */
    public void setIsDeleteing(boolean isDeleteing) {
        this.isDeleteing = isDeleteing;
    }

    public interface OnStartDragingListener {
        void onStartDraging();
    }

    public void setOnStartDragingListener(OnStartDragingListener startDragingListener) {
        this.startDragingListener = startDragingListener;
    }
}