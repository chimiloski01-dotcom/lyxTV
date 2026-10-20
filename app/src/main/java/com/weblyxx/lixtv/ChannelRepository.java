package com.weblyxx.lixtv;
import android.os.Handler;
import android.os.Looper;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
public final class ChannelRepository {
 public static final String BR="https://iptv-org.github.io/iptv/countries/br.m3u";
 public static final String GLOBAL="https://iptv-org.github.io/iptv/index.m3u";
 public static final class Channel {
  public final String title,url,group,logo;
  public Channel(String title,String url,String group,String logo){this.title=title;this.url=url;this.group=group;this.logo=logo;}
 }
 public interface Callback {void done(List<Channel> channels,String error);}
 private static final ExecutorService pool=Executors.newSingleThreadExecutor();
 private static final Handler main=new Handler(Looper.getMainLooper());
 private static String attr(String text,String key){int start=text.indexOf(key+"=\"");if(start<0)return "";start+=key.length()+2;int end=text.indexOf('"',start);return end<0?"":text.substring(start,end);}
 public static void load(String url,Callback callback){
  pool.execute(()->{List<Channel> result=new ArrayList<>();String error=null;HttpURLConnection conn=null;
   try {conn=(HttpURLConnection)new URL(url).openConnection();conn.setConnectTimeout(12000);conn.setReadTimeout(22000);conn.setRequestProperty("User-Agent","LIX-TV/0.4");
    if(conn.getResponseCode()!=200)throw new IOException("HTTP "+conn.getResponseCode());
    try(BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"))){
     String line,title="",group="",logo="";
     while((line=reader.readLine())!=null){line=line.trim();if(line.startsWith("#EXTINF:")){int comma=line.lastIndexOf(',');title=comma>=0?line.substring(comma+1).trim():"Canal";group=attr(line,"group-title");logo=attr(line,"tvg-logo");}
      else if((line.startsWith("http://")||line.startsWith("https://"))&&!title.isEmpty()){result.add(new Channel(title,line,group,logo));title="";if(result.size()>=15000)break;}
     }
    }
    if(result.isEmpty())throw new IOException("Nenhum canal");
   } catch(Exception ex){error=ex.getMessage();}finally{if(conn!=null)conn.disconnect();}
   String e=error;main.post(()->callback.done(result,e));
  });
 }
}