package cn.pencilsky.browser;

import android.app.Application;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenlin on 20/06/2017.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //使用内存保持cookie，app退出后，cookie消失
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        //信任所有证书
        HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory();
        OkGo.getInstance()
                .init(this)
                .setOkHttpClient(builder.build());
    }
}
