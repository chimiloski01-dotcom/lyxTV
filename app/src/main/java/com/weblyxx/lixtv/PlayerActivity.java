package com.weblyxx.lixtv;
import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.media3.common.*;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
public class PlayerActivity extends Activity {
 private ExoPlayer player;
 @Override public void onCreate(Bundle saved){super.onCreate(saved);
  String url=getIntent().getStringExtra("url");
  if(url==null||!(url.startsWith("http://")||url.startsWith("https://"))){Toast.makeText(this,"Fonte inválida",Toast.LENGTH_LONG).show();finish();return;}
  PlayerView view=new PlayerView(this);view.setUseController(true);setContentView(view);
  player=new ExoPlayer.Builder(this).build();view.setPlayer(player);
  player.addListener(new Player.Listener(){@Override public void onPlayerError(PlaybackException error){Toast.makeText(PlayerActivity.this,"Transmissão indisponível",Toast.LENGTH_LONG).show();}});
  player.setMediaItem(MediaItem.fromUri(url));player.prepare();player.play();
 }
 @Override protected void onStop(){super.onStop();if(player!=null)player.pause();}
 @Override protected void onDestroy(){if(player!=null){player.release();player=null;}super.onDestroy();}
}