package com.luoye.simpleC.view;

import android.content.*;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.myopicmobile.textwarrior.android.*;
import com.myopicmobile.textwarrior.common.*;
import java.io.*;

public class TextEditor extends FreeScrollingTextField {
	private Document _inputtingDoc;
	private boolean _isWordWrap;
	private Context mContext;
	private String _lastSelectFile;
	private int _index;
	public TextEditor(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public TextEditor(Context context,AttributeSet attributeSet){
		super(context,attributeSet);
		mContext = context;
		init();
	}

	private  void init()
	{
		setTypeface(Typeface.MONOSPACE);

		DisplayMetrics dm=mContext.getResources().getDisplayMetrics();

		float size= TypedValue.applyDimension(2, BASE_TEXT_SIZE_PIXELS, dm);
		setTextSize((int)size);
		setShowLineNumbers(true);
		setHighlightCurrentRow(true);
		setWordWrap(false);
		setAutoIndentWidth(2);
		Lexer.setLanguage(LanguageC.getInstance());
		setNavigationMethod(new YoyoNavigationMethod(this));
		int textColor = Color.BLACK;// 默认文字颜色
		int selectionText =Color.argb(255,0,120,215);//选择文字颜色
		setTextColor(textColor);
		setTextHighlightColor(selectionText);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO: Implement this method
		super.onLayout(changed, left, top, right, bottom);
		if (_index != 0 && right > 0) {
			moveCaret(_index);
			_index = 0;
		}
	}

	public void setDark(boolean isDark) {
		if (isDark)
			setColorScheme(new ColorSchemeDark());
		else
			setColorScheme(new ColorSchemeLight());
	}

	public void addNames(String[] names) {
		LanguageC lang=(LanguageC) Lexer.getLanguage();
		String[] old=lang.getNames();
		String[] news=new String[old.length + names.length];
		System.arraycopy(old, 0, news, 0, old.length);
		System.arraycopy(names, 0, news, old.length, names.length);
		lang.setNames(news);
		Lexer.setLanguage(lang);
		respan();
		invalidate();

	}

	public void setPanelBackgroundColor(int color) {
		// TODO: Implement this method
		_autoCompletePanel.setBackgroundColor(color);
	}

	public void setPanelTextColor(int color) {
		// TODO: Implement this method
		_autoCompletePanel.setTextColor(color);
	}
	
	public void setKeywordColor(int color) {
		getColorScheme().setColor(ColorScheme.Colorable.KEYWORD, color);
	}

	public void setUserWordColor(int color) {
		getColorScheme().setColor(ColorScheme.Colorable.LITERAL, color);
	}

	public void setBaseWordColor(int color) {
		getColorScheme().setColor(ColorScheme.Colorable.NAME, color);
	}

	public void setStringColor(int color) {
		getColorScheme().setColor(ColorScheme.Colorable.STRING, color);
	}

	public void setCommentColor(int color) {
		getColorScheme().setColor(ColorScheme.Colorable.COMMENT, color);
	}

	public void setBackgroundColor(int color) {
		getColorScheme().setColor(ColorScheme.Colorable.BACKGROUND, color);
	}

	public void setTextColor(int color) {
		getColorScheme().setColor(ColorScheme.Colorable.FOREGROUND, color);
	}

	public void setTextHighlightColor(int color) {
		getColorScheme().setColor(ColorScheme.Colorable.SELECTION_BACKGROUND, color);
	}

	public String getSelectedText() {
		// TODO: Implement this method
		return  _hDoc.subSequence(getSelectionStart(), getSelectionEnd() - getSelectionStart()).toString();
	}

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        final int filteredMetaState = event.getMetaState() & ~KeyEvent.META_CTRL_MASK;
        if (KeyEvent.metaStateHasNoModifiers(filteredMetaState)) {
            switch (keyCode) {
				case KeyEvent.KEYCODE_A:
					selectAll();
					return true;
				case KeyEvent.KEYCODE_X:
					cut();
					return true;
				case KeyEvent.KEYCODE_C:
					copy();
					return true;
				case KeyEvent.KEYCODE_V:
					paste();
					return true;
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }
	


	@Override
	public void setWordWrap(boolean enable) {
		// TODO: Implement this method
		_isWordWrap = enable;
		super.setWordWrap(enable);
	}

	public DocumentProvider getText() {
		return createDocumentProvider();
	}

	public File getOpenedFile()
	{
		if(_lastSelectFile!=null)
			return  new File(_lastSelectFile);

		return  null;
	}

	public void setOpenedFile(String file)
	{
		_lastSelectFile=file;
	}
	public void insert(int idx, String text) {
		selectText(false);
		moveCaret(idx);
		paste(text);
	}

	public void setText(CharSequence c, boolean isRep) {
		replaceText(0, getLength() - 1, c.toString());
	}

	public void setText(CharSequence c) {
		Document doc=new Document(this);
		doc.setWordWrap(_isWordWrap);
		doc.setText(c);
		setDocumentProvider(new DocumentProvider(doc));
	}

	public void setSelection(int index) {
		selectText(false);
		if (!hasLayout())
			moveCaret(index);
		else
			_index = index;
	}


	public void undo() {
		DocumentProvider doc = createDocumentProvider();
		int newPosition = doc.undo();

		if (newPosition >= 0) {
			//TODO editor.setEdited(false); if reached original condition of file
			setEdited(true);

			respan();
			selectText(false);
			moveCaret(newPosition);
			invalidate();
		}

	}

	public void redo() {
		DocumentProvider doc = createDocumentProvider();
		int newPosition = doc.redo();

		if (newPosition >= 0) {
			setEdited(true);

			respan();
			selectText(false);
			moveCaret(newPosition);
			invalidate();
		}

	}

	private   Handler handler=new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

				case ReadThread.MSG_READ_OK:
					setText(msg.obj.toString());
					break;
				case ReadThread.MSG_READ_FAIL:
					showToast("打开失败");
					break;
				case WriteThread.MSG_WRITE_OK:
					showToast("保存成功");
					break;
				case WriteThread.MSG_WRITE_FAIL:
					showToast("保存失败");
					break;
			}
		}
	};

	public void open(String filename) {
		_lastSelectFile = filename;

		File inputFile = new File(filename);
		_inputtingDoc = new Document(this);
		_inputtingDoc.setWordWrap(this.isWordWrap());
		ReadThread readThread = new ReadThread(inputFile.getAbsolutePath(), handler);
		readThread.start();
	}

	/**
	 * 保存文件
	 * * @param file
     */
	public void save(String file) {
		WriteThread writeThread=new WriteThread(getText().toString(),file,handler);
		writeThread.start();
	}


	private Toast toast;
	private void showToast(CharSequence text)
	{
		if(toast==null)
		{
			toast=Toast.makeText(mContext,text,Toast.LENGTH_SHORT);
		}
		else
		{
			toast.setText(text);
		}
		toast.show();
	}
}
