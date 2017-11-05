package com.luoye.simpleC;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.os.*;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import com.luoye.simpleC.activity.FileListActivity;
import com.luoye.simpleC.activity.SettingActivity;
import com.luoye.simpleC.util.ConstantPool;
import com.luoye.simpleC.util.ShellUtils;
import com.luoye.simpleC.util.Utils;
import com.luoye.simpleC.view.SymbolView;
import com.luoye.simpleC.view.TextEditor;
import com.myopicmobile.textwarrior.common.ReadThread;
import com.myopicmobile.textwarrior.common.WriteThread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jackpal.term.RunScript;
import jackpal.term.RunShortcut;
import jackpal.term.Term;

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
		SymbolView symbolView=new SymbolView(MainActivity.this,rootView);
		symbolView.setOnSymbolViewClick(new SymbolView.OnSymbolViewClick() {
			@Override
			public void onClick(View view, String text) {
				editor.paste(text);
			}
		});
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
				autoSave();
				String path = data.getStringExtra("path");
				editor.open(path);
				ActionBar actionBar = getActionBar();
				if (actionBar != null)
					actionBar.setSubtitle(editor.getOpenedFile().getName());
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

				startActivityForResult(new Intent(MainActivity.this, FileListActivity.class),0);

				break;
			//保存
			case  R.id.menu_save:
				if(editor.getOpenedFile()!=null) {
					editor.save(editor.getOpenedFile().getAbsolutePath());
				}
				else
				{
					showToast("没有打开文件");
				}
				break;
			//运行
			case R.id.menu_run:
				autoSave();//本地保存一次
				editor.save(getFilesDir()+File.separator+"temp.c");//保存在缓存一次
				ShellUtils.CommandResult result = Utils.compile(getApplicationContext(),new File(getFilesDir()+File.separator+"temp.c"));
				String info = null;
				if (result.result == 0) {
					showToast("编译成功");
					Utils.execBin(MainActivity.this);

				} else {
					info = result.successMsg;
					View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.console,null);
					TextView infoTextView=(TextView)view.findViewById(R.id.console_msg);
					infoTextView.setText(info);
					AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
							.setTitle("编译失败")
							.setView(view)

							.setPositiveButton("确定", null)
							.create();
					alertDialog.show();
				}
				break;
			//另存为
			case R.id.menu_save_as:
				final EditText editText=new EditText(MainActivity.this);
				AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton("保存", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								if (!TextUtils.isEmpty(editText.getText())) {
									File f = new File(ConstantPool.FILE_PATH);
									editor.save(f.getAbsolutePath() + File.separator + editText.getText());
								} else {
									showToast("请输入文件名");
								}
							}
						})
						.setTitle("输入文件名")
						.setView(editText)
						.create();
				alertDialog.show();
				break;
			//关闭文件
			case R.id.menu_close_file:
				autoSave();
				editor.setOpenedFile(null);
				ActionBar actionBar=getActionBar();
				if (actionBar != null)
					actionBar.setSubtitle(null);
				editor.setText("");

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
				startActivity(new Intent(MainActivity.this, SettingActivity.class));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private  void readPreferences()
	{
		darkMode=settingPreference.getBoolean("editor_dark_mode",false);
		autoSave=settingPreference.getBoolean("editor_auto_save",true);
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
