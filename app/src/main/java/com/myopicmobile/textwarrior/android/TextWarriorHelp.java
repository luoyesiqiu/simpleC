/*
 * Copyright (c) 2011 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.android;

import android.app.Activity;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TextWarriorHelp extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
       // setContentView(luoye.multiplelang.test.R.layout.);
        
		//WebView contents = (WebView) findViewById(R.id.help_content);
//		contents.getSettings().setBuiltInZoomControls(true);
//		contents.setWebViewClient(new EmailLinksClient(this));
//
//		contents.loadUrl(determineHelpFile());
	}

	private String determineHelpFile(){
		String lang = getResources().getConfiguration().locale.getLanguage();
        String helpFile;
        
        //I hate hard-coding
        if(lang.equals("fr")){
        	helpFile = "file:///android_asset/help_fr.html";
        }
        else if(lang.equals("es")){
        	helpFile = "file:///android_asset/help_es.html";
        }
        else if(lang.equals("de")){
        	helpFile = "file:///android_asset/help_de.html";
        }
        else if(lang.equals("zh")){
        	String country =  getResources().getConfiguration().locale.getCountry();
        	if(country.equals("TW") || country.equals("HK")){
        		helpFile = "file:///android_asset/help_zh_tw.html";
        	}
        	else{
            	helpFile = "file:///android_asset/help_zh_cn.html";
        	}
        }
        else{
        	helpFile = "file:///android_asset/help.html";
        }
		return helpFile;
	}
	
	
	// mailto links don't work in WebView. This class fixes that.
	private class EmailLinksClient extends WebViewClient {
		private Activity _activity;
		
		public EmailLinksClient(Activity context){
		    _activity = context;
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {     
		    if(url.startsWith(MailTo.MAILTO_SCHEME)){
		        MailTo mt = MailTo.parse(url);
		        Intent i = new Intent(Intent.ACTION_SEND);
		        i.setType("plain/text");
		        i.putExtra(Intent.EXTRA_EMAIL, new String[]{mt.getTo()});
		        i.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
		        i.putExtra(Intent.EXTRA_CC, mt.getCc());
		        i.putExtra(Intent.EXTRA_TEXT, mt.getBody());
		        _activity.startActivity(Intent.createChooser(i, null));
		        
		        // Apparently on some devices, the clicked link will fail to
		        // invalidate after being pressed, so refresh the page here
		        view.reload();
		        return true;
		    }
		    view.loadUrl(url);
		    return true;
		}
	}
}
