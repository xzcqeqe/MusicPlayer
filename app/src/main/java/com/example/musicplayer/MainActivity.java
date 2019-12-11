package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mplayer = new MediaPlayer();
    ListView listview;
    List<Song> list;
    MyAdapter adapter;
    ImageView image_play,play_style,image_front,image_next,imageview;
    TextView t_name,t_singer;
    SeekBar seekBar;
    private int currentposition;
    private Random random = new Random();
    private int playStyle=0;
    // 判断seekbar是否正在滑动
    private boolean ischanging = false;
    private Thread thread;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        else {
            View layout_playbar = (View) findViewById(R.id.layout_playbar);
            imageview = (ImageView) layout_playbar.findViewById(R.id.imageview);
            image_play = (ImageView) layout_playbar
                    .findViewById(R.id.imageview_play);
            image_next = (ImageView) layout_playbar
                    .findViewById(R.id.imageview_next);
            image_front = (ImageView) layout_playbar
                    .findViewById(R.id.imageview_front);
            t_name = (TextView) layout_playbar.findViewById(R.id.name);
            t_singer = (TextView) layout_playbar.findViewById(R.id.singer);
            seekBar = (SeekBar) layout_playbar.findViewById(R.id.seekbar);

            //   设置list  并给listview设置监听
            setListView();

            //给底部按钮添加点击事件
            setClick();

            //当一首歌 播放完后执行的操作
            setMediaPlayerListener();
        }







    }



    //音乐播放
    private void musicplay(int position) {

        try {
            mplayer.reset();
            mplayer.setDataSource(list.get(position).getPath());
            mplayer.prepare();
            mplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        t_name.setText(list.get(currentposition).getSong());
        t_singer.setText(list.get(currentposition).getSinger());
        seekBar.setMax(list.get(position).getDuration());
        thread = new Thread(new SeekBarThread());
        thread.start();
    }
    // 随机播放下一曲
    private void random_nextMusic() {
        currentposition = currentposition + random.nextInt(list.size() - 1);
        currentposition %= list.size();
        musicplay(currentposition);
        adapter.setFlag(currentposition);
        adapter.notifyDataSetChanged();
        if (mplayer.isPlaying()) {

            image_play.setImageResource(R.mipmap.pause);
        }

    }
    // 下一曲
    private void nextMusic() {
        currentposition++;
        if (currentposition > list.size() - 1) {
            currentposition = 0;
        }
        musicplay(currentposition);
        adapter.setFlag(currentposition);
        adapter.notifyDataSetChanged();
        if (mplayer.isPlaying()) {
            image_play.setImageResource(R.mipmap.pause);
        }

    }

    // 上一曲
    private void frontMusic() {
        currentposition--;
        if (currentposition < 0) {
            currentposition = list.size() - 1;
        }
        musicplay(currentposition);
        adapter.setFlag(currentposition);
        adapter.notifyDataSetChanged();
        if (mplayer.isPlaying()) {
            image_play.setImageResource(R.mipmap.pause);
        }


    }
    // 自定义的线程,用于下方seekbar的刷新
    class SeekBarThread implements Runnable {

        @Override
        public void run() {

            while (!ischanging && mplayer.isPlaying()) {
                // 将SeekBar位置设置到当前播放位置
                seekBar.setProgress(mplayer.getCurrentPosition());

                try {
                    // 每500毫秒更新一次位置
                    Thread.sleep(500);
                    // 播放进度

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void setMediaPlayerListener() {
        // 监听mediaplayer播放完毕时调用
        mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                switch (playStyle) {
                    case 2:
                        musicplay(currentposition);
                        break;
                    case 0:
                        // 这里会引发初次进入时直接点击播放按钮时，播放的是下一首音乐的问题
                        nextMusic();
                        break;
                    case 1:
                        random_nextMusic();
                        break;
                    default:

                        break;
                }
            }
        });
        // 设置发生错误时调用
        mplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                mp.reset();
                // Toast.makeText(MainActivity.this, "未发现音乐", 1500).show();
                return false;
            }
        });
    }

      private  void setListView(){
          image_play =(ImageView)findViewById(R.id.imageview_play);
          list = MusicScan.getMusicData(MainActivity.this);
           adapter = new MyAdapter(MainActivity.this, list);
          listview = (ListView) findViewById(R.id.music);
          listview.setAdapter(adapter);
          adapter.setFlag(currentposition);
          adapter.notifyDataSetChanged();

//          adapter.notifyDataSetChanged();
          listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view,
                                      int position, long id) {
                  currentposition = position;
                  musicplay(currentposition);
                  image_play.setImageResource(R.mipmap.pause);
                  t_name.setText(list.get(currentposition).getSong());
                  t_singer.setText(list.get(currentposition).getSinger());
                  adapter.setFlag(currentposition);
                  adapter.notifyDataSetChanged();


              }
          });


      }

      //底部按钮栏 设置监听
      private  void setClick(){
          play_style =(ImageView)findViewById(R.id.imageview_order);
          image_play = (ImageView)findViewById(R.id.imageview_play);
          image_front=(ImageView)findViewById(R.id.imageview_front);
          image_next = (ImageView)findViewById(R.id.imageview_next);
          seekBar = (SeekBar) findViewById(R.id.seekbar);
            play_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playStyle++;
                    if(playStyle>2){playStyle=0;}
                    switch (playStyle){
                        case 0:
                            play_style.setImageResource(R.mipmap.order);
                            Toast.makeText(MainActivity.this, "顺序播放",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case 1 :
                            play_style.setImageResource(R.mipmap.random);
                            Toast.makeText(MainActivity.this, "随机播放",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case 2 :
                            play_style.setImageResource(R.mipmap.oneone);
                            Toast.makeText(MainActivity.this, "单曲循环",
                                    Toast.LENGTH_SHORT).show();
                            break;

                    }

                }
            });

                 //给播放按钮设置监听
           image_play.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if (mplayer.isPlaying()) {
                       mplayer.pause();
                       image_play.setImageResource(R.mipmap.play);
                   } else {
                       mplayer.start();
                       // thread = new Thread(new SeekBarThread());
                       // thread.start();
                      image_play.setImageResource(R.mipmap.pause);
                   }

               }
           });
                 //给下一首按钮设置监听
             image_next.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                      if(playStyle==1){
                          random_nextMusic();
                      }else {
                          nextMusic();
                      }
                 }
             });
                 //给上一首按钮设置监听
              image_front.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      if (playStyle==1){
                          random_nextMusic();
                      }else{
                          frontMusic();
                      }
                  }
              });
                 //给进度条设置监听
          seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

              @Override
              public void onStopTrackingTouch(SeekBar seekBar) {
                  // TODO Auto-generated method stub
                  ischanging =false;
                  mplayer.seekTo(seekBar.getProgress());
                  thread = new Thread(new SeekBarThread());
                  thread.start();
              }

              @Override
              public void onStartTrackingTouch(SeekBar seekBar) {
                  // TODO Auto-generated method stub
                   ischanging = true;
              }

              @Override
              public void onProgressChanged(SeekBar seekBar, int progress,
                                            boolean fromUser) {
                  // TODO Auto-generated method stub
                  // 可以用来写拖动的时候实时显示时间
              }
          });


      }


}
