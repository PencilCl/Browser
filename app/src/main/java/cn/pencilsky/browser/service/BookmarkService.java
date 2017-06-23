package cn.pencilsky.browser.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.pencilsky.browser.entity.Bookmark;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by chenlin on 19/06/2017.
 */
public class BookmarkService {
    public static ArrayList<Bookmark> bookmarks;

    synchronized public static Observable<ArrayList<Bookmark>> getBookmarks(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Bookmark>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Bookmark>> e) throws Exception {
                if (bookmarks == null) {
                    bookmarks = new ArrayList<>();
                    // 从本地数据库中载入
                    SQLiteDatabase db = DBHelper.getDB(context);
                    Cursor cursor = db.query("bookmark", null, null, null, null, null, null);

                    int idIndex = cursor.getColumnIndex("id");
                    int nameIndex = cursor.getColumnIndex("name");
                    int linkIndex = cursor.getColumnIndex("link");
                    int iconResIdIndex = cursor.getColumnIndex("iconResId");
                    while (cursor.moveToNext()) {
                        Bookmark bookmark = new Bookmark(
                                cursor.getInt(idIndex),
                                cursor.getInt(iconResIdIndex),
                                cursor.getString(nameIndex),
                                cursor.getString(linkIndex)
                        );
                        bookmarks.add(bookmark);
                    }
                    cursor.close();
                }
                e.onNext(bookmarks);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存收藏
     * @param context
     * @param bookmark
     * @return 成功返回空字符串, 错误返回错误信息
     */
    synchronized public static Observable<String> addBookmark(final Context context, final Bookmark bookmark) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (bookmarks == null) {
                    bookmarks = new ArrayList<>();
                }

                SQLiteDatabase db = DBHelper.getDB(context);
                ContentValues cv = new ContentValues();
                cv.put("name", bookmark.getName());
                cv.put("link", bookmark.getLink());
                cv.put("iconResId", bookmark.getIconResId());
                db.insert("bookmark", null, cv);
                // 获取最后插入的记录id
                Cursor cursor = db.rawQuery("select last_insert_rowid() from bookmark", null);
                int lastId = 0;
                if (cursor.moveToFirst()) lastId = cursor.getInt(0);
                cursor.close();

                if (lastId == 0) {
                    e.onNext("添加收藏失败");
                } else {
                    bookmark.setId(lastId);
                    bookmarks.add(bookmark);
                    e.onNext("");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 删除收藏
     * @param context
     * @param bookmark
     * @return 成功返回空字符串 失败返回错误信息
     */
    synchronized public static Observable<String> removeBookmark(final Context context, final Bookmark bookmark) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                SQLiteDatabase db = DBHelper.getDB(context);
                db.delete("bookmark", "id=?", new String[]{String.valueOf(bookmark.getId())});
                if (bookmarks != null) {
                    bookmarks.remove(bookmark);
                }
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io());
    }
}
