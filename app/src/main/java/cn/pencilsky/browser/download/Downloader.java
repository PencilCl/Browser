package cn.pencilsky.browser.download;

import android.content.Context;
import cn.pencilsky.browser.entity.Download;
import cn.pencilsky.browser.service.DownloadService;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by chenlin on 19/06/2017.
 */
public class Downloader implements DownloadThread.DownloadInterface {
    private static HashMap<Download, Downloader> downloadDownloaderHashMap;

    int threadSum;
    int finishedThreadSum;
    boolean pause;
    HashMap<String, DownloadThread> downloadThreadHashMap;
    HashMap<String, Long> startPos;
    HashMap<String, Long> endPos;

    DownloaderInterface downloaderInterface;
    Download download;

    Context mContext;

    public void setDownloaderInterface(DownloaderInterface downloaderInterface) {
        this.downloaderInterface = downloaderInterface;
    }

    public Download getDownload() {
        return download;
    }

    public static Downloader getInstance(Download download, Context context) {
        return getInstance(download, context, 5); // 默认开启线程数为5
    }

    /**
     * 通过download对象获取Downloader
     * 如果downloadDownloaderHashMap中不存在，则根据download创建并返回
     * @param download
     * @param context
     * @param threadSum
     * @return
     */
    public static Downloader getInstance(Download download, Context context, int threadSum) {
        if (downloadDownloaderHashMap == null) {
            downloadDownloaderHashMap = new HashMap<>();
        }

        Downloader downloader = downloadDownloaderHashMap.get(download);
        if (downloader == null) {
            downloader = new Downloader();
            downloader.download = download;
            downloader.threadSum = threadSum;
            downloader.mContext = context;

            downloader.init();
            downloadDownloaderHashMap.put(download, downloader);
        }

        return downloader;
    }

    private Downloader() {

    }

    public Downloader(String url, String fileName, long fileSize, long downloadedSize, int threadSum, Context context) {
        this.download = new Download(-1, url, fileName, fileSize, downloadedSize, Download.PAUSED);
        DownloadService.addDownload(context, download)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });
        this.threadSum = threadSum;
        this.mContext = context;

        init();
    }

    private void init() {
        finishedThreadSum = 0;
        downloadThreadHashMap = new HashMap<>();
        startPos = new HashMap<>();
        endPos = new HashMap<>();

        if (downloadDownloaderHashMap == null) {
            downloadDownloaderHashMap = new HashMap<>();
        }
        downloadDownloaderHashMap.put(this.download, this);
    }

    public void start() {
        if (this.download.getStatus() != Download.DOWNLOADING) {
            this.download.setStatus(Download.DOWNLOADING);
            DownloadService.saveDownload(this.mContext, this.download)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {

                        }
                    });
        }
        // 如果还没分配线程，则先分配线程
        if (startPos.size() == 0) {
            long singleLen = download.getFileSize() / threadSum;
            for (int i = 0; i < threadSum; ++i) {
                startPos.put("Thread" + i, i * singleLen);
                // 最后一个线程分配剩余字节；
                endPos.put("Thread" + i, (i == threadSum - 1 ? download.getFileSize() : (i + 1) * singleLen - 1));
            }
        }

        createAndStartThread();
    }

    public void createAndStartThread() {
        finishedThreadSum = 0;

        if (downloaderInterface != null) {
            downloaderInterface.onStatusChange(this, "开始下载");
        }

        for (int i = 0; i < threadSum; ++i) {
            DownloadThread downloadThread = new DownloadThread(download.getUrl(), download.getFileName(), startPos.get("Thread" + i), endPos.get("Thread" + i), this);
            downloadThread.setName("Thread" + i);
            downloadThread.start();
            downloadThreadHashMap.put("Thread" + i, downloadThread);
        }
    }

    public void pause() {
        if (downloaderInterface != null) {
            downloaderInterface.onStatusChange(this, "暂停下载");
        }
        download.setStatus(Download.PAUSED);
        DownloadService.saveDownload(mContext, download)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });
        pause = true;
        Set<String> keys = downloadThreadHashMap.keySet();
        for (String key : keys) {
            downloadThreadHashMap.get(key).pause();
        }
    }

    @Override
    public void finish(DownloadThread downloadThread) {
        download.setStatus(Download.DOWNLOADED);
        DownloadService.saveDownload(mContext, download)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });
        finishedThreadSum += 1;
        if (threadSum == finishedThreadSum && downloaderInterface != null) {
            downloaderInterface.onFinish(this);
        }
    }

    @Override
    public void download(DownloadThread downloadThread, int len) {
        String key = downloadThread.getName();
        startPos.put(key, startPos.get(key) + len);
        long downloadedLen = download.getDownloadedSize() + len;
        download.setDownloadedSize(downloadedLen);
        if (downloaderInterface != null) {
            downloaderInterface.onUpdated(this, downloadedLen);
        }
    }

    public interface DownloaderInterface {
        void onUpdated(Downloader downloader, long downloadedSize);
        void onFinish(Downloader downloader);
        void onStatusChange(Downloader downloader, String status);
    }

}
