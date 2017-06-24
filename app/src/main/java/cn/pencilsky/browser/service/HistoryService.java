package cn.pencilsky.browser.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.pencilsky.browser.entity.History;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by chenlin on 21/06/2017.
 */
public class HistoryService {
    public static ArrayList<History> histories;

    private final static int MAX_HISTORY_CACHE = 10; // 当新增加的历史记录大于等于10时将历史记录保存到数据库中
    private static ArrayList<History> newHistories; // 新增加的历史记录

    synchronized public static Observable<ArrayList<History>> getHistories(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<History>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<History>> e) throws Exception {
                if (histories == null) {
                    histories = new ArrayList<>();
                    // 从本地数据库中载入
                    SQLiteDatabase db = DBHelper.getDB(context);
                    Cursor cursor = db.query("history", null, null, null, null, null, null);

                    int idIndex = cursor.getColumnIndex("id");
                    int urlIndex = cursor.getColumnIndex("url");
                    while (cursor.moveToNext()) {
                        History history = new History(
                                cursor.getInt(idIndex),
                                cursor.getString(urlIndex)
                        );
                        histories.add(history);
                    }
                    db.close();
                    cursor.close();
                }
                e.onNext(histories);
            }
        }).subscribeOn(Schedulers.io());
    }

    synchronized public static Observable<String> addHistory(final Context context, final History history) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (histories == null) {
                    histories = new ArrayList<>();
                }
                if (newHistories == null) {
                    newHistories = new ArrayList<>();
                }

                // 如果历史记录已经存在，则不进行添加
                if (hasHistory(history.getUrl())) {
                    e.onNext("");
                    return ;
                }

                histories.add(history);
                newHistories.add(history);

                if (newHistories.size() >= MAX_HISTORY_CACHE) {
                    saveHistory(context);
                }

                e.onNext("");
            }
        }).subscribeOn(Schedulers.io());
    }

    synchronized public static Observable<String> removeHistory(final Context context, final History history) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (history.getId() != -1) {
                    // 如果已保存到数据库中，则需要在数据库中删除
                    SQLiteDatabase db = DBHelper.getDB(context);
                    db.delete("history", "id=?", new String[]{String.valueOf(history.getId())});
                    db.close();
                }

                if (histories != null) {
                    histories.remove(history);
                }
                if (newHistories != null) {
                    newHistories.remove(history);
                }
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io());
    }

    synchronized public static Observable<String> removeAllHistory(final Context context) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                SQLiteDatabase db = DBHelper.getDB(context);
                db.delete("history", null, null);
                if (histories != null) {
                    histories.clear();
                }
                db.close();
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io());
    }

    synchronized public static void saveHistory(Context context) {
        if (newHistories == null) return ;

        SQLiteDatabase db = DBHelper.getDB(context);
        History saveHistory;
        for (int i = 0; i < newHistories.size(); ++i) {
            saveHistory = newHistories.get(i);
            ContentValues cv = new ContentValues();
            cv.put("url", saveHistory.getUrl());
            db.insert("history", null, cv);
            // 获取最后插入的记录id
            Cursor cursor = db.rawQuery("select last_insert_rowid() from history", null);
            int lastId = 0;
            if (cursor.moveToFirst()) lastId = cursor.getInt(0);
            cursor.close();
            newHistories.remove(saveHistory);
            saveHistory.setId(lastId);
        }

        db.close();
    }


    /**
     * 判断当前历史记录中是否包含url
     * @param url
     * @return
     */
    private static boolean hasHistory(String url) {
        for (History history : histories) {
            if (history.getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }
}
