package com.luoye.simpleC.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.luoye.simpleC.R;
import com.luoye.simpleC.entity.FileListItem;

import java.util.List;

/**
 * Created by zyw on 2017/2/15.
 */
public class FileListAdapter extends BaseAdapter {

    private  List<FileListItem> list;
    private  Context context;
    public FileListAdapter(Context context, List<FileListItem> list)
    {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FileListItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.file_list_item,null);
        ImageView imageView=(ImageView) view.findViewById(R.id.file_icon);
        TextView fileName=(TextView) view.findViewById(R.id.file_name_tv);
        TextView fileSize=(TextView) view.findViewById(R.id.file_size_tv);
        imageView.setImageBitmap(list.get(position).getIcon());
        fileName.setText(list.get(position).getName());
        fileSize.setText(list.get(position).getSize());
        return view;
    }

}
