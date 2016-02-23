package com.basti.greendaotest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.basti.greendao.Note;

import java.util.List;

/**
 * Created by SHIBW-PC on 2016/2/23.
 */
public class NoteAdapter extends BaseAdapter{

    private List<Note> list;
    private Context context;

    public NoteAdapter(List<Note> list, Context context) {
        this.list = list;
        this.context = context;
    }

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
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(list.get(position).getText());
        viewHolder.comment.setText(list.get(position).getComment());

        return convertView;
    }

    class ViewHolder{
        TextView text;
        TextView comment;
    }
}
