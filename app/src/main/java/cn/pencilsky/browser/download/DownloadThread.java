package cn.pencilsky.browser.download;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chenlin on 19/06/2017.
 */
public class DownloadThread extends Thread {
    DownloadInterface downloadInterface;
    String url;
    String filename;
    long start;
    long end;
    long downloadedLen;
    boolean exit;

    public DownloadThread(String url, String filename, long start, long end, DownloadInterface downloadInterface) {
        this.url = url;
        this.filename = filename;
        this.start = start;
        this.end = end;
        this.downloadInterface = downloadInterface;

        downloadedLen = 0;
        this.exit = false;
    }

    @Override
    public void run() {
        if (start >= end) {
            downloadInterface.finish(this);
            return ;
        }

        File file = new File(filename);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(start);

            URL urlObj = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlObj.openConnection();
            httpURLConnection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            InputStream is = httpURLConnection.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = is.read(bytes)) != -1 && !exit) {
                downloadedLen += len;
                randomAccessFile.write(bytes, 0, len);
                downloadInterface.download(this, len);
            }

            if (start + downloadedLen >= end) {
                downloadInterface.finish(this);
            }
            randomAccessFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        this.exit = true;
    }

    public interface DownloadInterface {
        void finish(DownloadThread downloadThread);
        void download(DownloadThread downloadThread, int len);
    }
}