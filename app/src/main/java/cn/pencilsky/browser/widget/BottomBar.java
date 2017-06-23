package cn.pencilsky.browser.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.pencilsky.browser.R;

/**
 * Created by chenlin on 15/06/2017.
 */
public class BottomBar extends RelativeLayout implements View.OnClickListener, View.OnLongClickListener {
    private ImageButton leftButton;
    private ImageButton rightButton;
    private ImageButton centerButton;
    private TextView centerText;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClickLeft(View view);
        void onClickCenter(View view);
        void onClickRight(View view);
        boolean onLongClickLeft(View view);
        boolean onLongClickCenter(View view);
        boolean onLongClickRight(View view);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public BottomBar(Context context) {
        this(context, null);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.widget_bottom_bar, this, true);
        leftButton = (ImageButton) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(this);
        rightButton = (ImageButton) findViewById(R.id.rightButton);
        rightButton.setOnClickListener(this);
        centerButton = (ImageButton) findViewById(R.id.centerButton);
        centerButton.setOnClickListener(this);
        centerText = (TextView) findViewById(R.id.centerText);
        centerText.setOnClickListener(this);
    }

    public CharSequence getCenterText() {
        return centerText.getText();
    }

    public void setCenterText(String text) {
        centerText.setText(text);

        centerButton.setVisibility(View.GONE);
        centerText.setVisibility(View.VISIBLE);
    }

    public void setCenterImg(int resId) {
        centerButton.setImageResource(resId);

        centerText.setVisibility(View.GONE);
        centerButton.setVisibility(View.VISIBLE);
    }

    public void setLeftImg(int resId) {
        leftButton.setImageResource(resId);
    }

    public void setRightImg(int resId) {
        rightButton.setImageResource(resId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:
                if (this.onClickListener != null) {
                    this.onClickListener.onClickLeft(v);
                }
                break;
            case R.id.rightButton:
                if (this.onClickListener != null) {
                    this.onClickListener.onClickRight(v);
                }
                break;
            case R.id.centerButton:
            case R.id.centerText:
                if (this.onClickListener != null) {
                    this.onClickListener.onClickCenter(v);
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:
                if (this.onClickListener != null) {
                    return this.onClickListener.onLongClickLeft(v);
                }
            case R.id.rightButton:
                if (this.onClickListener != null) {
                    return this.onClickListener.onLongClickRight(v);
                }
            case R.id.centerButton:
            case R.id.centerText:
                if (this.onClickListener != null) {
                    return this.onClickListener.onLongClickCenter(v);
                }
        }
        return false;
    }
}
