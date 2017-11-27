package com.myopicmobile.textwarrior.android;
import android.widget.*;
import android.content.*;
import android.view.View.*;
import android.view.*;

import com.luoye.simpleC.R;
import com.myopicmobile.textwarrior.common.ColorScheme.Colorable;
import com.myopicmobile.textwarrior.common.DocumentProvider;

import android.content.res.*;

public class ClipboardPanel {
	protected FreeScrollingTextField _textField;
	private Context _context;
	private ActionMode  _clipboardActionMode;
	private final boolean DEBUG=false;
	public ClipboardPanel(FreeScrollingTextField textField) {
		_textField = textField;
		_context = textField.getContext();
		
		
	}


	public Context getContext() {
		return _context;
	}

	public void show() {
		startClipboardAction();
	}

	public void hide() {
		stopClipboardAction();
	}

	@SuppressWarnings("ResourceType")
	public void startClipboardAction() {
		// TODO: Implement this method
		if (_clipboardActionMode == null)
			_textField.startActionMode(new ActionMode.Callback(){

					@Override
					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						// TODO: Implement this method
						_clipboardActionMode = mode;
						mode.setTitle(android.R.string.selectTextMode);
						TypedArray array = _context.getTheme().obtainStyledAttributes(new int[] {  
																						  android.R.attr.actionModeSelectAllDrawable, 
																						  android.R.attr.actionModeCutDrawable, 
																						  android.R.attr.actionModeCopyDrawable, 
																						  android.R.attr.actionModePasteDrawable, 
																					  }); 
						menu.add(0, 0, 0, _context.getString(android.R.string.selectAll))
							.setShowAsActionFlags(2)
							.setAlphabeticShortcut('a')
							.setIcon(array.getDrawable(0));

						menu.add(0, 1, 0, _context.getString(android.R.string.cut))
							.setShowAsActionFlags(2)
							.setAlphabeticShortcut('x')
							.setIcon(array.getDrawable(1));

						menu.add(0, 2, 0, _context.getString(android.R.string.copy))
							.setShowAsActionFlags(2)
							.setAlphabeticShortcut('c')
							.setIcon(array.getDrawable(2));
						
						menu.add(0, 3, 0, _context.getString(android.R.string.paste))
							.setShowAsActionFlags(2)
							.setAlphabeticShortcut('v')
							.setIcon(array.getDrawable(3));
						menu.add(0, 4, 0, _context.getString(R.string.comment))
								.setShowAsActionFlags(2)
								.setAlphabeticShortcut('v')
								.setIcon(getContext().getResources().getDrawable(R.drawable.slash));
						array.recycle();
						return true;
					}

					@Override
					public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
						// TODO: Implement this method
						return false;
					}

					@Override
					public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
						// TODO: Implement this method
						switch (item.getItemId()) {
							case 0:
								_textField.selectAll();
								break;
							case 1:
								_textField.cut();
								mode.finish();
								break;
							case 2:
								_textField.copy();
								mode.finish();
								break;
							case 3:
								_textField.paste();
								mode.finish();
								break;
							case 4:
								dealComment();
								mode.finish();
								break;
						}
						return false;
					}

					@Override
					public void onDestroyActionMode(ActionMode p1) {
						// TODO: Implement this method
						_textField.selectText(false);
						_clipboardActionMode = null;
					}
				});

	}

	private  void   dealComment()
	{
		DocumentProvider documentProvider=_textField.createDocumentProvider();
		int startRowNum=documentProvider.findRowNumber(_textField.getSelectionStart());
		int endRowNum =documentProvider.findRowNumber(_textField.getSelectionEnd());

		for(int i=startRowNum;i<=endRowNum;i++) {
			if (isLineComment(i)) {
				unCommentRow(i);
			}
			else
			{
				commentRow(i);
			}
		}
	}
	private  void log(String log)
	{
		if(DEBUG)
		System.out.println("-------------->"+log);
	}
	/*
	 *判断是否为行注释
	 */
	public boolean isLineComment(int row){
		DocumentProvider documentProvider=_textField.createDocumentProvider();
		int rowStart=documentProvider.getRowOffset(row);
		documentProvider.seekChar(rowStart);//调到该行的开始
		int offset=0;
		while (documentProvider.hasNext()){
			char ch=documentProvider.next();
			log("ch1 :"+ch+","+(int)ch);
			if(ch!='/'&&ch!=' ')
				return  false;
			char nextCh=documentProvider.charAt(rowStart+offset+1);
			log("nextCh1 :"+nextCh+","+(int)ch);
			if(ch=='/'&&nextCh=='/'){
				return true;
			}
			++offset;
		}
		return  false;
	}

	/**
	 * 取消注释
	 */
	public void unCommentRow(int row)
	{
		DocumentProvider documentProvider=_textField.createDocumentProvider();
		int rowStart=documentProvider.getRowOffset(row);
		documentProvider.seekChar(rowStart);//调到该行的开始
		log("rowStart:"+rowStart);
		int offset=0;
		while (documentProvider.hasNext()){

			char ch=documentProvider.next();
			log("ch2:"+ch);
			char nextCh=documentProvider.charAt(rowStart+offset+1);
			log("nextCh2:"+nextCh);
			if(ch=='/'&&nextCh=='/'){
				documentProvider.deleteAt(rowStart+offset,System.nanoTime());
				documentProvider.deleteAt(rowStart+offset,System.nanoTime());//删除一个‘/’后，第二个'/'的位置变成了原来第一个的位置
				_textField.respan();
				return;
			}
			++offset;
		}


	}
	public void commentRow(int row)
	{
		DocumentProvider documentProvider=_textField.createDocumentProvider();
		documentProvider.insert(documentProvider.getRowOffset(row),"/");
		documentProvider.insert(documentProvider.getRowOffset(row),"/");
		_textField.respan();
	}
	public void stopClipboardAction() {
		if (_clipboardActionMode != null) {
			_clipboardActionMode.finish();
			_clipboardActionMode = null;
		}
	}

}
