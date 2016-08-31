package com.example.zhangsir.customaudiopalyer;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,View.OnTouchListener{
//    final String TAG="CustomAudioPlayer";
    final String POSITION="Position2Play";
    MediaPlayer mediaPlayer;
    ImageView theView;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int position=0;
    boolean bClicked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        setContentView(R.layout.activity_main);

        theView=(ImageView)findViewById(R.id.image_view);
        theView.setOnTouchListener(this);

        prefs=getPreferences(MODE_PRIVATE);//这种方式获取的SharedPreferences是以当前类名为默认前缀名称的
        editor=prefs.edit();

        position=prefs.getInt(POSITION,0);//获取存储的播放位置
        mediaPlayer=MediaPlayer.create(this,R.raw.laugh);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.seekTo(position);
        mediaPlayer.start();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //重新开始播放
        mediaPlayer.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(mediaPlayer.isPlaying()){
                    position=(int)(motionEvent.getX()*mediaPlayer.getDuration()/ theView.getWidth());
                    mediaPlayer.seekTo(position);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if(isDoubleClick()){
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    else{
                        mediaPlayer.start();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    private boolean isDoubleClick(){
        //检测之前是否单击过
       if(!bClicked){
            new Thread(){
                @Override
                public void run(){
                    bClicked=true;
                    try {
                        Thread.sleep(170);//170ms后即将记录的单击次数清零
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bClicked=false;
                }
            }.start();
           return false;
        }
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //保存当前播放位置
        position=mediaPlayer.getCurrentPosition();
        if(position>2000)
            position-=2000;
        else
            position=0;
        editor.putInt(POSITION,position);
        editor.commit();
        //如果没有下面的语句，那么停止该软件的时候就不会立即停止播放音乐
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
