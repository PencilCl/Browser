package cn.pencilsky.browser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pencilsky.browser.adapter.BookmarkRecyclerAdapter;
import cn.pencilsky.browser.download.Downloader;
import cn.pencilsky.browser.entity.Bookmark;
import cn.pencilsky.browser.entity.History;
import cn.pencilsky.browser.service.BookmarkService;
import cn.pencilsky.browser.service.HistoryService;
import cn.pencilsky.browser.util.CommonUtil;
import cn.pencilsky.browser.util.SearchSuggestionHandler;
import cn.pencilsky.browser.util.ToastUtil;
import cn.pencilsky.browser.widget.BottomBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements
        BookmarkRecyclerAdapter.OnItemClickListener, View.OnClickListener,
        BottomBar.OnClickListener, PopupMenu.OnMenuItemClickListener {
    @Bind(R.id.input)
    AutoCompleteTextView input;
    @Bind(R.id.bookmark)
    RecyclerView bookmark;
    @Bind(R.id.bottomBar)
    BottomBar bottomBar;
    @Bind(R.id.scan)
    ImageButton scan;
    @Bind(R.id.homePage)
    RelativeLayout homePage;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    BookmarkRecyclerAdapter adapter;

    SearchSuggestionHandler searchSuggestionHandler;

    boolean exitNext; // 标记下次点击返回键是否退出程序
    boolean inHomepage; // 判断当前是否处于主页
    String currentUrl; // 记录当前访问url

    static String DOWNLOAD_PATH;
    static final int DOWNLOAD_THREAD_NUM = 5;

    static Pattern linkPattern = Pattern.compile("^(((ht|f)tps?)://)?[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/Download/";
        searchSuggestionHandler = new SearchSuggestionHandler(this, input);
        init();
    }

    private void init() {
        exitNext = false;
        switchToHomePage();
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        scan.setOnClickListener(this);
        bottomBar.setOnClickListener((BottomBar.OnClickListener) this);

        initInput();
        initBookmark();
        initWebView();
    }

    private void initInput() {
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    // 处理用户点击键盘上的搜索功能

                    // 隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    switchToWebView();

                    String inputContent = input.getText().toString();
                    // 如果输入内容为链接，则直接跳转到链接
                    if (linkPattern.matcher(inputContent).find()) {
                        webView.loadUrl(inputContent);
                    } else { // 否则使用百度搜索
                        webView.loadUrl(String.format("https://www.baidu.com/s?ie=utf-8&wd=%s", input.getText().toString()));
                    }
                    return true;
                }
                return false;
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            static final String SEARCH_TAG = "search";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), SEARCH_TAG);

                final String searchContent = s.toString();

                if ("".equals(searchContent)) {
                    return ;
                }

                Observable<Response<String>> observable = OkGo.<String>get(String.format("https://sp0.baidu.com/5a1Fazu8AA54nxGko9WTAnF6hhy/su?wd=%s", searchContent))
                        .tag(SEARCH_TAG)
                        .converter(new StringConvert())
                        .adapt(new ObservableResponse<String>());

                Observable.zip(observable, BookmarkService.getBookmarks(MainActivity.this), HistoryService.getHistories(MainActivity.this), new Function3<Response<String>, ArrayList<Bookmark>, ArrayList<History>, String[]>() {
                    @Override
                    public String[] apply(Response<String> stringResponse, ArrayList<Bookmark> bookmarks, ArrayList<History> histories) throws Exception {
                        // 处理百度推荐
                        String s = stringResponse.body();
                        s = s.substring(s.indexOf("[") + 1, s.lastIndexOf("]")).replace("\"", "");
                        String[] words = s.split(",");
                        // 处理Bookmark和History推荐
                        ArrayList<String> suggestions = new ArrayList<>();
                        for (Bookmark bookmark : bookmarks) {
                            if (bookmark.getLink().contains(searchContent)) {
                                suggestions.add(bookmark.getLink());
                            }
                        }
                        for (History history : histories) {
                            if (history.getUrl().contains(searchContent)) {
                                suggestions.add(history.getUrl());
                            }
                        }

                        if (suggestions.size() != 0) {
                            words = Arrays.copyOf(words, words.length + suggestions.size()); // 扩充数组
                            String[] suggestionArray = suggestions.toArray(new String[suggestions.size()]);
                            System.arraycopy(suggestionArray, 0, words, words.length - suggestionArray.length, suggestionArray.length); // 合并数组
                        }

                        return words;
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String[]>() {
                            @Override
                            public void accept(String[] words) throws Exception {
                                Bundle data = new Bundle();
                                data.putStringArray("words", words);
                                Message message = new Message();
                                message.setData(data);

                                searchSuggestionHandler.sendMessage(message);
                            }
                        });
            }
        });
    }

    private void initBookmark() {
        //设置布局管理器
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        bookmark.setLayoutManager(layoutManager);

        BookmarkService.getBookmarks(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Bookmark>>() {
                    @Override
                    public void accept(ArrayList<Bookmark> bookmarks) throws Exception {
                        adapter = new BookmarkRecyclerAdapter(MainActivity.this, bookmarks);
                        bookmark.setAdapter(adapter);
//                        ItemTouchHelper helper = new ItemTouchHelper(new MyItemTouchCallback(adapter));
//                        helper.attachToRecyclerView(bookmark);
                        adapter.setOnItemClickListener(MainActivity.this);
                    }
                });
    }

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // 添加支持缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                // 处理浏览器下载
                // 获取文件名
                Pattern pattern = Pattern.compile("filename=\"(.*?)\"");
                Matcher matcher = pattern.matcher(contentDisposition);
                String filename;
                if (matcher.find()) {
                    filename = matcher.group(1);
                } else {
                    filename = CommonUtil.getFileNameFromUrl(url);
                }
                // 开始下载
                new Downloader(url, DOWNLOAD_PATH + filename, contentLength, 0, DOWNLOAD_THREAD_NUM, MainActivity.this).start();
                ToastUtil.showShort(MainActivity.this, String.format("开始下载文件 %s", filename));
            }
        });

         // 监听scroll事件，解决与swipeRefreshLayout滑动冲突问题
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (webView.getScrollY() == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onItemClick(int position, Bookmark model) {
        switchToWebView();
        webView.loadUrl(model.getLink());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan:
                System.out.println("scanning binary code");
                break;
        }
    }

    @Override
    public void onClickLeft(View view) {

    }

    @Override
    public void onClickCenter(View view) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            // WebView 正在加载，则停止加载
            webView.stopLoading();
        } else if (!inHomepage) {
            switchToHomePage();
            input.requestFocus();
            // 显示软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * BottomBar 右边被点击事件
     * 弹出主菜单
     * @param view
     */
    @Override
    public void onClickRight(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater()
                .inflate(R.menu.main_menu, popupMenu.getMenu());
        Menu menu = popupMenu.getMenu();
        if (!inHomepage) {
            menu.getItem(2).setTitle(R.string.addBookmark);
            for (Bookmark bookmark : BookmarkService.bookmarks) {
                if (bookmark.getLink().equals(currentUrl)) {
                    menu.getItem(2).setTitle(R.string.removeBookmark);
                }
            }
        }
        menu.getItem(2).setVisible(!inHomepage);
        menu.getItem(3).setVisible(!inHomepage);
        menu.getItem(4).setEnabled(webView.canGoForward());
        menu.getItem(5).setEnabled(webView.canGoBack() || !inHomepage);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onLongClickLeft(View view) {
        return false;
    }

    @Override
    public boolean onLongClickCenter(View view) {
        return false;
    }

    @Override
    public boolean onLongClickRight(View view) {
        return false;
    }

    /**
     * 处理主菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.downloader:
                startActivity(new Intent(MainActivity.this, DownloaderActivity.class));
                break;
            case R.id.bookmarkAndHistory:
                startActivityForResult(new Intent(MainActivity.this, BookmarkAndHistoryActivity.class), BookmarkAndHistoryActivity.REQUEST_CODE);
                break;
            case R.id.backward:
                onBackPressed();
                break;
            case R.id.forward:
                if (inHomepage) {
                    switchToWebView();
                } else {
                    webView.goForward();
                }
                break;
            case R.id.mark:
                markCurrentUrl();
                break;
            case R.id.refresh:
                webView.reload();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

        // 退出前对未保存的历史记录进行保存
        HistoryService.saveHistory(this);
    }

    /**
     * 处理点击收藏菜单事件
     * 如果当前url未被收藏 则收藏
     * 否则取消收藏
     */
    private void markCurrentUrl() {
        BookmarkService.getBookmarks(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Bookmark>>() {
                    @Override
                    public void accept(ArrayList<Bookmark> bookmarks) throws Exception {
                        for (Bookmark bookmark : bookmarks) {
                            if (bookmark.getLink().equals(currentUrl)) {
                                // 取消收藏
                                removeBookmark(bookmark);
                                return ;
                            }
                        }
                        // 收藏
                        addCurrentToBookmark();
                    }
                });
    }

    /**
     * 将当前网页从收藏夹中移除
     * @param bookmark
     */
    private void removeBookmark(Bookmark bookmark) {
        BookmarkService.removeBookmark(this, bookmark)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        ToastUtil.showShort(MainActivity.this, "".equals(s) ? "取消收藏成功" : "取消收藏失败");
                    }
                });
    }

    /**
     * 将当前网页添加到收藏夹中
     */
    private void addCurrentToBookmark() {
        BookmarkService.addBookmark(MainActivity.this, new Bookmark(-1, R.drawable.ic_website, bottomBar.getCenterText().toString(), currentUrl))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        ToastUtil.showShort(MainActivity.this, "".equals(s) ? "收藏成功" : "收藏失败");
                    }
                });
    }

    private void switchToHomePage() {
        inHomepage = true;
        swipeRefreshLayout.setVisibility(View.GONE);
        homePage.setVisibility(View.VISIBLE);
    }

    private void switchToWebView() {
        inHomepage = false;
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        homePage.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BookmarkAndHistoryActivity.REQUEST_CODE && resultCode == BookmarkAndHistoryActivity.RESULT_CODE_OPEN_URL) {
            // 打开在收藏夹/历史记录中点击的链接
            switchToWebView();
            webView.loadUrl(data.getStringExtra("url"));
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (progressBar != null) {
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            switchToWebView();
            currentUrl = url;
            bottomBar.setCenterImg(R.drawable.ic_close);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (inHomepage) {
                return ;
            }
            // 获取页面标题
            view.evaluateJavascript("document.title", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    bottomBar.setCenterText(value);
                }
            });

            // 保存历史记录
            HistoryService.addHistory(MainActivity.this, new History(-1, url))
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {

                        }
                    });
        }

        /**
         * 捕获 mail to 链接
         * 启动发送邮件activity
         * @param view
         * @param url
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:")) {
                // 启动发送邮件
                MailTo mt = MailTo.parse(url);
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse(url));
                i.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
                i.putExtra(Intent.EXTRA_TEXT, mt.getBody());
                MainActivity.this.startActivity(i);
                view.reload();
                return true;
            }

            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (inHomepage) {
                if (exitNext) {
                    super.onBackPressed();
                } else {
                    exitNext = true;
                    ToastUtil.showShort(this, R.string.exit_confirm);

                    // 2s 后重新设置exitNext为false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            exitNext = false;
                        }
                    }).start();
                }
            } else {
                switchToHomePage();
                webView.stopLoading();
                bottomBar.setCenterImg(R.drawable.ic_arrow_up);
            }
        }
    }
}
