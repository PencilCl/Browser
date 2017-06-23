package cn.pencilsky.browser.entity;

/**
 * Created by chenlin on 16/06/2017.
 */
public class Bookmark {
    private int id;
    private int iconResId;
    private String name;
    private String link;

    public Bookmark(int id, int iconResId, String name, String link) {
        this.id = id;
        this.iconResId = iconResId;
        this.name = name;
        this.link = link;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
