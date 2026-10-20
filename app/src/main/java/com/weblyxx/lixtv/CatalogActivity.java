package com.weblyxx.lixtv;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.view.*;import android.widget.*;import java.util.*;
public class CatalogActivity extends Activity {
 boolean series;LinearLayout results;EditText query;TextView status;
 final int BACK=0xff060F20,PANEL=0xff142B4B,ACCENT=0xff1879FF,MUTED=0xffA8C0DC;
 int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 GradientDrawable panel(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(14));return d;}
 TextView label(String s,int size){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(-1);v.setGravity(Gravity.CENTER_VERTICAL);return v;}
 TextView button(String s){TextView b=label(s,17);b.setPadding(dp(18),dp(13),dp(18),dp(13));b.setBackground(panel(PANEL));b.setFocusable(true);b.setClickable(true);b.setOnFocusChangeListener((v,f)->{b.setBackground(panel(f?ACCENT:PANEL));b.setScaleX(f?1.02f:1f);});return b;}
 @Override public void onCreate(Bundle state){super.onCreate(state);series=getIntent().getBooleanExtra("series",false);
 ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BACK);
 LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(30),dp(22),dp(30),dp(24));scroll.addView(root);setContentView(scroll);
 TextView brand=label("LIX TV  /  "+(series?"SÉRIES":"FILMES"),30);brand.setTypeface(null,1);brand.setTextColor(0xff64BEFF);root.addView(brand);
 TextView caption=label("Explore histórias e descubra novos títulos",16);caption.setTextColor(MUTED);root.addView(caption);
 LinearLayout tools=new LinearLayout(this);tools.setPadding(0,dp(16),0,dp(10));root.addView(tools);
 query=new EditText(this);query.setSingleLine(true);query.setTextColor(-1);query.setHintTextColor(MUTED);query.setHint("Pesquisar "+(series?"séries":"filmes"));query.setPadding(dp(16),0,dp(16),0);query.setBackground(panel(PANEL));tools.addView(query,new LinearLayout.LayoutParams(0,dp(57),1));
 TextView find=button("⌕  Buscar");LinearLayout.LayoutParams findp=new LinearLayout.LayoutParams(dp(145),dp(57));findp.leftMargin=dp(12);tools.addView(find,findp);find.setOnClickListener(v->refresh());
 status=label("Carregando...",15);status.setTextColor(MUTED);root.addView(status);
 results=new LinearLayout(this);results.setOrientation(1);root.addView(results);
 refresh();
 }
 void refresh(){results.removeAllViews();String token=getSharedPreferences("settings",0).getString("tmdb_token","");status.setText("Consultando catálogo em português...");
 TmdbCatalog.list(token,series,query.getText().toString(),(titles,error)->{
 if(isFinishing()||isDestroyed())return;
 if(error!=null){status.setText(error);return;}
 status.setText(titles.size()+" títulos • TMDB fornece capas e informações, não os vídeos");
 for(TmdbCatalog.Title t:titles){
 LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(12),dp(12),dp(12),dp(12));row.setBackground(panel(PANEL));
 LinearLayout.LayoutParams rowParams=new LinearLayout.LayoutParams(-1,dp(160));rowParams.topMargin=dp(12);results.addView(row,rowParams);
 ImageView poster=new ImageView(this);poster.setScaleType(ImageView.ScaleType.CENTER_CROP);poster.setBackground(panel(0xff25446A));LinearLayout.LayoutParams img=new LinearLayout.LayoutParams(dp(100),dp(144));img.rightMargin=dp(16);row.addView(poster,img);
 if(t.poster!=null&&t.poster.startsWith("/"))LogoLoader.load(poster,"https://image.tmdb.org/t/p/w342"+t.poster);
 LinearLayout copy=new LinearLayout(this);copy.setOrientation(1);row.addView(copy,new LinearLayout.LayoutParams(0,-1,1));
 TextView name=label(t.name,21);name.setTypeface(null,1);name.setMaxLines(2);copy.addView(name);
 TextView description=label(t.summary.isEmpty()?"Sinopse ainda não disponível em português":t.summary,15);description.setTextColor(MUTED);description.setMaxLines(3);copy.addView(description);
 TextView details=button("Detalhes  ›");details.setTextSize(14);copy.addView(details,new LinearLayout.LayoutParams(-1,dp(46)));
 details.setOnClickListener(v->new AlertDialog.Builder(this).setTitle(t.name).setMessage(t.summary+"\n\nTMDB ID: "+t.id+"\nO TMDB não oferece vídeo. Reprodução depende de fonte autorizada separada.").setPositiveButton("Voltar",null).show());
 }
 });}
}