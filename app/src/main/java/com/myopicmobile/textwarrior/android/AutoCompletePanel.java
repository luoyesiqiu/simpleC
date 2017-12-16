package com.myopicmobile.textwarrior.android;
import android.content.*;
import android.content.res.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.luoye.simpleC.R;
import com.myopicmobile.textwarrior.common.*;
import java.util.*;
import android.util.*;

public class AutoCompletePanel {

	private FreeScrollingTextField _textField;
	private Context _context;
	private static Language _globalLanguage = LanguageNonProg.getInstance();
	private ListPopupWindow _autoCompletePanel;
	private AutoPanelAdapter _adapter;
	private Filter _filter;
	private int _verticalOffset;
	private int _height;
	private int _horizontal;
	private CharSequence _constraint;
	private int _backgroundColor;
	private GradientDrawable gd;
	private int _textColor;
	private  boolean isShow=false;
	private  final  int PADDING=20;
	private  final  boolean DEBUG=false;
	public AutoCompletePanel(FreeScrollingTextField textField) {
		_textField = textField;
		_context = textField.getContext();
		initAutoCompletePanel();

	}

	public void setTextColor(int color){
		_textColor=color;
		gd.setStroke(1, color);
		_autoCompletePanel.setBackgroundDrawable(gd);
	}
	
	
	public void setBackgroundColor(int color){
		_backgroundColor=color;
		gd.setColor(color);
		_autoCompletePanel.setBackgroundDrawable(gd);
	}
	
	public void setBackground(Drawable color){
		_autoCompletePanel.setBackgroundDrawable(color);
	}

	@SuppressWarnings("ResourceType")
	private void initAutoCompletePanel() {
		_autoCompletePanel = new ListPopupWindow(_context);
		_autoCompletePanel.setAnchorView(_textField);
		_adapter = new AutoPanelAdapter(_context);
		_autoCompletePanel.setAdapter(_adapter);
		_filter = _adapter.getFilter();
		_autoCompletePanel.setContentWidth(ListPopupWindow.WRAP_CONTENT);
		//setHeight(300);

		TypedArray array = _context.getTheme().obtainStyledAttributes(new int[] {  
																		  android.R.attr.colorBackground, 
																		  android.R.attr.textColorPrimary, 
																	  }); 
		int backgroundColor = array.getColor(0, 0xFF00FF); 
		int textColor = array.getColor(1, 0xFF00FF); 
		array.recycle();
		gd=new GradientDrawable();
		gd.setColor(backgroundColor);
		gd.setCornerRadius(4);
		gd.setStroke(1, textColor);
		setTextColor(textColor);
		_autoCompletePanel.setBackgroundDrawable(gd);
		_autoCompletePanel.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
					// TODO: Implement this method
					select(p3);
				}
			});

	}
	public void selectFirst()
	{
		select(0);
	}

	public void select(int pos)
	{
		View view=_adapter.getView(pos,null,null);
		TextView textView=(TextView) view.findViewById(R.id.auto_panel_text);
		String text=textView.getText().toString();
		String commitText=null;
		boolean isFunc=text.contains("(");
		if(isFunc)
		{
			commitText=text.substring(0,text.indexOf('('))+"()";
		}
		else{
			commitText=text;
		}
		_textField.replaceText(_textField.getCaretPosition() - _constraint.length(), _constraint.length(), commitText);
		_adapter.abort();
		dismiss();
		if(isFunc) {
			_textField.moveCaretLeft();
		}
	}

	public void setWidth(int width) {
		// TODO: Implement this method
		_autoCompletePanel.setWidth(width);
	}

	private void setHeight(int height) {
		// TODO: Implement this method

		if (_height != height) {
			_height = height;
			_autoCompletePanel.setHeight(height);
		}
	}

	private void setHorizontalOffset(int horizontal) {
		// TODO: Implement this method
		horizontal = Math.min(horizontal, _textField.getWidth() / 2);
		if (_horizontal != horizontal) {
			_horizontal = horizontal;
			_autoCompletePanel.setHorizontalOffset(horizontal);
		}
	}


	private void setVerticalOffset(int verticalOffset) {
		// TODO: Implement this method
		//verticalOffset=Math.min(verticalOffset,_textField.getWidth()/2);
		int max=0 - _autoCompletePanel.getHeight();
		if (verticalOffset > max) {
			_textField.scrollBy(0, verticalOffset - max);
			verticalOffset = max;
		}
		if (_verticalOffset != verticalOffset) {
			_verticalOffset = verticalOffset;
			_autoCompletePanel.setVerticalOffset(verticalOffset);
		}
	}

	public void update(CharSequence constraint) {
		_adapter.restart();
		_filter.filter(constraint);
	}

	public void show() {
		if (!_autoCompletePanel.isShowing())
			_autoCompletePanel.show();
		_autoCompletePanel.getListView().setFadingEdgeLength(0);
		isShow=true;
	}

	public void dismiss() {
		if (_autoCompletePanel.isShowing()) {
			isShow=false;
			_autoCompletePanel.dismiss();
		}

	}
	synchronized public static void setLanguage(Language lang) {
		_globalLanguage = lang;
	}

	synchronized public static Language getLanguage() {
		return _globalLanguage;
	}

	public boolean isShow() {
		return  _autoCompletePanel.isShowing();
	}


	class ListItem{
		public ListItem(Bitmap bitmap, String text) {
			this.bitmap = bitmap;
			this.text = text;
		}

		private  Bitmap bitmap;

		public Bitmap getBitmap() {
			return bitmap;
		}

		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		private  String text;


	}
	/**
	 * Adapter定义
	 */
	class AutoPanelAdapter extends BaseAdapter implements Filterable {

		private int _h;
		private Flag _abort;
		private DisplayMetrics dm;
		private List<ListItem> listItems;
		private  Bitmap bitmap;
		public AutoPanelAdapter(Context context) {
			_abort = new Flag();
			listItems=new ArrayList<>();
			dm=context.getResources().getDisplayMetrics();
			bitmap= BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon_method);
		}

		public void abort() {
			_abort.set();
		}

		
		private int dp(float n) {
			// TODO: Implement this method
			return (int)TypedValue.applyDimension(1,n,dm);
		}

		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public ListItem getItem(int i) {
			return listItems.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			View tempView=null;
			if(view==null)
			{
				View rootView=LayoutInflater.from(_context).inflate(R.layout.auto_panel_item,null);
				tempView=rootView;
			}
			else {
				tempView=view;
			}
			TextView textView=(TextView)tempView.findViewById(R.id.auto_panel_text);
			ImageView imageView=(ImageView)tempView.findViewById(R.id.auto_panel_icon);
			String text=getItem(i).getText();
			SpannableString spannableString=null;
			ForegroundColorSpan foregroundColorSpan =null;
			log(text);
			if(text.contains("(")) {
				//函数
				spannableString=new SpannableString(text);
				foregroundColorSpan=new ForegroundColorSpan(Color.BLACK);
				spannableString.setSpan(foregroundColorSpan, 0,text.indexOf('('), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			else if(text.contains("[keyword]"))
			{
				//log("key:"+text);
				//关键字
				foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
				int idx=text.indexOf("[keyword]");
				text=text.substring(0,idx);
				spannableString=new SpannableString(text);
				spannableString.setSpan(foregroundColorSpan, 0,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			else
			{
				//其他
				spannableString=new SpannableString(text);
				foregroundColorSpan=new ForegroundColorSpan(Color.BLACK);
				spannableString.setSpan(foregroundColorSpan, 0,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			textView.setText(spannableString);
			imageView.setImageBitmap(getItem(i).getBitmap());
			return tempView;
		}

		public void restart() {
			// TODO: Implement this method
			_abort.clear();
		}

		/**
		 * 计算列表高
		 * @return
         */
		public int getItemHeight() {
			if (_h != 0)
				return _h;
			LayoutInflater inflater = LayoutInflater.from(_context);
			View rootView =  inflater.inflate(R.layout.auto_panel_item, null);
			rootView.measure(0, 0);
			_h = rootView.getMeasuredHeight();

			return _h;
		}
		/**
		 * 实现自动完成的过滤算法
		 */
		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				/**
				 * 本方法在后台线程执行，定义过滤算法
				 */
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					// 此处实现过滤
					// 过滤后利用FilterResults将过滤结果返回
					ArrayList<String> buf = new ArrayList<String>();
					String input = String.valueOf(constraint).toLowerCase();

					String[] keywords = _globalLanguage.getUserWord();
					for (String k:keywords) {
						if (k.toLowerCase().startsWith(input))
							buf.add(k);
					}
					keywords = _globalLanguage.getKeywords();
					for (String k:keywords) {
						if (k.indexOf(input) == 0)
							buf.add(k);
					}
					keywords = _globalLanguage.getNames();
					for (String k:keywords) {
						if (k.toLowerCase().startsWith(input))
							buf.add(k);
					}
					_constraint = input;
					FilterResults filterResults = new FilterResults();
					filterResults.values = buf;   // results是上面的过滤结果
					filterResults.count = buf.size();  // 结果数量
					return filterResults;
				}
				/**
				 * 本方法在UI线程执行，用于更新自动完成列表
				 */
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null && results.count > 0 && !_abort.isSet()) {
						// 有过滤结果，显示自动完成列表
						listItems.clear();   // 清空旧列表
						ArrayList<String> stringArrayList=(ArrayList<String>) results.values;
						for(int i=0;i<stringArrayList.size();i++)
						{
							String itemText=stringArrayList.get(i);
							if(itemText.contains("(")){
								listItems.add(new ListItem(bitmap,itemText));
							}
							else
							{
								listItems.add(new ListItem(null,itemText));
							}
						}
						int y = _textField.getCaretY() + _textField.rowHeight() / 2 - _textField.getScrollY();
						setHeight(getItemHeight() * Math.min(2, results.count));

						setHorizontalOffset(PADDING);
						setWidth(_textField.getWidth()-PADDING*2);
						setVerticalOffset(y - _textField.getHeight());//_textField.getCaretY()-_textField.getScrollY()-_textField.getHeight());
						notifyDataSetChanged();
						show();
					}
					else {
						// 无过滤结果，关闭列表
						notifyDataSetInvalidated();
					}
				}

			};
			return filter;
		}
	}

	private  void log(String log)
	{
		if(DEBUG)
		{
			System.out.println("-------------->AutoCompletePanel:"+log);
		}
	}
}
