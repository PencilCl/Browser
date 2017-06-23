package cn.pencilsky.browser.entity;

/**
 * Created by chenlin on 19/06/2017.
 */
public class Download {
    public final static int DOWNLOADING = 0;
    public final static int DOWNLOADED = 1;
    public final static int PAUSED = 2;

    private int id;
    private String url;
    private String fileName;
    private long fileSize;
    private long downloadedSize;
    private int status;

    public Download(int id, String url, String fileName, long fileSize, long downloadedSize, int status) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.downloadedSize = downloadedSize;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
