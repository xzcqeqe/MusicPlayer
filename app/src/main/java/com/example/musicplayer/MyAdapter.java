package com.example.musicplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Song> list;

    //标记正在播放的歌
    private int position_flag = 0;
   //设计蓝色标记
    private int myBlue = Color.argb(0xff, 0x00, 0xBF, 0xFF);

    public MyAdapter(MainActivity mainActivity, List<Song> list) {
        this.context = mainActivity;
        this.list = list;
    }
    public void setFlag(int flag) {
        this.position_flag = flag;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            // 引入布局
            view = View.inflate(context, R.layout.list_item, null);
            // 实例化对象
            holder.song = (TextView) view.findViewById(R.id.item_mymusic_song);
            holder.singer = (TextView) view
                    .findViewById(R.id.item_mymusic_singer);
            holder.duration = (TextView) view
                    .findViewById(R.id.item_mymusic_duration);
            holder.position = (TextView) view
                    .findViewById(R.id.item_mymusic_postion);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 给控件赋值
        String string_song = list.get(i).getSong();
        if (string_song.length() >= 5
                && string_song.substring(string_song.length() - 4,
                string_song.length()).equals(".mp3")) {
            holder.song.setText(string_song.substring(0,
                    string_song.length() - 4).trim());
        } else {
            holder.song.setText(string_song.trim());
     }

        holder.position.setText(i + 1 + "");

        holder.singer.setText(list.get(i).getSinger().toString().trim());
        // 时间转换为时分秒
        int duration = list.get(i).getDuration();
        String time = MusicScan.formatTime(duration);
        holder.duration.setText(time);
        if(i==position_flag){
            holder.song.setTextColor(myBlue);
            holder.singer.setTextColor(myBlue);
            holder.duration.setTextColor(myBlue);

        }else {
            holder.song.setTextColor(Color.BLACK);
            holder.singer.setTextColor(Color.BLACK);
            holder.duration.setTextColor(Color.BLACK);

        }

        return view;
    }

    class ViewHolder {
        TextView song;// 歌曲名
        TextView singer;// 歌手
        TextView duration;// 时长
        TextView position;// 序号
    }




}
