package cn.pencilsky.browser.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.pencilsky.browser.R;
import cn.pencilsky.browser.entity.Bookmark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlin on 19/06/2017.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String bookmarkTable = "create table `bookmark`(`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` varchar(100), `link` varchar(500), `iconResId` int(50))";
        String downloadTable = "create table `download`(`id` INTEGER PRIMARY KEY AUTOINCREMENT, `url` varchar(200), `fileName` varchar(100), `fileSize` int(50), `downloadedSize` int(50), `status` int(2))";
        String historyTable = "create table `history`(`id` INTEGER PRIMARY KEY AUTOINCREMENT, `url` varchar(200))";

        db.execSQL(bookmarkTable);
        db.execSQL(downloadTable);
        db.execSQL(historyTable);

        List<Bookmark> defaultBookmarks = new ArrayList<>();
        defaultBookmarks.add(new Bookmark(-1, R.drawable.ic_google, "Google", "https://www.google.com/"));
        defaultBookmarks.add(new Bookmark(-1, R.drawable.ic_bilibili, "Bilibili", "http://www.bilibili.com/"));
        defaultBookmarks.add(new Bookmark(-1, R.drawable.ic_github, "Github", "https://github.com/"));
        defaultBookmarks.add(new Bookmark(-1, R.drawable.ic_taobao, "淘宝", "https://www.taobao.com/"));
        defaultBookmarks.add(new Bookmark(-1, R.drawable.ic_aiqiyi, "爱奇艺", "http://www.iqiyi.com/"));
        defaultBookmarks.add(new Bookmark(-1, R.drawable.ic_baidu, "百度", "https://www.baidu.com/"));
        defaultBookmarks.add(new Bookmark(-1, R.drawable.ic_zhihu, "知乎", "https://www.zhihu.com/"));

        Bookmark bookmark;
        for (int i = 0; i < defaultBookmarks.size(); ++i) {
            bookmark = defaultBookmarks.get(i);
            ContentValues cv = new ContentValues();
            cv.put("name", bookmark.getName());
            cv.put("link", bookmark.getLink());
            cv.put("iconResId", bookmark.getIconResId());
            db.insert("bookmark", null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static SQLiteDatabase getDB(Context context) {
        return new DBHelper(context, "browser", null, 1).getReadableDatabase();
    }

}
