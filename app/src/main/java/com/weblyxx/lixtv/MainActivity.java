package com.weblyxx.lixtv;
import android.app.*;
import android.content.*;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.text.*;
import java.util.*;

public class MainActivity extends Activity {
 private final int BG=Color.rgb(7,19,39),SURFACE=Color.rgb(17,42,74),BLUE=Color.rgb(19,112,248),WHITE=Color.WHITE;
 private LinearLayout content; private TextView status; private EditText search;
 private List<ChannelRepository.Channel> channels=new ArrayList<>();
 private String current="Brasil";private int page=0;private static final int PAGE=55;private boolean loading=false;
 private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 private GradientDrawable bg(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(12));return d;}
 private TextView label(String text,int size){TextView t=new TextView(this);t.setText(text);t.setTextColor(WHITE);t.setTextSize(size);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
 private TextView button(String name){TextView b=label(name,16);b.setGravity(Gravity.CENTER);b.setTypeface(null,1);b.setFocusable(true);b.setClickable(true);b.setBackground(bg(SURFACE));b.setPadding(dp(12),dp(9),dp(12),dp(9));
 b.setOnFocusChangeListener((v,f)->{b.setBackground(bg(f?BLUE:SURFACE));b.setScaleX(f?1.02f:1f);b.setScaleY(f?1.02f:1f);});return b;}
 @Override public void onCreate(Bundle state){super.onCreate(state);build();load("Brasil");}
 private void build(){
  ScrollView sc=new ScrollView(this);sc.setFillViewport(true);sc.setBackgroundColor(BG);
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(24),dp(18),dp(24),dp(24));sc.addView(root);setContentView(sc);
  TextView logo=label("◉  LIX TV",31);logo.setTextColor(Color.rgb(83,192,255));logo.setTypeface(null,1);root.addView(logo);
  TextView sub=label("WEBLYXX  •  ANDROID TV BOX  •  CANAIS PÚBLICOS",12);sub.setTextColor(Color.rgb(149,185,215));root.addView(sub);
  LinearLayout nav=new LinearLayout(this);nav.setPadding(0,dp(14),0,dp(12));root.addView(nav);
  for(String tab:new String[]{"Brasil","Mundo","Favoritos","Atualizar"}){
   TextView b=button(tab);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(55),1);lp.setMargins(0,0,dp(9),0);nav.addView(b,lp);
   b.setOnClickListener(v->{if(tab.equals("Atualizar")){load(current.equals("Mundo")?"Mundo":"Brasil");return;}current=tab;page=0;if(tab.equals("Mundo")||tab.equals("Brasil"))load(tab);else render();});
  }
  search=new EditText(this);search.setSingleLine(true);search.setTextColor(WHITE);search.setHintTextColor(Color.rgb(157,188,218));search.setHint("Buscar canais pelo nome");search.setTextSize(18);search.setPadding(dp(16),0,dp(16),0);search.setBackground(bg(SURFACE));root.addView(search,new LinearLayout.LayoutParams(-1,dp(55)));
  search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){page=0;render();}public void afterTextChanged(Editable e){}});
  status=label("Carregando...",14);status.setPadding(0,dp(14),0,dp(9));root.addView(status);
  content=new LinearLayout(this);content.setOrientation(1);root.addView(content);
 }
 private void load(String tab){loading=true;current=tab;render();ChannelRepository.load(tab.equals("Mundo")?ChannelRepository.GLOBAL:ChannelRepository.BR,(result,error)->{loading=false;if(error!=null){status.setText("Falha ao carregar: "+error);content.removeAllViews();return;}channels=result;page=0;render();});}
 private boolean favorite(String url){return getPreferences(0).getBoolean("fav_"+url,false);}
 private void render(){if(content==null)return;content.removeAllViews();if(loading){status.setText("Carregando canais do IPTV-org...");return;}
  String q=search.getText().toString().toLowerCase(Locale.ROOT).trim();
  List<ChannelRepository.Channel> filtered=new ArrayList<>();
  for(ChannelRepository.Channel ch:channels){if(current.equals("Favoritos")&&!favorite(ch.url))continue;if(!ch.title.toLowerCase(Locale.ROOT).contains(q)&&!ch.group.toLowerCase(Locale.ROOT).contains(q))continue;filtered.add(ch);}
  int start=Math.min(page*PAGE,filtered.size()),end=Math.min(start+PAGE,filtered.size());
  status.setText(current+"  •  "+filtered.size()+" canais encontrados  •  página "+(page+1));
  if(page>0){TextView prev=button("◀ Página anterior");content.addView(prev,new LinearLayout.LayoutParams(-1,dp(52)));prev.setOnClickListener(v->{page--;render();});}
  for(int i=start;i<end;i++){ChannelRepository.Channel ch=filtered.get(i);
   LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(73));lp.setMargins(0,dp(6),0,0);content.addView(row,lp);
   TextView play=button("▶  "+ch.title+(ch.group.isEmpty()?"":"  ·  "+ch.group));play.setGravity(Gravity.CENTER_VERTICAL);row.addView(play,new LinearLayout.LayoutParams(0,-1,1));
   play.setOnClickListener(v->{Intent intent=new Intent(this,PlayerActivity.class);intent.putExtra("url",ch.url);startActivity(intent);});
   TextView fav=button(favorite(ch.url)?"★":"☆");LinearLayout.LayoutParams fl=new LinearLayout.LayoutParams(dp(65),-1);fl.leftMargin=dp(8);row.addView(fav,fl);
   fav.setOnClickListener(v->{boolean next=!favorite(ch.url);getPreferences(0).edit().putBoolean("fav_"+ch.url,next).apply();fav.setText(next?"★":"☆");if(current.equals("Favoritos"))render();});
  }
  if(end<filtered.size()){TextView next=button("Próxima página ▶");LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(56));lp.topMargin=dp(12);content.addView(next,lp);next.setOnClickListener(v->{page++;render();});}
  if(filtered.isEmpty()){TextView none=label("Nenhum canal nesta seleção. Experimente uma busca diferente.",18);content.addView(none);}
 }
}