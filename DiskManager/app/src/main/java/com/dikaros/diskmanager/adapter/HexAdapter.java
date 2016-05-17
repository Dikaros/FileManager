package com.dikaros.diskmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dikaros.diskmanager.R;

import java.util.List;

/**
 * Created by mac on 16/5/8.
 */
public class HexAdapter extends BaseAdapter {

    public HexAdapter(Context context, List<String[]> list) {
        this.context = context;
        this.list = list;
    }

    Context context;
    List<String[]> list;


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] data = list.get(position);
        ViewHolder holder = null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.file_hex_cell,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //第一行是地址
        holder.textViews[0].setText(data[0]);

        //数据行
        for (int i=1;i<data.length-1;i++){
            holder.textViews[i].setText(data[i]);
        }
        //最后一行是文本
        holder.textViews[9].setText(data[data.length-1]);
        return convertView;
    }

    public class ViewHolder {
        TextView[] textViews;
        int[] ids = {R.id.tv_hex_address, R.id.tv_hex_data_1, R.id.tv_hex_data_2, R.id.tv_hex_data_3, R.id.tv_hex_data_4, R.id.tv_hex_data_5, R.id.tv_hex_data_6, R.id.tv_hex_data_7, R.id.tv_hex_data_8, R.id.tv_hex_content};

        public ViewHolder(View v) {
            textViews = new TextView[10];
            for (int i=0;i<ids.length;i++){
                textViews[i] = (TextView) v.findViewById(ids[i]);
            }
        }
    }
}
