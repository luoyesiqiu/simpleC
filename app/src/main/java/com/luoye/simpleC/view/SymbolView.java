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
import com.luoye.simpleC.util.Utils;

/**
 * Created by zyw on 2017/11/4.
 */
public class SymbolView {
    private final int TILE_WIDTH = 60;
    private final String symbol = "→{}();,=\"|'&![]<>+-\\/*.%~?#$@:_";
    public static  final  String TAB_SYMBOL = "-->";
    private LinearLayout linearLayout;
    private PopupWindow popupWindow;
    private View rootView;
    private OnSymbolViewClick onSymbolViewClick;
    private boolean visible = false;
    private InputMethodManager inputMethodManager;
    private boolean isFirst = true;
    private int maxLayoutHeight = 0;//布局总长
    private int currentLayoutHeight = 0;//当前布局高

    public SymbolView(Context context, final View rootView) {
        this.rootView = rootView;
        popupWindow = new PopupWindow(context);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = LayoutInflater.from(context).inflate(R.layout.symbol_view, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.linear_container);
        final float[] tempPoint = new float[2];
        for (int i = 0; i < symbol.length(); i++) {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            if (i == 0) {
                textView.setText(TAB_SYMBOL);
            } else {
                textView.setText(String.valueOf(symbol.charAt(i)));
            }
            textView.setClickable(true);
            textView.setTextSize(25);
            textView.setMinWidth(TILE_WIDTH);
            textView.setOnTouchListener(new View.OnTouchListener() {
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

            linearLayout.addView(textView, layoutParams);

        }
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.getBackground().setAlpha(0);//窗口完全透明
        view.setBackgroundColor(Color.argb(0xee, 0xff, 0xff, 0xff));//视图不完全透明

        popupWindow.setContentView(view);
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
                        if (currentLayoutHeight == maxLayoutHeight || !visible) {
                            hide();
                        } else if (currentLayoutHeight < maxLayoutHeight && visible) {
                            show(rootView.getHeight() - r.bottom);
                        }
                    }
                });
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private void show(int bottom) {
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, bottom);
    }

    private void hide() {
        popupWindow.dismiss();
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
