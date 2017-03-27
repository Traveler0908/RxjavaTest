package com.qw.dp.rxjavatest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.btn_RxJava)
    Button btn_RxJava;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.gridRx)
    RecyclerView gridRv;
    ItemListAdapter adapter = new ItemListAdapter();
    private int page = 0;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        gridRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gridRv.setAdapter(adapter);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setDistanceToTriggerSync(100);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.gray_bg));
        //刷新监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        //完成后调用完成的方法
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }

        });
    }

    @OnClick(R.id.btn_RxJava)
    void previousPage() {
        loadPage(--page);
        if (page == 1) {
            btn_RxJava.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_RxJava_NEXT)
    void nextPage() {
        loadPage(++page);
        if (page == 2) {
            btn_RxJava.setEnabled(true);
        }
    }

    private void loadPage(int page) {
        swipeRefreshLayout.setRefreshing(true);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = NetWork.getGankApi()
                .getBeauties(10, page)
                .map(GankBeautyResultToItemsMapper.getInstance())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    Observer<List<Item>> observer = new Observer<List<Item>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(List<Item> images) {
            swipeRefreshLayout.setRefreshing(false);
            adapter.setItems(images);
        }
    };

    public class ItemListAdapter extends RecyclerView.Adapter {
        List<Item> images;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
            return new TestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TestViewHolder testViewHolder = (TestViewHolder) holder;
            Item image = images.get(position);
            Glide.with(holder.itemView.getContext()).load(image.imageUrl).into(testViewHolder.imageIv);
            testViewHolder.descriptionTv.setText(image.description);
        }

        @Override
        public int getItemCount() {
            return images == null ? 0 : images.size();
        }

        public void setItems(List<Item> images) {
            this.images = images;
            notifyDataSetChanged();
        }

        class TestViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.imageIv)
            ImageView imageIv;
            @Bind(R.id.descriptionTv)
            TextView descriptionTv;

            public TestViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }


}
