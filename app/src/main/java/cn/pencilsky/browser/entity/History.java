package cn.pencilsky.browser.entity;

/**
 * Created by chenlin on 21/06/2017.
 */
public class History {
    int id;
    String url;

    public History(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
