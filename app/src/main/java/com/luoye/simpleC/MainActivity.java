package com.luoye.simpleC;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.*;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import com.luoye.simpleC.activity.FileListActivity;
import com.luoye.simpleC.activity.HelpActivity;
import com.luoye.simpleC.activity.SettingActivity;
import com.luoye.simpleC.interfaces.CompileCallback;
import com.luoye.simpleC.util.ConstantPool;
import com.luoye.simpleC.util.ShellUtils;
import com.luoye.simpleC.util.Utils;
import com.luoye.simpleC.view.SymbolView;
import com.luoye.simpleC.view.TextEditor;
import com.myopicmobile.textwarrior.android.RecentFiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity
{
	private TextEditor editor;
	private SharedPreferences sharedPreferences;
	private ProgressDialog progressDialog;
	private  static  final String KEY_FIRST_RUN="isFirstRun";
	private ArrayList<String> header;
	private SharedPreferences settingPreference;
	private boolean darkMode=false;
	private boolean autoSave=true;
	private boolean showSymbolView=false;
	private  SymbolView symbolView;
	private final String PROBLEMS_URL="https://github.com/luoyesiqiu/C--Problems/blob/master/Problems.md";
	private RecentFiles recentFiles;
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		editor =new TextEditor(this);
		setContentView(editor);
		settingPreference= PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences=getSharedPreferences("setting",MODE_PRIVATE);
		init();

	}

	@Override
	protected void onResume() {
		super.onResume();
		readPreferences();
		editor.setDark(darkMode);
		showSymbolView();
	}

	private void showSymbolView()
	{
		if(showSymbolView){
			symbolView.setVisible(true);
		}
		else
		{
			symbolView.setVisible(false);
		}
	}

	private void autoSave()
	{
		if(autoSave&&editor.getOpenedFile()!=null) {
			editor.save(editor.getOpenedFile().getAbsolutePath());
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		autoSave();
	}

	private  void init()
	{
		if(sharedPreferences.getBoolean(KEY_FIRST_RUN,true)) {
			progressDialog=ProgressDialog.show(this,"","初始化中，请等待...",false,false);
			new Thread() {
				@Override
				public void run() {
					try {
						InputStream inputStream=getAssets().open("bin.zip");
						boolean step1=Utils.unzip(inputStream,getFilesDir());//解压bin
						inputStream=getAssets().open("lib.zip");
						boolean step2=Utils.unzip(inputStream,getFilesDir());//解压lib
						Utils.changeToExecutable(new File(getFilesDir()+ File.separator+"tcc"));
						//Utils.writeFile(MainActivity.this,"make",getFilesDir(),"make");
						//Utils.changeToExecutable(new File(getFilesDir()+File.separator+"make"));
						boolean success=step1&&step2;
						handler.sendMessage(Message.obtain(handler,0,success));
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}.start();
		}
		header=Utils.getHeader(MainActivity.this);
		String[] arr=new String[header.size()];
		editor.addNames(header.toArray(arr));
		View rootView=getWindow().getDecorView();
		symbolView=new SymbolView(MainActivity.this,rootView);
		symbolView.setOnSymbolViewClick(new SymbolView.OnSymbolViewClick() {
			@Override
			public void onClick(View view, String text) {
				editor.paste(text);
			}
		});
		recentFiles=new RecentFiles(this);
	}


	private  Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0)
			{
				progressDialog.dismiss();
				if(!(boolean)msg.obj)
				{
					new AlertDialog.Builder(MainActivity.this)
							.setMessage("初始化失败，请打开软件重试")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									finish();
								}
							})
							.create()
							.show();
				}else{
					//写入
					SharedPreferences.Editor editor=sharedPreferences.edit();
					editor.putBoolean(KEY_FIRST_RUN,false);
					editor.apply();
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode== ConstantPool.OK_SELECT_RESULT_CODE) {
			if(data!=null) {
				String path = data.getStringExtra("path");
				openFile(path);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		switch (item.getItemId())
		{
			//打开文件
			case R.id.menu_open:
				selectFile();
				break;
			//保存
			case  R.id.menu_save:
				save();
				break;
			//运行
			case R.id.menu_run:
				run();
				break;
			//另存为
			case R.id.menu_save_as:
				saveAs();
				break;
			//关闭文件
			case R.id.menu_close_file:
				closeFile();
				break;
			//重做
			case R.id.menu_redo:
				editor.redo();
				break;
			//撤销
			case R.id.menu_undo:
				editor.undo();
				break;
			//设置
			case R.id.menu_setting:
				preferences();
				break;
			case R.id.menu_learn:
				exercise();
				break;
			case R.id.menu_recent_file:
				recent();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private  void openFile(String path)
	{
		autoSave();
		editor.open(path);
		recentFiles.addRecentFile(path);
		recentFiles.save();
		ActionBar actionBar = getActionBar();
		if (actionBar != null)
			actionBar.setSubtitle(editor.getOpenedFile().getName());
	}

	private  void recent(){
		final List<String> recentFilesList=recentFiles.getRecentFiles();

		final String[] recentFilesArray=new String[recentFilesList.size()];
		Iterator<String> iterator=recentFilesList.iterator();
		for(int i=0;iterator.hasNext();i++){
			String temp=iterator.next();
			recentFilesArray[i]=new File(temp).getName();
		}
		new AlertDialog.Builder(this).setTitle("最近打开文件")
				.setItems(recentFilesArray, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int idx) {
						openFile(recentFilesList.get(idx));
					}
				})
				.create()
				.show();

	}
	private void exercise()
	{
		Intent intent=new Intent(MainActivity.this, HelpActivity.class);
		intent.putExtra("title","练习");
		intent.putExtra("data",PROBLEMS_URL);
		startActivity(intent);
	}
	private void preferences()
	{
		startActivity(new Intent(MainActivity.this, SettingActivity.class));
	}
	private  void selectFile()
	{
		startActivityForResult(new Intent(MainActivity.this, FileListActivity.class),0);
	}
	private void closeFile()
	{
		autoSave();
		editor.setOpenedFile(null);
		ActionBar actionBar=getActionBar();
		if (actionBar != null)
			actionBar.setSubtitle(null);
		editor.setText("");
	}
	private  void save()
	{
		if(editor.getOpenedFile()!=null) {
			editor.save(editor.getOpenedFile().getAbsolutePath());
		}
		else
		{
			saveAs();
		}
	}
	private  void saveAs()
	{
		final EditText editText=new EditText(MainActivity.this);
		AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
				.setPositiveButton("保存", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (!TextUtils.isEmpty(editText.getText())) {
							File f = new File(ConstantPool.FILE_PATH);
							File newFile=new File(f.getAbsolutePath() + File.separator + editText.getText());
							editor.save(newFile.getAbsolutePath());
							editor.setOpenedFile(newFile.getAbsolutePath());
							ActionBar actionBar=getActionBar();
							if(actionBar!=null)
							{
								actionBar.setSubtitle(newFile.getName());
							}
						} else {
							showToast("请输入文件名");
						}
					}
				})
				.setTitle("输入文件名")
				.setView(editText)
				.create();
		alertDialog.show();
	}
	private void run()
	{
		autoSave();//本地保存一次
		File[] files=new File[1];
		//判断是否打开了文件
		if(editor.getOpenedFile()!=null) {
			files[0] = editor.getOpenedFile();
		}else
		{
			editor.save(getFilesDir()+File.separator+"temp.c");//保存在缓存
			files[0] = new File(getFilesDir()+File.separator+"temp.c");

		}
		 Utils.compile(getApplicationContext(), files, new CompileCallback() {
			@Override
			public void onCompileResult(ShellUtils.CommandResult result) {
				String info = null;
				if (result.result == 0) {
					showToast("编译成功");
					Utils.execBin(MainActivity.this);

				} else {
					info = result.successMsg;
					View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.compile_error_layout,null);
					TextView infoTextView=(TextView)view.findViewById(R.id.console_msg);
					infoTextView.setText(info);
					infoTextView.setTextColor(Color.BLACK);
					Matcher matcher=Pattern.compile(":(\\d+):").matcher(info);
					final String[] pos=new String[1];
					if(matcher.find())
						pos[0] = matcher.group(1);
					AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
							.setTitle("编译失败")
							.setView(view)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									if(pos[0]!=null)
										editor.gotoLine(Integer.parseInt(pos[0]));

								}
							})
							.create();
					alertDialog.show();
				}
			}
		});

	}


	private  void readPreferences()
	{
		darkMode=settingPreference.getBoolean("editor_dark_mode",false);
		autoSave=settingPreference.getBoolean("editor_auto_save",true);
		showSymbolView=settingPreference.getBoolean("show_symbol_view",false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		getMenuInflater().inflate(R.menu.main_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	private Toast toast;
	private void showToast(CharSequence text)
	{
		if(toast==null)
		{
			toast=Toast.makeText(this,text,Toast.LENGTH_SHORT);
		}
		else
		{
			toast.setText(text);
		}
		toast.show();
	}
}
