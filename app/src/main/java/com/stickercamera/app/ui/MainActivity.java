package com.stickercamera.app.ui;

import android.graphics.BitmapFactory;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.common.util.DataUtils;
import com.common.util.FileUtils;
import com.common.util.StringUtils;
import com.customview.LabelView;
import com.github.skykai.stickercamera.R;
import com.melnykov.fab.FloatingActionButton;
import com.stickercamera.App;
import com.stickercamera.AppConstants;
import com.stickercamera.app.camera.CameraManager;
import com.stickercamera.app.model.FeedItem;
import com.stickercamera.app.model.TagItem;
import com.stickercamera.base.BaseActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 主界面
 * Created by sky on 2015/7/20.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private List<FeedItem> feedList;
    private PictureAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();

        //如果没有照片则打开相机
        String str = DataUtils.getStringPreferences(App.getApp(), AppConstants.FEED_INFO);
        if (StringUtils.isNotEmpty(str)) {
            feedList = JSON.parseArray(str, FeedItem.class);
        }
        if (feedList == null) {
            CameraManager.getInst().openCamera(MainActivity.this);
        } else {
            mAdapter.setList(feedList);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FeedItem feedItem) {
        if (feedList == null) {
            feedList = new ArrayList<FeedItem>();
        }
        feedList.add(0, feedItem);
        DataUtils.setStringPreferences(App.getApp(), AppConstants.FEED_INFO, JSON.toJSONString(feedList));
        mAdapter.setList(feedList);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        titleBar.hideLeftBtn();
        titleBar.hideRightBtn();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PictureAdapter();
        mRecyclerView.setAdapter(mAdapter);
        fab.setOnClickListener(v -> CameraManager.getInst().openCamera(MainActivity.this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //照片适配器
    public class PictureAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<FeedItem> items = new ArrayList<FeedItem>();

        public void setList(List<FeedItem> list) {
            if (items.size() > 0) {
                items.clear();
            }
            items.addAll(list);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            FeedItem feedItem = items.get(position);
            holder.picture.setImageBitmap(BitmapFactory.decodeFile(feedItem.getImgPath()));
            holder.setTagList(feedItem.getTagList());

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            // 将标签移除,避免回收使用时标签重复
            holder.pictureLayout.removeViews(1, holder.pictureLayout.getChildCount() - 1);
            super.onViewRecycled(holder);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            // 这里可能有问题 延迟200毫秒加载是为了等pictureLayout已经在屏幕上显示getWidth才为具体的值
            holder.pictureLayout.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (TagItem feedImageTag : holder.getTagList()) {
                        LabelView tagView = new LabelView(MainActivity.this);
                        tagView.init(feedImageTag);
                        tagView.draw(holder.pictureLayout,
                                (int) (feedImageTag.getX() * ((double) holder.pictureLayout.getWidth() / (double) 1242)),
                                (int) (feedImageTag.getY() * ((double) holder.pictureLayout.getWidth() / (double) 1242)),
                                feedImageTag.isLeft());
                        tagView.wave();
                    }
                }
            }, 200);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pictureLayout)
        RelativeLayout pictureLayout;
        @BindView(R.id.picture)
        ImageView picture;

        private List<TagItem> tagList = new ArrayList<>();

        public List<TagItem> getTagList() {
            return tagList;
        }

        public void setTagList(List<TagItem> tagList) {
            if (this.tagList.size() > 0) {
                this.tagList.clear();
            }
            this.tagList.addAll(tagList);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
