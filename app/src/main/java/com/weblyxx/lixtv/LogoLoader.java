package com.weblyxx.lixtv;
import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.os.Handler;import android.os.Looper;import android.widget.ImageView;import java.net.*;import java.io.*;import java.util.concurrent.*;import android.util.LruCache;
public final class LogoLoader {
 private static final ExecutorService work=Executors.newFixedThreadPool(3);
 private static final Handler ui=new Handler(Looper.getMainLooper());
 private static final LruCache<String,Bitmap> cache=new LruCache<String,Bitmap>(8*1024*1024){protected int sizeOf(String k,Bitmap v){return v.getByteCount();}};
 public static void load(ImageView view,String url){
  view.setImageDrawable(null);
  if(url==null||!url.startsWith("https://"))return;
  Bitmap saved=cache.get(url);if(saved!=null){view.setImageBitmap(saved);return;}
  view.setTag(url);
  work.execute(()->{HttpURLConnection con=null;try{
   con=(HttpURLConnection)new URL(url).openConnection();con.setConnectTimeout(3500);con.setReadTimeout(3500);
   if(con.getResponseCode()!=200)return;
   try(InputStream in=con.getInputStream()){BitmapFactory.Options opts=new BitmapFactory.Options();opts.inSampleSize=2;Bitmap bmp=BitmapFactory.decodeStream(in,null,opts);
    if(bmp==null||bmp.getByteCount()>2*1024*1024)return;cache.put(url,bmp);
    ui.post(()->{if(url.equals(view.getTag()))view.setImageBitmap(bmp);});
   }
  }catch(Exception ignored){}finally{if(con!=null)con.disconnect();}});
 }
}