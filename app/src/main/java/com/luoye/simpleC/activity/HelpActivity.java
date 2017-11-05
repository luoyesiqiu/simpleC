package com.luoye.simpleC.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.luoye.simpleC.R;
import com.luoye.simpleC.view.MdWebView;


/**
 * Created by zyw on 2017/8/10.
 */
public class HelpActivity extends Activity {

    private MdWebView wv;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_compose);
        wv=(MdWebView)findViewById(R.id.previewmdWebView1);
        progressBar=(ProgressBar)findViewById(R.id.webview_progressBar) ;

        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar.setProgress(newProgress);//设置进度值
                }
            }
        });
        wv.setDownloadListener(new MyWebViewDownLoadListener());
        Intent intent=getIntent();
        String data=intent.getStringExtra("data");
        String title=intent.getStringExtra("title");
        setTitle(title);
        wv.loadUrl(data);

        ActionBar actionBar=getActionBar();

        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
        wv.goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
