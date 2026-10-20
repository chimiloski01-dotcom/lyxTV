package com.weblyxx.lixtv;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.media3.common.*;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class PlayerActivity extends Activity {
 private ExoPlayer player;
 private final Handler handler=new Handler(Looper.getMainLooper());
 private TextView notice;
 private long previousPosition=-1;
 private int staleChecks=0,attempts=0;
 private String url;
 private boolean stopped=false;
 private final Runnable health=new Runnable(){
  @Override public void run(){
   if(player==null||stopped)return;
   int state=player.getPlaybackState();
   long pos=player.getCurrentPosition();
   if(state==Player.STATE_READY&&player.isPlaying()){
    if(previousPosition>=0&&Math.abs(pos-previousPosition)<700)staleChecks++;
    else staleChecks=0;
    if(staleChecks>=5){staleChecks=0;recover();}
   }
   previousPosition=pos;
   handler.postDelayed(this,4000);
  }
 };
 @Override public void onCreate(Bundle saved){super.onCreate(saved);
  url=getIntent().getStringExtra("url");
  if(url==null||!(url.startsWith("http://")||url.startsWith("https://"))){Toast.makeText(this,"Fonte inválida",Toast.LENGTH_LONG).show();finish();return;}
  android.widget.FrameLayout root=new android.widget.FrameLayout(this);
  PlayerView view=new PlayerView(this);view.setUseController(true);root.addView(view);
  notice=new TextView(this);notice.setTextColor(0xffffffff);notice.setBackgroundColor(0xcc102947);notice.setPadding(20,12,20,12);notice.setVisibility(View.GONE);
  android.widget.FrameLayout.LayoutParams lp=new android.widget.FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.CENTER_HORIZONTAL);lp.topMargin=18;root.addView(notice,lp);
  setContentView(root);
  player=new ExoPlayer.Builder(this).build();view.setPlayer(player);
  player.addListener(new Player.Listener(){
   @Override public void onPlayerError(PlaybackException error){message("Falha na transmissão. O canal pode estar fora do ar.");}
   @Override public void onPlaybackStateChanged(int state){if(state==Player.STATE_READY){message("");attempts=0;}else if(state==Player.STATE_BUFFERING)message("Carregando transmissão...");else if(state==Player.STATE_ENDED)message("A transmissão terminou na origem.");}
  });
  MediaItem item=new MediaItem.Builder().setUri(url).setLiveConfiguration(new MediaItem.LiveConfiguration.Builder().setTargetOffsetMs(9000).build()).build();
  player.setMediaItem(item);player.prepare();player.play();handler.postDelayed(health,4000);
 }
 private void recover(){if(player==null)return;if(attempts>=2){message("Vídeo travado ou repetido na origem. Experimente outro canal.");return;}attempts++;message("Reconectando ao canal...");player.stop();player.prepare();player.play();}
 private void message(String msg){if(notice==null)return;notice.setText(msg);notice.setVisibility(msg.isEmpty()?View.GONE:View.VISIBLE);}
 @Override protected void onStart(){super.onStart();stopped=false;if(player!=null)player.play();}
 @Override protected void onStop(){stopped=true;handler.removeCallbacks(health);if(player!=null)player.pause();super.onStop();}
 @Override protected void onDestroy(){handler.removeCallbacks(health);if(player!=null){player.release();player=null;}super.onDestroy();}
}