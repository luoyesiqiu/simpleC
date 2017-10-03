package com.luoye.simpleC.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.luoye.simpleC.R;
import com.luoye.simpleC.adapter.FileListAdapter;
import com.luoye.simpleC.entity.FileListItem;
import com.luoye.simpleC.util.ConstantPool;
import com.luoye.simpleC.util.FileNameSort;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件列表
 * Created by zyw on 2017/2/15.
 */
public class FileListActivity extends Activity {

    private FileListAdapter adapter;
    private List<FileListItem> listItems;
    private ListView listView;
    private static File curpath = null;
    private Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list_layout);
        ActionBar actionBar=getActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        listItems = new ArrayList<>();
        adapter = new FileListAdapter(this, listItems);
        listView = (ListView) findViewById(R.id.file_list_view);
        listView.setDivider(getResources().getDrawable(android.R.drawable.divider_horizontal_dark));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClick());
        if (curpath == null)//为空才重新设置目录，这样可以打开上次打开的目录
        {
            curpath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "simpleC");
            if(!curpath.exists())
            {
                curpath.mkdirs();
            }
        }

        loadList(curpath);
        showToast("单击文件项选择");

    }

    /**
     * 列表单击事件
     */
    private class ItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileListItem fileListItem = listItems.get(position);
            if (position == 0 && fileListItem.getType() == FileListItem.UP_ITEM) {
                if(!curpath.getAbsolutePath().equals("/")) {
                    curpath = curpath.getParentFile();

                    loadList(curpath);
                }
            } else if (fileListItem.getType() == FileListItem.FOLDER_ITEM) {
                curpath = new File(curpath.getPath() + File.separator + fileListItem.getName());

                loadList(curpath);
            }
            else if(fileListItem.getType()==FileListItem.FILE_ITEM)
            {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("path", curpath.getAbsolutePath() + File.separator + listItems.get(position).getName());
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
    private void loadList(File path) {
        File[] files = path.listFiles();

        if (files == null)
            return;
        Arrays.sort(files, new FileNameSort());
        //不是根目录添加返回上级项
        clearList();
        if (!path.getAbsolutePath().equals("/")) {
            listItems.add(new FileListItem(BitmapFactory.decodeResource(getResources(), R.mipmap.folder), "..", "", FileListItem.UP_ITEM));
        }
        for (File f : files) {
            if (f.isDirectory())
                listItems.add(new FileListItem(BitmapFactory.decodeResource(getResources(), R.mipmap.folder), f.getName(), "", FileListItem.FOLDER_ITEM));
            else
                listItems.add(new FileListItem(BitmapFactory.decodeResource(getResources(), R.mipmap.file), f.getName(), formetFileSize(f.length()), FileListItem.FILE_ITEM));
        }

        adapter.notifyDataSetChanged();
        listView.setSelection(0);//滚动到第一项
        setTitle(getShortPath(curpath.getAbsolutePath()));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
