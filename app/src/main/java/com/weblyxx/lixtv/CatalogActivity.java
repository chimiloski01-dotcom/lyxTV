package com.weblyxx.lixtv;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.view.*;import android.widget.*;import android.text.*;import java.util.*;
public class CatalogActivity extends Activity {
 boolean series;LinearLayout results;EditText query;TextView status;int bg=0xff071429;
 int dp(int p){return (int)(p*getResources().getDisplayMetrics().density+.5f);}
 TextView button(String title){TextView t=new TextView(this);t.setText(title);t.setTextColor(-1);t.setTextSize(17);t.setPadding(dp(18),dp(17),dp(18),dp(17));t.setBackgroundColor(0xff183353);t.setFocusable(true);t.setOnFocusChangeListener((v,focused)->t.setBackgroundColor(focused?0xff197AFF:0xff183353));return t;}
 @Override public void onCreate(Bundle saved){super.onCreate(saved);series=getIntent().getBooleanExtra("series",false);
 ScrollView scroll=new ScrollView(this);scroll.setBackgroundColor(bg);LinearLayout root=new LinearLayout(this);root.setPadding(dp(24),dp(18),dp(24),dp(20));root.setOrientation(1);scroll.addView(root);setContentView(scroll);
 TextView heading=button(series?"SÉRIES • LIX TV":"FILMES • LIX TV");heading.setTextSize(26);heading.setBackgroundColor(bg);root.addView(heading);
 query=new EditText(this);query.setTextColor(-1);query.setHintTextColor(0xff9bbadd);query.setSingleLine(true);query.setHint("Pesquisar no TMDB");root.addView(query);
 TextView search=button("⌕ Pesquisar");root.addView(search);search.setOnClickListener(v->refresh());
 status=button("Carregando catálogo...");status.setBackgroundColor(bg);root.addView(status);
 results=new LinearLayout(this);results.setOrientation(1);root.addView(results);refresh();
 }
 void refresh(){results.removeAllViews();String token=getSharedPreferences("settings",0).getString("tmdb_token","");status.setText("Consultando informações de "+(series?"séries":"filmes")+"...");
 TmdbCatalog.list(token,series,query.getText().toString(),(titles,error)->{
  if(isFinishing())return;
  if(error!=null){status.setText(error);return;}
  status.setText(titles.size()+" títulos encontrados • Informações do TMDB (sem vídeo)");
  for(TmdbCatalog.Title t:titles){TextView item=button(t.name+"\n"+(t.summary.isEmpty()?"Sem sinopse em português":t.summary));item.setMaxLines(5);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(8);results.addView(item,p);item.setOnClickListener(v->new AlertDialog.Builder(this).setTitle(t.name).setMessage(t.summary+"\n\nTMDB ID: "+t.id+"\nA disponibilidade de reprodução precisa ser verificada separadamente.").setPositiveButton("OK",null).show());}
 });}
}