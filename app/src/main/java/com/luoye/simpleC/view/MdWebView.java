package com.luoye.simpleC.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MdWebView extends WebView

{
	Context context;

	String baseUrl = "file:///android_asset/";
	public MdWebView(Context context)
	{
		
		super(context);
		this.context=context;
		
		
		init();
	}
	
	public MdWebView(Context context, AttributeSet attr)
	{
		super(context,attr);
		this.context=context;
		init();
	}
	public void loadData(String data)
	{
		loadDataWithBaseURL(baseUrl,data,"text/html","UTF-8",null);
		//Toast.makeText(context,"滑动",5000).show();
		setWebViewClient(new MyWebViewClient());
	}
	
	private void init()
	{
		getSettings().setJavaScriptEnabled(true);
		getSettings().setDefaultTextEncodingName("utf-8");
	}

	private  class MyWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			loadUrl(url);
			return  true;
		}
	}

}
