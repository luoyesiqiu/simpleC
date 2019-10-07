package com.luoye.simpleC;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.luoye.simpleC.activity.FileListActivity;
import com.luoye.simpleC.activity.HelpActivity;
import com.luoye.simpleC.activity.SettingActivity;
import com.luoye.simpleC.interfaces.CompileCallback;
import com.luoye.simpleC.interfaces.ExecCallback;
import com.luoye.simpleC.interfaces.UnzipCallback;
import com.luoye.simpleC.util.CFileNameFilter;
import com.luoye.simpleC.util.ConstantPool;
import com.luoye.simpleC.util.IO;
import com.luoye.simpleC.util.PermissionHelper;
import com.luoye.simpleC.util.Setting;
import com.luoye.simpleC.util.ShellUtils;
import com.luoye.simpleC.util.Utils;
import com.luoye.simpleC.view.SymbolView;
import com.luoye.simpleC.view.TextEditor;
import com.myopicmobile.textwarrior.android.RecentFiles;
import com.myopicmobile.textwarrior.common.ColorSchemeDark;
import com.myopicmobile.textwarrior.common.ReadThread;
import com.myopicmobile.textwarrior.common.WriteThread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = false;
    private TextEditor editor;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    //SharedPreference
    private static final String KEY_FIRST_RUN = "isFirstRun";
    private static final String KEY_APP_VERSION = "appVersion";
    //Bundle
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_FILE_CONTENT = "fileContent";
    private static final String KEY_IS_MULTI_COMPILE = "isMultiCompile";
    private static final String KEY_MULTI_COMPILE_FILES_NAME = "multiCompileFilesName";
    private final String GCC_VERSION = "7.2.0";
    private final int MSG_INIT = 0x100;
    private Setting settings;
    private SymbolView symbolView;

    private RecentFiles recentFiles;

    private boolean isMultiFileCompile = false;
    private String multiFilesName = "";
    private PermissionHelper mPermissionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        settings = Setting.getInstance(this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        editor = findViewById(R.id.main_editor);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        editor.requestFocus();
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

        if (savedInstanceState != null) {
            editor.requestFocus();

            String openedFilePath = savedInstanceState.getString(KEY_FILE_PATH, "");
            if (!openedFilePath.equals("")) {
                editor.open(openedFilePath);
                setSubtitle(new File(openedFilePath).getName());
            } else {
                //没有打开文件时
                editor.setText(savedInstanceState.getString(KEY_FILE_CONTENT, ""));
            }
            multiFilesName = savedInstanceState.getString(KEY_MULTI_COMPILE_FILES_NAME, "");
            isMultiFileCompile = savedInstanceState.getBoolean(KEY_IS_MULTI_COMPILE, false);
        }
        init();
    }

    private void setSubtitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(title);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editor.getOpenedFile() != null) {
            outState.putString(KEY_FILE_PATH, editor.getOpenedFile().getAbsolutePath());
        }
        if (!editor.getText().toString().equals("")) {
            outState.putString(KEY_FILE_CONTENT, editor.getText().toString());
        }
        outState.putBoolean(KEY_IS_MULTI_COMPILE, isMultiFileCompile);
        outState.putString(KEY_MULTI_COMPILE_FILES_NAME, multiFilesName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        settings.update();//加载设置
        editor.setDark(settings.isDarkMode());
        showSymbolView();
        editor.setAutoCompete(settings.isAutoCompete());
        editor.setShowLineNumbers(settings.isShowLineNumber());
        editor.invalidate();
    }

    private void showSymbolView() {
        if (settings.isShowSymbolView()) {
            symbolView.setVisible(true);
        } else {
            symbolView.setVisible(false);
        }
    }

    private void autoSave() {
        if (settings.isAutoSave() && editor.getOpenedFile() != null) {
            editor.save(editor.getOpenedFile().getAbsolutePath());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoSave();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void init() {
        if (sharedPreferences.getBoolean(KEY_FIRST_RUN, true)
                || !sharedPreferences.getString(KEY_APP_VERSION, "")
                .equals(Utils.getAppVersion(MainActivity.this))) {
            progressDialog = ProgressDialog.show(this, "", "初始化中，请等待...", false, false);
            new Thread() {
                @Override
                public void run() {
                    try {
                        InputStream inputStream = getAssets().open("gcc.zip");
                        //解压bin
                        Utils.unzip(inputStream, getFilesDir(), new UnzipCallback() {
                            @Override
                            public void onResult(boolean success) {
                                if (success) {
                                    File binDir1 = new File(getFilesDir() + File.separator + "gcc" + File.separator + "bin");
                                    File binDir2 = new File(getFilesDir() + File.separator + "gcc" + File.separator + "arm-linux-androideabi" + File.separator + "bin");
                                    File binDir3 = new File(getFilesDir() + File.separator + "gcc" + File.separator + "libexec/gcc/arm-linux-androideabi/" + GCC_VERSION);
                                    for (File f : binDir1.listFiles()) {
                                        if (f.isFile())
                                            Utils.changeToExecutable(f);
                                    }
                                    for (File f : binDir2.listFiles()) {
                                        if (f.isFile())
                                            Utils.changeToExecutable(f);
                                    }
                                    for (File f : binDir3.listFiles()) {
                                        if (f.isFile())
                                            Utils.changeToExecutable(f);
                                    }
                                    try {
                                        //解压lib
                                        InputStream libInputStream = getAssets().open("bin.zip");
                                        Utils.unzip(libInputStream, getFilesDir(), new UnzipCallback() {
                                            @Override
                                            public void onResult(boolean success) {
                                                if (success) {
                                                    File f = new File(getFilesDir() + "/indent");
                                                    Utils.changeToExecutable(f);
                                                    handler.sendMessage(Message.obtain(handler, MSG_INIT, true));
                                                } else {
                                                    handler.sendMessage(Message.obtain(handler, MSG_INIT, false));
                                                }
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    handler.sendMessage(Message.obtain(handler, MSG_INIT, false));
                                }
                            }
                        });//解压bin

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
        //加载头文件名
        ArrayList<String> cHeader = Utils.getCHeader(MainActivity.this);
        String[] arr1 = new String[cHeader.size()];
        editor.addNames(cHeader.toArray(arr1));

        ArrayList<String> cppHeader = Utils.getCppHeader(MainActivity.this);
        String[] arr2 = new String[cppHeader.size()];
        editor.addNames(cppHeader.toArray(arr2));

        View rootView = getWindow().getDecorView();
        symbolView = new SymbolView(MainActivity.this, rootView);
        symbolView.setOnSymbolViewClick(new SymbolView.OnSymbolViewClick() {
            @Override
            public void onClick(View view, String text) {
                if (text.equals(SymbolView.TAB_SYMBOL)) {
                    editor.insert(editor.getCaretPosition(), "  ");//两个空格
                } else {
                    editor.insert(editor.getCaretPosition(), text);
                }
            }
        });
        recentFiles = new RecentFiles(this);
        //外部打开文件
        Intent intent = getIntent();
        externalOpenFile(intent);
        mPermissionHelper = new PermissionHelper(this);
        if (Build.VERSION.SDK_INT >= 23 && !mPermissionHelper.isAllRequestedPermissionGranted()) {
            mPermissionHelper.applyPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 外部应用打开文件
     *
     * @param intent
     */
    private void externalOpenFile(Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        if (action.equals(Intent.ACTION_VIEW)) {
            String path = intent.getData().getPath();
            openFile(path);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        externalOpenFile(intent);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_INIT) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if (!(boolean) msg.obj) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("初始化失败，请打开软件重试")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                            .create()
                            .show();
                } else {
                    //写入
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_FIRST_RUN, false);
                    editor.putString(KEY_APP_VERSION, Utils.getAppVersion(MainActivity.this));
                    editor.apply();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ConstantPool.OK_SELECT_RESULT_CODE) {
            if (data != null) {
                String path = data.getStringExtra("path");
                openFile(path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: Implement this method
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Implement this method
        switch (item.getItemId()) {
            //打开文件
            case R.id.menu_open:
                selectFile();
                break;
            //保存
            case R.id.menu_save:
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
                launcherHelp();
                break;
            case R.id.menu_recent_file:
                recent();
                break;
            case R.id.menu_indent_code:
                indent();
                break;
            case R.id.menu_compile_option:
                compileOption();
                break;
            case R.id.menu_export_executable:
                exportExecutableFile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportExecutableFile(){
        Date nowDate =new Date();
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("MM-dd HH:mm:ss");
        File srcFile =new File(getFilesDir(),"temp");
        File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "simpleC", "executable_"+simpleDateFormat.format(nowDate)) ;

        try {
            if(srcFile.exists()) {
                Utils.copyFile(srcFile, outFile);
                showToast("导出成功，路径："+outFile.getAbsolutePath());
            }
            else{
                showToast("可执行文件不存在，请先运行！");
            }
        } catch (IOException e) {
            showToast("导出失败！");
            e.printStackTrace();
        }
    }
    private void compileOption() {
        View view = getLayoutInflater().inflate(R.layout.compile_option_layout, null);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.multi_file_check_box);
        final EditText editText = (EditText) view.findViewById(R.id.multi_file_edit);
        if (editor.getOpenedFile() == null) {
            isMultiFileCompile = false;
            multiFilesName = "";
            checkBox.setEnabled(false);
        }
        checkBox.setChecked(isMultiFileCompile);
        editText.setText(multiFilesName);

        editText.setEnabled(checkBox.isChecked());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                editText.setEnabled(check);
                if (check) {
                    isMultiFileCompile = check;
                    if (editor.getOpenedFile() != null) {
                        String temp = "";
                        File[] sameDirFiles = editor.getOpenedFile().getParentFile().listFiles(new CFileNameFilter());
                        for (File f : sameDirFiles) {
                            temp += (f.getName() + " ");
                        }
                        editText.setText(temp);
                    }
                } else {
                    File path = null;
                    if ((path = editor.getOpenedFile()) != null) {
                        multiFilesName = path.getName();
                        editText.setText(multiFilesName);
                    }
                }
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("编译选项")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        multiFilesName = editText.getText().toString();
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    /**
     * 缩进代码
     */
    private void indent() {
        if (editor.getText().toString().equals(""))
            return;
        final File tempIndent = new File(getFilesDir() + File.separator + ConstantPool.INDENT_FILE_NAME);

        //写出缓存文件
        WriteThread writeThread = new WriteThread(editor.getText().toString(), tempIndent.getAbsolutePath(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WriteThread.MSG_WRITE_OK) {
                    //执行缩进
                    Utils.execBin(MainActivity.this, new File(getFilesDir() + File.separator + "indent"), ConstantPool.INDENT_ARGS + " " + tempIndent.getAbsolutePath(), new ExecCallback() {
                        @Override
                        public void onResult(ShellUtils.CommandResult result) {
                            ReadThread readThread = new ReadThread(tempIndent.getAbsolutePath(), new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == ReadThread.MSG_READ_OK) {
                                        editor.replaceAll(msg.obj.toString());
                                    }
                                }
                            });
                            readThread.start();
                        }
                    });
                }
            }
        });
        writeThread.start();

    }

    /**
     * 打开一个文本文件
     *
     * @param path
     */
    private void openFile(String path) {
        //这里很重要！文件不要重复打开！！！
        if (editor.getOpenedFile() != null) {
            if (!path.equals(editor.getOpenedFile().getAbsolutePath()))
                autoSave();
        }
        editor.open(path);
        recentFiles.addRecentFile(path);
        recentFiles.save();
        setSubtitle(editor.getOpenedFile().getName());

        multiFilesName = new File(path).getName();
        isMultiFileCompile = false;
    }

    /**
     * 最近打开文件
     */
    private void recent() {
        final List<String> recentFilesList = recentFiles.getRecentFiles();

        final String[] recentFilesArray = new String[recentFilesList.size()];
        Iterator<String> iterator = recentFilesList.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            String temp = iterator.next();
            recentFilesArray[i] = new File(temp).getName();
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
    private void launcherHelp() {
        Intent intent = new Intent();
        intent.putExtra("title", "帮助");
        intent.putExtra("data", IO.getFromAssets(this, "help.md"));
        intent.setClass(MainActivity.this, HelpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void preferences() {
        startActivity(new Intent(MainActivity.this, SettingActivity.class));
    }

    private void selectFile() {
        startActivityForResult(new Intent(MainActivity.this, FileListActivity.class), 0);
    }

    /**
     * 关闭文件
     */
    private void closeFile() {
        autoSave();
        editor.setOpenedFile(null);
        setSubtitle(null);
        editor.setText("");
    }

    /**
     * 保存
     */
    private void save() {
        if (editor.getOpenedFile() != null) {
            editor.save(editor.getOpenedFile().getAbsolutePath());
        } else {
            saveAs();
        }
    }

    /**
     * 另存为
     */
    private void saveAs() {
        final EditText editText = new EditText(MainActivity.this);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(editText.getText())) {
                            File f = new File(ConstantPool.FILE_PATH);

                            File newFile = new File(f.getAbsolutePath() + File.separator + editText.getText());
                            //没有设置后缀名,自动设置
                            if (!newFile.getAbsolutePath().contains(".")) {
                                if (settings.isGccCompile()) {
                                    newFile = new File(f.getAbsolutePath() + File.separator + editText.getText() + ".c");
                                } else {
                                    newFile = new File(f.getAbsolutePath() + File.separator + editText.getText() + ".cpp");
                                }
                            }
                            editor.save(newFile.getAbsolutePath());
                            editor.setOpenedFile(newFile.getAbsolutePath());
                            setSubtitle(newFile.getName());
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

    /**
     * 选择编译器编译
     *
     * @param context
     * @param files
     * @param compileCallback
     * @param gccCompile      是否gcc编译
     */
    private void compile(Context context, File[] files, CompileCallback compileCallback, boolean gccCompile) {
        if (gccCompile) {
            Utils.gccCompile(context, files, compileCallback);
        } else {
            Utils.gplusplusCompile(context, files, compileCallback);
        }
    }

    /**
     * compile code
     * @param files
     */
    private void compile(final File[] files) {
        final boolean gccCompile = settings.isGccCompile();
        final ProgressDialog compileDialog = ProgressDialog.show(this, "", "正在编译...", false, false);
        compileDialog.show();
        //thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //headler
                compile(getApplicationContext(), files, new CompileCallback() {
                    public void onCompileResult(final ShellUtils.CommandResult result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String info = null;
                                if (result.result == 0) {
                                    if (compileDialog != null)
                                        compileDialog.dismiss();
                                    Utils.execBin(MainActivity.this);

                                } else {
                                    if (compileDialog != null)
                                        compileDialog.dismiss();
                                    info = result.getMsg();
                                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.compile_error_layout, null);
                                    TextView infoTextView = (TextView) view.findViewById(R.id.console_msg);
                                    infoTextView.setText(info);
                                    infoTextView.setTextColor(Color.BLACK);
                                    ScrollView scrollView = new ScrollView(MainActivity.this);
                                    scrollView.addView(view, LinearLayout.LayoutParams.MATCH_PARENT);
                                    Matcher matcher = Pattern.compile("(\\d+):\\d+:\\s+error:").matcher(info);
                                    final String[] pos = new String[1];
                                    if (matcher.find())
                                        pos[0] = matcher.group(1);
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("编译失败")
                                            .setView(scrollView)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (pos[0] != null)
                                                        editor.gotoLine(Integer.parseInt(pos[0]));

                                                }
                                            })
                                            .create();
                                    alertDialog.show();
                                }
                            }
                        });
                    }

                }, gccCompile);
            }
        }).start();

    }

    /**
     * run
     */
    File[] compileFiles = null;
    private void run() {
        File openFile = null;
        //判断是否打开了文件,编译前!!一定要!!保存
        if (editor.getOpenedFile() != null) {
            //判断是否多文件编译
            openFile = editor.getOpenedFile();
            if (!isMultiFileCompile) {
                compileFiles = new File[1];
                compileFiles[0] = editor.getOpenedFile();
            } else {
                String[] fileNames = multiFilesName.split(" ");
                compileFiles = new File[fileNames.length];
                for (int i = 0; i < fileNames.length; i++) {
                    compileFiles[i] = new File(editor.getOpenedFile().getParent() + File.separator + fileNames[i]);
                }
            }
        } else {
            compileFiles = new File[1];
            openFile = compileFiles[0] = new File(getFilesDir() + File.separator + ConstantPool.TEMP_FILE_NAME);

        }

        WriteThread writeThread = new WriteThread(editor.getText().toString(), openFile.getAbsolutePath(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WriteThread.MSG_WRITE_OK) {
                    compile(compileFiles);
                }
            }
        });
        writeThread.start();

    }

    private void log(String text) {
        if (DEBUG) {
            Log.i(TAG, text);
        }
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
}
