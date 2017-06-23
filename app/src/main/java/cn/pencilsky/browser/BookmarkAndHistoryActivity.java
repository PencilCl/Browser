package cn.pencilsky.browser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pencilsky.browser.adapter.BookmarkListRecyclerAdapter;
import cn.pencilsky.browser.adapter.HistoryRecyclerAdapter;
import cn.pencilsky.browser.adapter.ViewPagerAdapter;
import cn.pencilsky.browser.entity.Bookmark;
import cn.pencilsky.browser.entity.History;
import cn.pencilsky.browser.fragment.RecyclerViewFragment;
import cn.pencilsky.browser.service.BookmarkService;
import cn.pencilsky.browser.service.HistoryService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by chenlin on 19/06/2017.
 */
public class BookmarkAndHistoryActivity extends AppCompatActivity {
    final public static int REQUEST_CODE = 10001;
    final public static int RESULT_CODE_OPEN_URL = 10002;
    final public static int RESULT_CODE_NOTHING = 10003;

    @Bind(R.id.backButton)
    ImageButton backButton;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    RecyclerViewFragment bookmarkFragment;
    RecyclerViewFragment historyFragment;

    BookmarkListRecyclerAdapter bookmarkAdapter;
    HistoryRecyclerAdapter historyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_history);
        ButterKnife.bind(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setupViewPager();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        bookmarkFragment = new RecyclerViewFragment();
        bookmarkFragment.setOnCreateRecyclerView(new RecyclerViewFragment.OnCreateRecyclerView() {
            @Override
            public void onCreateRecyclerView(RecyclerView recyclerView) {
                BookmarkService.getBookmarks(BookmarkAndHistoryActivity.this)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<Bookmark>>() {
                            @Override
                            public void accept(final ArrayList<Bookmark> bookmarks) throws Exception {
                                bookmarkAdapter = new BookmarkListRecyclerAdapter(BookmarkAndHistoryActivity.this, bookmarks);
                                bookmarkFragment.getRecyclerView().setAdapter(bookmarkAdapter);
                                bookmarkAdapter.setOnItemClickListener(new BookmarkListRecyclerAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, Bookmark model) {
                                        Intent data = new Intent();
                                        data.putExtra("url", model.getLink());
                                        setResult(RESULT_CODE_OPEN_URL, data);
                                        finish();
                                    }

                                    @Override
                                    public boolean OnItemLongClick(int position, Bookmark model) {
                                        return false;
                                    }
                                });
                            }
                        });
            }
        });
        adapter.addFragment(bookmarkFragment);

        historyFragment = new RecyclerViewFragment();
        historyFragment.setOnCreateRecyclerView(new RecyclerViewFragment.OnCreateRecyclerView() {
            @Override
            public void onCreateRecyclerView(RecyclerView recyclerView) {
                HistoryService.getHistories(BookmarkAndHistoryActivity.this)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<History>>() {
                            @Override
                            public void accept(final ArrayList<History> histories) throws Exception {
                            historyAdapter = new HistoryRecyclerAdapter(BookmarkAndHistoryActivity.this, histories);
                            historyFragment.getRecyclerView().setAdapter(historyAdapter);
                            historyAdapter.setOnItemClickListener(new HistoryRecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position, History model) {
                                    Intent data = new Intent();
                                    data.putExtra("url", model.getUrl());
                                    setResult(RESULT_CODE_OPEN_URL, data);
                                    finish();
                                }

                                @Override
                                public boolean OnItemLongClick(int position, History model) {
                                    return false;
                                }
                            });
                            }
                        });
            }
        });
        adapter.addFragment(historyFragment);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.bookmark_history_bookmark);
        tabLayout.getTabAt(1).setText(R.string.bookmark_history_history);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CODE_NOTHING);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
