package cn.pencilsky.browser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pencilsky.browser.adapter.DownloadRecyclerAdapter;
import cn.pencilsky.browser.adapter.ViewPagerAdapter;
import cn.pencilsky.browser.download.Downloader;
import cn.pencilsky.browser.entity.Download;
import cn.pencilsky.browser.fragment.RecyclerViewFragment;
import cn.pencilsky.browser.service.DownloadService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by chenlin on 19/06/2017.
 */
public class DownloaderActivity extends AppCompatActivity implements DownloadRecyclerAdapter.OnItemClickListener, Downloader.DownloaderInterface{
    @Bind(R.id.backButton)
    ImageButton backButton;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    RecyclerViewFragment downloadFragment = new RecyclerViewFragment();
    RecyclerViewFragment downloadedFragment = new RecyclerViewFragment();

    DownloadRecyclerAdapter downloadRecyclerAdapter;
    DownloadRecyclerAdapter downloadedRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupViewPager();
    }

    @Override
    public void onItemClick(int position, Download model, View v) {
        if (model.getStatus() == Download.DOWNLOADING) {
            Downloader.getInstance(model, DownloaderActivity.this).pause();
            new UpdateUIHandler(getMainLooper(), model).sendMessage(new Message());
        } else {
            Downloader.getInstance(model, DownloaderActivity.this).start();
        }
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // 初始化正在下载Fragment
        DownloadService.getDownloads(DownloaderActivity.this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Download>>() {
                    @Override
                    public void accept(ArrayList<Download> downloads) throws Exception {
                        ArrayList<Download> downloadArrayList = new ArrayList<>();
                        for (Download download : downloads) {
                            if (download.getStatus() != download.DOWNLOADED) {
                                downloadArrayList.add(download);
                                Downloader downloader = Downloader.getInstance(download, DownloaderActivity.this);
                                downloader.setDownloaderInterface(DownloaderActivity.this);
                                // 正在下载状态，则启动下载
                                if (download.getStatus() == download.DOWNLOADING) {
                                    downloader.start();
                                }
                            }
                        }

                        downloadRecyclerAdapter = new DownloadRecyclerAdapter(DownloaderActivity.this, downloadArrayList);
                        downloadFragment.getRecyclerView().setAdapter(downloadRecyclerAdapter);
                        downloadRecyclerAdapter.setOnItemClickListener(DownloaderActivity.this);
                    }
                });
        adapter.addFragment(downloadFragment);

        // 初始化已下载Fragment
        DownloadService.getDownloads(DownloaderActivity.this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Download>>() {
                    @Override
                    public void accept(final ArrayList<Download> downloads) throws Exception {
                        ArrayList<Download> downloadArrayList = new ArrayList<>();
                        for (Download download : downloads) {
                            if (download.getStatus() == download.DOWNLOADED) {
                                downloadArrayList.add(download);
                            }
                        }

                        downloadedRecyclerAdapter = new DownloadRecyclerAdapter(DownloaderActivity.this, downloadArrayList);
                        downloadedFragment.getRecyclerView().setAdapter(downloadedRecyclerAdapter);
                        downloadedRecyclerAdapter.setOnItemClickListener(DownloaderActivity.this);
                    }
                });
        adapter.addFragment(downloadedFragment);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.downloader_download);
        tabLayout.getTabAt(1).setText(R.string.downloader_downloaded);
    }

    @Override
    public void onUpdated(Downloader downloader, long downloadedSize) {
        new UpdateUIHandler(this.getMainLooper(), downloader.getDownload()).sendMessage(new Message());
    }

    @Override
    public void onFinish(Downloader downloader) {

    }

    @Override
    public void onStatusChange(Downloader downloader, String status) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    class UpdateUIHandler extends Handler {
        Download download;
        public UpdateUIHandler(Looper looper, Download download) {
            super(looper);
            this.download = download;
        }

        @Override
        public void handleMessage(Message msg) {
            downloadRecyclerAdapter.updateItem(download);
        }
    }
}
