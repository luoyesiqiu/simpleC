package com.luoye.simpleC.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.luoye.simpleC.R;
import com.luoye.simpleC.util.IO;
import com.luoye.simpleC.util.Utils;
import com.luoye.simpleC.view.MdWebView;

/**
 * Created by zyw on 2017/8/10.
 */
public class HelpActivity extends AppCompatActivity {

    private MdWebView mdWebView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_compose);
        mdWebView = (MdWebView) findViewById(R.id.previewmdWebView1);
        progressBar = (ProgressBar) findViewById(R.id.webview_progressBar);

        mdWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar.setProgress(newProgress);//设置进度值
                }
            }
        });
        mdWebView.setDownloadListener(new MyWebViewDownLoadListener());
        mdWebView.setWebViewClient(new MyWebViewClient());
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        String title = intent.getStringExtra("title");
        setTitle(title);

        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n<head>\n\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
        sb.append("<style type=\"text/css\">\n");
        sb.append(IO.getFromAssets(this, "markdown.css"));
        sb.append("</style>");
        sb.append("</head>\n<body>\n");
        sb.append(IO.md2html(data));
        sb.append("\n</body>\n");
        sb.append("</html>");
        mdWebView.loadData(sb.toString());

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        mdWebView.goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
            case R.id.menu_join_qq_group:
                Utils.joinQQGroup(this, "UiGfmkfCXFsmxwv1-sQ4LCwnMoXaTuxr");

                break;
            case R.id.menu_api_ref:
                mdWebView.loadUrl(getString(R.string.api_ref));
                break;
            case R.id.menu_exercise:
                mdWebView.loadUrl(getString(R.string.oj));
                break;
            case R.id.menu_app_version:
                showToast(Utils.getAppVersion(this));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private Toast toast;

    private void showToast(CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
