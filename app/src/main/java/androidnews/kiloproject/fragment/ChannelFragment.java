package androidnews.kiloproject.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;

import androidnews.kiloproject.R;
import androidnews.kiloproject.adapter.DragAdapter;
import androidnews.kiloproject.adapter.OtherAdapter;
import androidnews.kiloproject.entity.data.ChannelItem;
import androidnews.kiloproject.system.base.BaseActivity;
import androidnews.kiloproject.system.base.BaseFragment;
import androidnews.kiloproject.widget.DragGrid;
import androidnews.kiloproject.widget.OtherGridView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.activity.MainActivity.DEFAULT_PAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_ARRAY;

public class ChannelFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    TextView myCategoryText;
    TextView moreCategoryText;
    /**
     * 用户栏目对应的适配器，可以拖动
     */
    DragAdapter userAdapter;
    /**
     * 其它栏目对应的适配器
     */
    OtherAdapter otherAdapter;

    ArrayList<ChannelItem> userChannelList = new ArrayList<>();
    ArrayList<ChannelItem> otherChannelList = new ArrayList<>();

    /**
     * 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
     */
    boolean isMove = false;

    DragGrid userGridView;

    OtherGridView otherGridView;

    String[] subTags;

    public int startPosition = 0;
    public int endPosition = 0;

    public static ChannelFragment newInstance(int start, int end) {
        ChannelFragment f = new ChannelFragment();
        Bundle b = new Bundle();
        b.putInt("start", start);
        b.putInt("end", end);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            startPosition = args.getInt("start");
            endPosition = args.getInt("end");
        }
        View view = inflater.inflate(R.layout.fragment_draggrid, container, false);
        myCategoryText = (TextView) view.findViewById(R.id.my_category_text);
        moreCategoryText = (TextView) view.findViewById(R.id.more_category_text);
        userGridView = (DragGrid) view.findViewById(R.id.userGridView);
        otherGridView = (OtherGridView) view.findViewById(R.id.otherGridView);
        initData();
        return view;
    }

    private void initData() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                int[] channelArray = new int[DEFAULT_PAGE];
                String arrayStr = SPUtils.getInstance().getString(CONFIG_TYPE_ARRAY);
                if (TextUtils.isEmpty(arrayStr) && startPosition == 0) {
                    for (int i = 0; i < DEFAULT_PAGE; i++) {
                        channelArray[i] = i;
                    }
                } else {
                    String[] channelStrArray = arrayStr.split("#");
                    channelArray = new int[channelStrArray.length];
                    for (int i = 0; i < channelStrArray.length; i++) {
                        channelArray[i] = Integer.parseInt(channelStrArray[i]);
                    }
                }

                String[] totalTags = getResources().getStringArray(R.array.address_tag);
                subTags = new String[endPosition - startPosition + 1];
                for (int i = 0; i < subTags.length; i++) {
                    subTags[i] = totalTags[startPosition + i];
                }

                int userOrder = 1, otherOrder = 1;

                for (Integer integer : channelArray) {
                    int samePosition = -1;
                    for (int i = 0; i < subTags.length; i++) {
                        if (i + startPosition == integer)
                            samePosition = i;
                    }
                    if (samePosition != -1) {
                        userChannelList.add(new ChannelItem(samePosition, subTags[samePosition], userOrder, 1, 0));
                        userOrder++;
                    }
                }

                for (int i = 0; i < subTags.length; i++) {
                    boolean isSame = false;
                    for (ChannelItem item : userChannelList) {
                        if (TextUtils.equals(item.name, subTags[i])) {
                            isSame = true;
                        }
                    }
                    if (!isSame) {
                        otherChannelList.add(new ChannelItem(i, subTags[i], otherOrder, 0, 0));
                        otherOrder++;
                    }
                }
                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            userAdapter = new DragAdapter(mActivity, userChannelList, userGridView);
                            userGridView.setAdapter(userAdapter);
                            otherAdapter = new OtherAdapter(mActivity, otherChannelList);
                            otherGridView.setAdapter(ChannelFragment.this.otherAdapter);
                            //设置GRIDVIEW的ITEM的点击监听
                            otherGridView.setOnItemClickListener(ChannelFragment.this);
                            userGridView.setOnItemClickListener(ChannelFragment.this);
                            userAdapter.setOnDelecteItemListener(new DragAdapter.OnDelecteItemListener() {
                                @Override
                                public void onDelete(final int position, View v, ViewGroup parent) {
                                    View view = userGridView.getChildAt(position);
                                    final ImageView moveImageView = getView(view);
                                    if (moveImageView != null) {
                                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                                        final int[] startLocation = new int[2];
                                        newTextView.getLocationInWindow(startLocation);
                                        final ChannelItem channel = userAdapter.getItem(position);//获取点击的频道内容
                                        otherAdapter.setVisible(false);
                                        userAdapter.setIsDeleteing(true);
                                        //添加到最后一个
                                        otherAdapter.addItem(channel);
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                try {
                                                    int[] endLocation = new int[2];
                                                    //获取终点的坐标
                                                    int otherPosition = otherAdapter.getLastVaildPositon() - 1;
                                                    if (otherPosition < 0)otherPosition = 0;
                                                    otherGridView.getChildAt(otherPosition).getLocationInWindow(endLocation);
                                                    MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                                    userAdapter.setRemove(position);
                                                } catch (Exception localException) {
                                                }
                                            }
                                        }, 50L);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    /**
     * GRIDVIEW对应的ITEM点击监听接口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            ((BaseActivity)mActivity).swipePanel.setLeftEnabled(false);
            ((BaseActivity)mActivity).swipePanel.setRightEnabled(false);
            return;
        }else {
            ((BaseActivity)mActivity).swipePanel.setLeftEnabled(true);
            ((BaseActivity)mActivity).swipePanel.setRightEnabled(true);
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                //TODO position为 0的不可以进行任何操作
                ToastUtils.showShort(R.string.channel_tip1);
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ChannelItem channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
                    userAdapter.setVisible(false);
                    //添加到最后一个
                    channel.setNewItem(1);
                    userAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, otherGridView);
                                otherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final ChannelItem moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(mActivity);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(mActivity);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 退出时候保存选择后数据库的设置
     */
    public int[] getChannel() {
        int[] channelArray = null;
        if (userChannelList.size() < 1)
            channelArray = new int[0];
        else {
            channelArray = new int[userChannelList.size()];
            for (int i = 0; i < userChannelList.size(); i++) {
                channelArray[i] = userChannelList.get(i).getId() + startPosition;
            }
        }
        return channelArray;
    }
}
