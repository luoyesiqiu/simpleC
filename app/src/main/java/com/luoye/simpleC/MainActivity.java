package com.luoye.simpleC;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.os.*;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import com.luoye.simpleC.activity.FileListActivity;
import com.luoye.simpleC.util.ConstantPool;
import com.luoye.simpleC.util.ShellUtils;
import com.luoye.simpleC.util.Utils;
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
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		editor =new TextEditor(this);
		setContentView(editor);
		sharedPreferences=getSharedPreferences("setting",MODE_PRIVATE);
		init();
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
			case R.id.menu_open:

				startActivityForResult(new Intent(MainActivity.this, FileListActivity.class),0);

				break;
			case  R.id.menu_save:
				if(editor.getOpenedFile()!=null) {
					editor.save(editor.getOpenedFile().getAbsolutePath());
				}
				else
				{
					showToast("没有打开文件");
				}
				break;
			case R.id.menu_run:
					File file=editor.getOpenedFile();
					if(file!=null) {
						editor.save(editor.getOpenedFile().getAbsolutePath());//保存
						ShellUtils.CommandResult result = Utils.compile(getApplicationContext(), editor.getOpenedFile());
						String info = null;
						if (result.result == 0) {
							showToast("编译成功");
							Utils.execBin(MainActivity.this);

						} else {
							info = result.successMsg;

							AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
									.setTitle("编译失败")
									.setMessage(info)
									.setPositiveButton("确定", null)
									.create();
							alertDialog.show();
						}
					}
					else
					{
						editor.save(getFilesDir()+File.separator+"temp.c");//保存
						ShellUtils.CommandResult result = Utils.compile(getApplicationContext(),new File(getFilesDir()+File.separator+"temp.c"));
						String info = null;
						if (result.result == 0) {
							showToast("编译成功");
							Utils.execBin(MainActivity.this);

						} else {
							info = result.successMsg;

							AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
									.setTitle("编译失败")
									.setMessage(info)
									.setPositiveButton("确定", null)
									.create();
							alertDialog.show();
						}
					}
				break;
			case R.id.menu_save_as:
				final EditText editText=new EditText(MainActivity.this);
				AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton("保存", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								if (!TextUtils.isEmpty(editText.getText())) {
									File f = new File(ConstantPool.FILE_PATH);
									WriteThread writeThread=new WriteThread(editor.getText().toString(),f.getAbsolutePath() + File.separator + editText.getText(),handler);
									writeThread.start();
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
			case R.id.menu_close_file:
				editor.setOpenedFile(null);
				ActionBar actionBar=getActionBar();
				if (actionBar != null)
					actionBar.setSubtitle(null);
				editor.setText("");
				break;
			case R.id.menu_redo:
				editor.redo();
				break;

			case R.id.menu_undo:
				editor.undo();
				break;
		}
		return super.onOptionsItemSelected(item);
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
