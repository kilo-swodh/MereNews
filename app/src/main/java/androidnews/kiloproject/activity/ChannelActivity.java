package androidnews.kiloproject.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.SnackbarUtils;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.adapter.DragAdapter;
import androidnews.kiloproject.adapter.OtherAdapter;
import androidnews.kiloproject.bean.data.ChannelItem;
import androidnews.kiloproject.bean.data.TypeArrayBean;
import androidnews.kiloproject.system.base.BaseActivity;
import androidnews.kiloproject.widget.DragGrid;
import androidnews.kiloproject.widget.OtherGridView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidnews.kiloproject.activity.MainActivity.DEFAULT_PAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_ARRAY;

public class ChannelActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.my_category_text)
    TextView myCategoryText;
    @BindView(R.id.seperate_line)
    View seperateLine;
    @BindView(R.id.more_category_text)
    TextView moreCategoryText;
    @BindView(R.id.subscribe_main_layout)
    LinearLayout subscribeMainLayout;
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

    public static final int SELECT_RESULT = 999;
    String[] tags;

    /**
     * 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
     */
    boolean isMove = false;
    @BindView(R.id.userGridView)
    DragGrid userGridView;
    @BindView(R.id.otherGridView)
    OtherGridView otherGridView;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.tv_save)
    TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        ButterKnife.bind(this);

        initToolbar(toolbar, true);

        initStateBar(R.color.main_background, true);
    }

    @Override
    protected void initSlowly() {
        TypeArrayBean typeArrayBean = CacheDiskUtils.getInstance().getParcelable(CONFIG_TYPE_ARRAY, TypeArrayBean.CREATOR);
        if (typeArrayBean == null || typeArrayBean.getTypeArray() == null) {
            typeArrayBean = new TypeArrayBean();
            typeArrayBean.setTypeArray(new ArrayList<>());
            for (int i = 0; i < DEFAULT_PAGE; i++) {
                typeArrayBean.getTypeArray().add(i);
            }
            CacheDiskUtils.getInstance().put(CONFIG_TYPE_ARRAY, typeArrayBean);
        }
        tags = getResources().getStringArray(R.array.address_tag);
        int userOrder = 1, otherOrder = 1;
        for (int i = 0; i < tags.length; i++) {
            boolean isSame = false;
            for (Integer integer : typeArrayBean.getTypeArray()) {
                if (i == integer)
                    isSame = true;
            }
            if (isSame) {
                userChannelList.add(new ChannelItem(i, tags[i], userOrder, 1, 0));
                userOrder++;
            } else {
                otherChannelList.add(new ChannelItem(i, tags[i], otherOrder, 0, 0));
                otherOrder++;
            }
        }

        userAdapter = new DragAdapter(this, userChannelList, userGridView);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(this, otherChannelList);
        otherGridView.setAdapter(this.otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
        userAdapter.setOnDelecteItemListener(new DragAdapter.OnDelecteItemListener() {
            @Override
            public void onDelete(final int position, View v, ViewGroup parent) {
                if (position != 0) {
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
                                    otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                    userAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
            }
        });

        userAdapter.setOnStartDragingListener(new DragAdapter.OnStartDragingListener() {
            @Override
            public void onStartDraging() {
                tvSave.setVisibility(View.GONE);
                tvComplete.setVisibility(View.VISIBLE);
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
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                //TODO position为 0的不可以进行任何操作
                Toast.makeText(getBaseContext(), tags[position], Toast.LENGTH_SHORT).show();
//			if (position != 0) {
//				final ImageView moveImageView = getView(view);
//				if (moveImageView != null) {
//					TextView newTextView = (TextView) view.findViewById(R.id.text_item);
//					final int[] startLocation = new int[2];
//					newTextView.getLocationInWindow(startLocation);
//					final ChannelItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
//					otherAdapter.setVisible(false);
//					//添加到最后一个
//					otherAdapter.addItem(channel);
//					new Handler().postDelayed(new Runnable() {
//						public void run() {
//							try {
//								int[] endLocation = new int[2];
//								//获取终点的坐标
//								otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
//								MoveAnim(moveImageView, startLocation , endLocation, channel,userGridView);
//								userAdapter.setRemove(position);
//							} catch (Exception localException) {
//							}
//						}
//					}, 50L);
//				}
//			}
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
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
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
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 退出时候保存选择后数据库的设置
     */
    private void saveChannel() {
        List<Integer> result = new ArrayList<Integer>();
        if (userChannelList.size() < 1)
            result.add(0);
        else
            for (ChannelItem item : userChannelList) {
                result.add(item.getId());
            }
        SnackbarUtils.with(moreCategoryText).setMessage(getString(R.string.successfully)).show();
        CacheDiskUtils.getInstance().put(CONFIG_TYPE_ARRAY, new TypeArrayBean(result));
        setResult(RESULT_OK);
        finish();
    }

    @OnClick({R.id.tv_complete, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_complete:
                userAdapter.hideDeleteIcon(true);
                userAdapter.showDeleteIcon(false);
                userAdapter.notifyDataSetChanged();
                tvComplete.setVisibility(View.GONE);
                tvSave.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_save:
                saveChannel();
                break;
        }
    }
}
