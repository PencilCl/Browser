package cn.pencilsky.browser.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.AutoCompleteTextView;
import cn.pencilsky.browser.adapter.MyNoFilterArrayAdapter;

/**
 * Created by chenlin on 23/06/2017.
 *
 * 给AutoCompleteTextView设置显示所有候选词汇
 * AutoCompleteTextView默认会对候选词汇进行startsWith方法进行判断
 * 通过该类方法可以实现显示所有传输的候选词汇
 *
 * 传输数据通过调用sendMessage方法
 * 参数:
 * key: words
 * value: String[] 要显示的候选词汇数组
 */
public class SearchSuggestionHandler extends Handler {
    Context context;
    AutoCompleteTextView input;

    public SearchSuggestionHandler(Context context, AutoCompleteTextView input) {
        this.context = context;
        this.input = input;
    }

    @Override
    public void handleMessage(Message msg) {
        MyNoFilterArrayAdapter suggestionArray = new MyNoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, msg.getData().getStringArray("words"));
        input.setAdapter(suggestionArray);
        input.setThreshold(1);
    }
}
