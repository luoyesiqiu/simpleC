package com.luoye.simpleC.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.luoye.simpleC.R;
import com.luoye.simpleC.resource.util.Setting;

/**
 * Created by zyw on 2017/11/4.
 */
public class SymbolView {
    private final int TILE_WIDTH = 60;
    private final String symbol = "→{}();,=\"|'&![]<>+-\\/*.%~?#$@:_";
    public static final String TAB_SYMBOL = "-->";
    private PopupWindow mPopupWindow;
    private View mRootView;
    private OnSymbolViewClick onSymbolViewClick;
    private boolean mVisible = false;
    private InputMethodManager inputMethodManager;
    private boolean isFirst = true;
    private int maxLayoutHeight = 0;//布局总长
    private int currentLayoutHeight = 0;//当前布局高
    private static SymbolView thiz;
    private View symbolView;
    private TextView[] textViewList;

    public static SymbolView getInstance(Context context, final View rootView) {
        if (thiz == null) {
            thiz = new SymbolView(context, rootView);
        }
        return thiz;
    }

    public SymbolView(Context context, final View rootView) {
        this.mRootView = rootView;
        mPopupWindow = new PopupWindow(context);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        symbolView = LayoutInflater.from(context).inflate(R.layout.symbol_view, null);
        LinearLayout linearLayout = (LinearLayout) symbolView.findViewById(R.id.linear_container);
        final float[] tempPoint = new float[2];
        textViewList = new TextView[symbol.length()];
        for (int i = 0; i < symbol.length(); i++) {
            textViewList[i] = new TextView(context);
            textViewList[i].setGravity(Gravity.CENTER);
            if (i == 0) {
                textViewList[i].setText(TAB_SYMBOL);
            } else {
                textViewList[i].setText(String.valueOf(symbol.charAt(i)));
            }
            textViewList[i].setClickable(true);
            textViewList[i].setTextSize(25);
            textViewList[i].setMinWidth(TILE_WIDTH);
            textViewList[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int color = v.getDrawingCacheBackgroundColor();
                    int motionEvent = event.getAction();
                    TextView tv = (TextView) v;

                    if (motionEvent == MotionEvent.ACTION_DOWN) {
                        tempPoint[0] = event.getX();
                        tempPoint[1] = event.getY();
                        tv.setBackgroundColor(Color.GRAY);

                    } else if (motionEvent == MotionEvent.ACTION_UP || motionEvent == MotionEvent.ACTION_CANCEL) {

                        tv.setBackgroundColor(color);
                        if (Math.abs(event.getX() - tempPoint[0]) < TILE_WIDTH) {
                            if (onSymbolViewClick != null)
                                onSymbolViewClick.onClick(tv, tv.getText().toString());
                        }
                    }
                    return true;
                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            linearLayout.addView(textViewList[i], layoutParams);

        }
        mPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.getBackground().setAlpha(0);//窗口完全透明
        Setting setting = Setting.getInstance(context);
        if (setting.isDarkMode()) {
            symbolView.setBackgroundColor(Color.argb(0xee, 0x0, 0x0, 0x0));//视图不完全透明
        } else {
            symbolView.setBackgroundColor(Color.argb(0xee, 0xff, 0xff, 0xff));//视图不完全透明
        }

        mPopupWindow.setContentView(symbolView);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        if (isFirst) {
                            maxLayoutHeight = r.bottom;//初始化时为布局的最高高度
                            isFirst = false;
                        } else {
                            currentLayoutHeight = r.bottom;//当前弹出的布局高
                        }
                        if (currentLayoutHeight == maxLayoutHeight || !isVisible()) {
                            hide();
                        } else if (currentLayoutHeight < maxLayoutHeight && isVisible()) {
                            show(rootView.getHeight() - r.bottom);
                        }
                    }
                });
    }

    public void useNightTheme(boolean night) {
        if (symbolView == null) {
            return;
        }
        if (night) {
            symbolView.setBackgroundColor(Color.argb(0xee, 0x0, 0x0, 0x0));//视图不完全透明
            for (TextView textView : textViewList) {
                if (textView == null) continue;
                textView.setTextColor(0xffffffff);
            }
        } else {
            symbolView.setBackgroundColor(Color.argb(0xee, 0xff, 0xff, 0xff));//视图不完全透明
            for (TextView textView : textViewList) {
                if (textView == null) continue;
                textView.setTextColor(0xff000000);
            }
        }
    }

    public void setVisible(boolean visible) {
        this.mVisible = visible;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    private void show(int bottom) {
        mPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, bottom);
    }

    public void hide() {
        mPopupWindow.dismiss();
    }

    public void setOnSymbolViewClick(OnSymbolViewClick onSymbolViewClick) {
        this.onSymbolViewClick = onSymbolViewClick;
    }


    public interface OnSymbolViewClick {
        void onClick(View view, String text);
    }

    private void log(String log) {
        System.out.println(log);
    }
}
