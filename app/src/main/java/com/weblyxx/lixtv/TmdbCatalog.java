package com.weblyxx.lixtv;
import android.os.Handler;import android.os.Looper;import org.json.*;import java.net.*;import java.io.*;import java.util.*;import java.util.concurrent.*;
public final class TmdbCatalog {
 public static final class Title {
  public final int id;public final String name,summary,poster;public final boolean series;
  Title(int id,String name,String summary,String poster,boolean series){this.id=id;this.name=name;this.summary=summary;this.poster=poster;this.series=series;}
 }
 public interface Result {void done(List<Title> titles,String error);}
 private static final ExecutorService queue=Executors.newSingleThreadExecutor();
 private static final Handler ui=new Handler(Looper.getMainLooper());
 public static void list(String bearer,boolean series,String query,Result callback){
  if(bearer==null||bearer.trim().isEmpty()){callback.done(new ArrayList<>(),"Configure seu token TMDB nas Configurações.");return;}
  queue.execute(()->{
   List<Title> titles=new ArrayList<>();String error=null;HttpURLConnection conn=null;
   try {
    String kind=series?"tv":"movie";
    String path=query.trim().isEmpty()?"/3/trending/"+kind+"/week?language=pt-BR":"/3/search/"+kind+"?language=pt-BR&query="+URLEncoder.encode(query,"UTF-8");
    conn=(HttpURLConnection)new URL("https://api.themoviedb.org"+path).openConnection();
    conn.setRequestProperty("Authorization","Bearer "+bearer.trim());conn.setRequestProperty("accept","application/json");
    conn.setConnectTimeout(9000);conn.setReadTimeout(12000);
    if(conn.getResponseCode()!=200)throw new IOException("TMDB HTTP "+conn.getResponseCode());
    ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream stream=conn.getInputStream()){byte[] buffer=new byte[4096];int n;while((n=stream.read(buffer))!=-1){out.write(buffer,0,n);if(out.size()>1200000)throw new IOException("Resposta muito grande");}}
    JSONArray a=new JSONObject(out.toString("UTF-8")).optJSONArray("results");
    if(a!=null)for(int i=0;i<a.length()&&i<35;i++){JSONObject v=a.optJSONObject(i);if(v==null)continue;titles.add(new Title(v.optInt("id"),v.optString(series?"name":"title"),v.optString("overview"),v.optString("poster_path"),series));}
   }catch(Exception ex){error=ex.getMessage();}finally{if(conn!=null)conn.disconnect();}
   String e=error;ui.post(()->callback.done(titles,e));
  });
 }
}