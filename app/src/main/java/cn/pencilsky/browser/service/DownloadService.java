package cn.pencilsky.browser.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.pencilsky.browser.entity.Download;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by chenlin on 20/06/2017.
 */
public class DownloadService {
    public static ArrayList<Download> downloads;

    synchronized public static Observable<ArrayList<Download>> getDownloads(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Download>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Download>> e) throws Exception {
                if (downloads == null) {
                    downloads = new ArrayList<>();
                    // 从本地数据库中载入
                    SQLiteDatabase db = DBHelper.getDB(context);
                    Cursor cursor = db.query("download", null, null, null, null, null, null);

                    int idIndex = cursor.getColumnIndex("id");
                    int urlIndex = cursor.getColumnIndex("url");
                    int fileNameIndex = cursor.getColumnIndex("fileName");
                    int fileSizeIndex = cursor.getColumnIndex("fileSize");
                    int downloadedSizeIndex = cursor.getColumnIndex("downloadedSize");
                    int statusIndex = cursor.getColumnIndex("status");
                    while (cursor.moveToNext()) {
                        Download download = new Download(
                                cursor.getInt(idIndex),
                                cursor.getString(urlIndex),
                                cursor.getString(fileNameIndex),
                                cursor.getInt(fileSizeIndex),
                                cursor.getInt(downloadedSizeIndex),
                                cursor.getInt(statusIndex)
                        );
                        downloads.add(download);
                    }
                    cursor.close();
                    db.close();
                }
                e.onNext(downloads);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存收藏
     * @param context
     * @param download
     * @return 成功返回空字符串, 错误返回错误信息
     */
    synchronized public static Observable<String> addDownload(final Context context, final Download download) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (downloads == null) {
                    downloads = new ArrayList<>();
                }

                SQLiteDatabase db = DBHelper.getDB(context);
                ContentValues cv = new ContentValues();
                cv.put("url", download.getUrl());
                cv.put("fileName", download.getFileName());
                cv.put("fileSize", download.getFileSize());
                cv.put("downloadedSize", download.getDownloadedSize());
                cv.put("status", download.getStatus());
                db.insert("download", null, cv);
                // 获取最后插入的记录id
                Cursor cursor = db.rawQuery("select last_insert_rowid() from download", null);
                int lastId = 0;
                if (cursor.moveToFirst()) lastId = cursor.getInt(0);
                cursor.close();
                db.close();

                if (lastId == 0) {
                    e.onNext("新建下载失败");
                } else {
                    download.setId(lastId);
                    downloads.add(download);
                    e.onNext("");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 删除收藏
     * @param context
     * @param download
     * @return 成功返回空字符串 失败返回错误信息
     */
    synchronized public static Observable<String> removeDownload(final Context context, final Download download) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                SQLiteDatabase db = DBHelper.getDB(context);
                db.delete("download", "id=?", new String[]{String.valueOf(download.getId())});
                if (downloads != null) {
                    downloads.remove(download);
                }
                db.close();
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 更新数据到数据库
     * @param context
     * @param download
     * @return 成功返回空字符串 失败返回错误信息
     */
    synchronized public static Observable<String> saveDownload(final Context context, final Download download) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 本地更新
                SQLiteDatabase db = DBHelper.getDB(context);
                ContentValues cv = new ContentValues();
                cv.put("url", download.getUrl());
                cv.put("fileName", download.getFileName());
                cv.put("fileSize", download.getFileSize());
                cv.put("downloadedSize", download.getStatus() == Download.DOWNLOADED ? download.getFileSize() : 0); // 如果文件尚未下载完，则设置已下载长度为0
                cv.put("status", download.getStatus());
                db.update("download", cv, "id=?", new String[]{String.valueOf(download.getId())});

                db.close();
                // 更新goals数组信息
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io());
    }
}
