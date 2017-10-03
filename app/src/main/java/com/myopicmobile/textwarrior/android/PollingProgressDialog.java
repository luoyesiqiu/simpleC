/*
 * Copyright (c) 2011 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;

import com.myopicmobile.textwarrior.common.ProgressObserver;
import com.myopicmobile.textwarrior.common.ProgressSource;
import com.myopicmobile.textwarrior.common.TextWarriorException;


public class PollingProgressDialog extends ProgressDialog
implements ProgressObserver, OnCancelListener{
	private Context _context;
	private Handler _handler;
	private ProgressSource _src;
	private int _min;
	private boolean pollActive;
	private final static int POLL_PERIOD = 250; // in milliseconds

	public PollingProgressDialog(Context context, ProgressSource src,
			boolean showProgress, boolean cancellable){
		super(context);
		_src = src;
		_context = context;

		//TODO assert on UI thread
		_handler = new Handler();
		setIndeterminate(!showProgress);
		if(showProgress){
			//setTitle(_context.getString(R.string.progress_dialog_message));
			setIcon(android.R.drawable.ic_dialog_info);
			setProgress(_min);
			setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pollActive = true;
		}
		else{
			//setMessage(_context.getString(R.string.progress_dialog_message));
			setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pollActive = false;
		}
		
		setCancelable(cancellable);
		if(cancellable){
			setButton(BUTTON_NEGATIVE, _context.getString(android.R.string.cancel),
				new DialogInterface.OnClickListener(){
				    public void onClick(DialogInterface dialog, int which) {
				    	dialog.cancel();
				    }
			});
			setOnCancelListener(this);
    	}

		_min = _src.getMin();
		TextWarriorException.assertVerbose(_min < _src.getMax(),
			"Maximum progress value <= min value");
	}
	
	public void startDelayedPollingDialog(){
		_src.registerObserver(this);
		setMax(_src.getMax());
		//wait for a while before showing a dialog
		_handler.postDelayed(_startProgressTask, POLL_PERIOD);
	}
	
	public void startPollingDialog(){
		_src.registerObserver(this);
		setMax(_src.getMax());
		_handler.post(_startProgressTask);
	}
	
	private final Runnable _startProgressTask = new Runnable(){
		public void run(){
			if(pollActive){
				updateProgress(_src.getCurrent());
				_handler.postDelayed(_updateProgressTask, POLL_PERIOD);
			}
			show();
		}
	};
	
	private final Runnable _updateProgressTask = new Runnable(){
		public void run(){
			updateProgress(_src.getCurrent());
		    _handler.postDelayed(_updateProgressTask, POLL_PERIOD);
		}
	};

	private void updateProgress(int value){
		setProgress(value - _min);
	}

	@Override
	public void onComplete(int requestCode, Object result){
		_handler.removeCallbacks(_startProgressTask);
		_handler.removeCallbacks(_updateProgressTask);
		dismiss();
	}
	
	@Override
	public void onError(int requestCode, int errorCode, String message){
		//TODO test this branch
    	//no need to call _src.forceStop() since worker thread would have stopped on error
		_handler.removeCallbacks(_startProgressTask);
		_handler.removeCallbacks(_updateProgressTask);
		dismiss();
	}
	
	@Override
	public void onCancel(DialogInterface dialog){
		_handler.removeCallbacks(_startProgressTask);
		_handler.removeCallbacks(_updateProgressTask);
		_src.forceStop(); //this causes _src to broadcast onCancel(int), which we ignore below
	}

	@Override
	public void onCancel(int requestCode) {
		//do nothing
	}
}
