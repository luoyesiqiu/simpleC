package com.luoye.simpleC.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.luoye.simpleC.R;
import com.luoye.simpleC.adapter.FileListAdapter;
import com.luoye.simpleC.entity.FileListItem;
import com.luoye.simpleC.util.ConstantPool;
import com.luoye.simpleC.util.FileNameSort;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件列表
 * Created by zyw on 2017/2/15.
 */
public class FileListActivity extends Activity {

    private Bitmap folderIcon;
    private Bitmap fileIcon;
    private FileListAdapter adapter;
    private List<FileListItem> listItems;
    private ListView listView;
    private static File curPath = null;
    private Toast toast;
    private  final File HOME_PATH=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "simpleC");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list_layout);
        ActionBar actionBar=getActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if(fileIcon==null)
        {
            fileIcon=BitmapFactory.decodeResource(getResources(), R.mipmap.file);
        }
        if(folderIcon==null)
        {
            folderIcon=BitmapFactory.decodeResource(getResources(), R.mipmap.folder);
        }
        listItems = new ArrayList<>();
        adapter = new FileListAdapter(this, listItems);
        listView = (ListView) findViewById(R.id.file_list_view);
        listView.setDivider(getResources().getDrawable(android.R.drawable.divider_horizontal_dark));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClick());
        registerForContextMenu(listView);
        if (curPath == null)//为空才重新设置目录，这样可以打开上次打开的目录
        {
            curPath = HOME_PATH;
            if(!curPath.exists())
            {
                curPath.mkdirs();
            }
        }

        loadList(curPath);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        File f=new File(curPath+File.separator+adapter.getItem(menuInfo.position).getName());
        switch (item.getItemId())
        {
            case 0:
                rename(f,menuInfo.position);
                break;
            case 1:
                deleteFile(f);
                listItems.remove(menuInfo.position);
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }
    /**
     * 深度优先遍历
     * 删除文件夹下所有文件和文件夹
     * @param file
     */
    public static void deleteFile(File file)
    {
        if(file.isFile()) {
            file.delete();
            return;
        }
        File[] fs=file.listFiles();

        for (File f:fs)
        {
            if (f.isFile())
            {
                f.delete();
            }
            else{
                deleteFile(f);
                f.delete();
            }

        }
        file.delete();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(((AdapterView.AdapterContextMenuInfo)menuInfo).position==0)
        {
            //第一项不产生菜单
            return ;
        }
        //第2个参数是id
        menu.add(0,0,0,"重命名");
        menu.add(0,1,1,"删除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    /*
    * 重命名
    */
    private void rename(final File oldName, final int idx)
    {
        final EditText editText=new EditText(this);
        editText.setText(oldName.getName());
        AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setTitle("重命名")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text=editText.getText().toString();
                        if(!text.equals(""))
                        {
                            oldName.renameTo(new File(oldName.getParent()+File.separator+text));
                            FileListItem fileListItem=listItems.get(idx);
                            fileListItem.setName(text.toString());
                            listItems.set(idx,fileListItem);
                            adapter.notifyDataSetChanged();
                        }else
                        {
                            showToast("重命名失败");
                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    /**
     * 列表单击事件
     */
    private class ItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileListItem fileListItem = listItems.get(position);
            if (position == 0 && fileListItem.getType() == FileListItem.UP_ITEM) {
                if(!curPath.getAbsolutePath().equals("/")) {
                    curPath = curPath.getParentFile();

                    loadList(curPath);
                }
            } else if (fileListItem.getType() == FileListItem.FOLDER_ITEM) {
                curPath = new File(curPath.getPath() + File.separator + fileListItem.getName());

                loadList(curPath);
            }
            else if(fileListItem.getType()==FileListItem.FILE_ITEM)
            {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("path", curPath.getAbsolutePath() + File.separator + listItems.get(position).getName());
                setResult(ConstantPool.OK_SELECT_RESULT_CODE, resultIntent);//选择结果回馈
                finish();
            }
        }
    }

    /**
     * 清空列表
     */
    private void clearList() {
        if (listItems != null)
            listItems.clear();
    }

    /**
     * 加载列表
     *
     * @param path
     */
    private  void loadList(File path) {
        File[] files = path.listFiles();

        if (files == null)
            return;
        Arrays.sort(files, new FileNameSort());
        //不是根目录添加返回上级项
        clearList();
        if (!path.getAbsolutePath().equals("/")) {
            listItems.add(new FileListItem(folderIcon, "..", "", FileListItem.UP_ITEM));
        }
        for (File f : files) {
            if (f.isDirectory())
                listItems.add(new FileListItem(folderIcon, f.getName(), "", FileListItem.FOLDER_ITEM));
            else
                listItems.add(new FileListItem(fileIcon, f.getName(), formetFileSize(f.length()), FileListItem.FILE_ITEM));
        }

        adapter.notifyDataSetChanged();
        listView.setSelection(0);//滚动到第一项
        setTitle(getShortPath(path.getAbsolutePath()));
    }

    /**
     * 格式化文件大小
     *
     * @param fileS
     * @return
     */
    public static String formetFileSize(long fileS) {
        //转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "b";
            if (fileS == 0)
                fileSizeString = fileS + ".0b";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "Kb";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "Mb";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "Gb";
        }
        return fileSizeString;
    }


    /**
     * 获取缩略路径
     *
     * @param path
     * @return
     */
    public static String getShortPath(String path) {
        int n = 0, p = 0;
        for (int i = 0; i < path.length(); i++)
            if (path.charAt(i) == '/')
                ++n;

        if (n >= 3) {
            for (int i = 0; i < path.length(); i++) {
                if (path.charAt(i) == '/')
                    ++p;
                if (p == n - 1) {
                    String newPath = "..." + path.substring(i, path.length());
                    return newPath;
                }
            }
        }

        return path;
    }

    /**
     * 显示吐死
     *
     * @param text
     */
    private void showToast(CharSequence text) {
        if (toast == null)
            toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        else
            toast.setText(text);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        else if (id==R.id.menu_create_folder)
        {
            final EditText editText=new EditText(FileListActivity.this);
            AlertDialog alertDialog=new AlertDialog.Builder(FileListActivity.this)
                    .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!TextUtils.isEmpty(editText.getText())) {
                                File folder = new File(curPath + File.separator + editText.getText());

                                boolean ok = folder.mkdir();
                                if (ok){
                                    listItems.add(1, new FileListItem(folderIcon, editText.getText().toString(), "", FileListItem.FOLDER_ITEM));
                                    adapter.notifyDataSetChanged();
                                    listView.setSelection(0);//滚动到第一项
                                }else
                                {
                                    showToast("文件夹创建失败");
                                }
                            }
                            else {
                                showToast("请输入文件夹名");
                            }
                        }
                    })
                    .setTitle("输入文件夹名")
                    .setView(editText)
                    .create();
            alertDialog.show();
        }
        else if (id==R.id.menu_create_file)
        {
            final EditText editText=new EditText(FileListActivity.this);
            AlertDialog alertDialog=new AlertDialog.Builder(FileListActivity.this)
                    .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!TextUtils.isEmpty(editText.getText())) {
                                File file = new File(curPath + File.separator + editText.getText());
                                FileOutputStream fileOutputStream=null;
                                boolean ok = false;
                                try {
                                    fileOutputStream=new FileOutputStream(file);
                                    ok=true;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }finally {
                                    if(fileOutputStream!=null)
                                        try {
                                            fileOutputStream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                }
                                if (ok){
                                    listItems.add(1, new FileListItem(fileIcon, editText.getText().toString(), "0.0b", FileListItem.FILE_ITEM));
                                    adapter.notifyDataSetChanged();
                                    listView.setSelection(0);//滚动到第一项
                                }else
                                {
                                    showToast("文件创建失败");
                                }
                            }
                            else {
                                showToast("请输入文件名");
                            }
                        }
                    })
                    .setTitle("输入文件名")
                    .setView(editText)
                    .create();
            alertDialog.show();
        }
        else if(id==R.id.menu_open_workspace)
        {
            curPath=HOME_PATH;
            loadList(curPath);
        }
        return super.onOptionsItemSelected(item);
    }
}
