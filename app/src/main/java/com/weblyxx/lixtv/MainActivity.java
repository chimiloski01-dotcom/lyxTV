package com.weblyxx.lixtv;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.graphics.Typeface;import android.graphics.drawable.GradientDrawable;import android.text.*;import android.view.*;import android.widget.*;import java.util.*;
public class MainActivity extends Activity {
 final int NAVY=0xff050D1C, PANEL=0xff142745, BLUE=0xff187EFF, SOFT=0xffA6BAD5, WHITE=Color.WHITE;
 LinearLayout body;TextView info;EditText search;String tab="Início";int page=0;List<ChannelRepository.Channel> channels=new ArrayList<>();boolean loading=false;final int PAGE=24;String category="Todos";boolean az=true;
 int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
 GradientDrawable shape(int c){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(dp(13));return d;}
 TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(WHITE);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
 TextView btn(String title){TextView t=text(title,17);t.setFocusable(true);t.setClickable(true);t.setPadding(dp(16),dp(11),dp(16),dp(11));t.setGravity(Gravity.CENTER_VERTICAL);t.setBackground(shape(PANEL));t.setOnFocusChangeListener((v,f)->{t.setBackground(shape(f?BLUE:PANEL));t.setScaleX(f?1.025f:1f);t.setScaleY(f?1.025f:1f);});return t;}
 void add(LinearLayout l,TextView v,int height){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(height));p.bottomMargin=dp(8);l.addView(v,p);}
 @Override public void onCreate(Bundle s){super.onCreate(s);frame();load("Brasil");}
 void frame(){
  ScrollView sc=new ScrollView(this);sc.setBackgroundColor(NAVY);sc.setFillViewport(true);
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(26),dp(16),dp(26),dp(30));sc.addView(root);setContentView(sc);
  TextView logo=text("LIX  TV",33);logo.setTextColor(0xff56c6ff);logo.setTypeface(Typeface.DEFAULT,Typeface.BOLD);root.addView(logo);
  TextView slogan=text("WEBLYXX    /    STREAMING PARA TV BOX",12);slogan.setTextColor(SOFT);root.addView(slogan);
  TextView hero=text("LIX TV     •     AO VIVO  &  ON DEMAND\\n\\nSeu universo de conteúdo, com navegação simples pelo controle remoto.",22);hero.setTypeface(null,1);hero.setPadding(dp(24),dp(20),dp(24),dp(20));GradientDrawable gradient=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff1268D9,0xff142E57,0xff081628});gradient.setCornerRadius(dp(18));hero.setBackground(gradient);LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,dp(165));hp.topMargin=dp(18);root.addView(hero,hp);
  LinearLayout nav=new LinearLayout(this);nav.setPadding(0,dp(14),0,dp(10));root.addView(nav);
  for(String option:new String[]{"Início","TV ao vivo","Filmes","Séries","Favoritos","Configurações"}){TextView b=btn(option);b.setTextSize(14);b.setGravity(Gravity.CENTER);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(53),1);p.rightMargin=dp(7);nav.addView(b,p);b.setOnClickListener(v->{tab=option;page=0;search.setVisibility((tab.equals("TV ao vivo")||tab.equals("Favoritos"))?View.VISIBLE:View.GONE);render();});}
  search=new EditText(this);search.setTextColor(WHITE);search.setHintTextColor(SOFT);search.setSingleLine(true);search.setHint("Pesquisar canais");search.setPadding(dp(16),0,dp(16),0);search.setBackground(shape(PANEL));root.addView(search,new LinearLayout.LayoutParams(-1,dp(52)));
  search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){page=0;render();}public void afterTextChanged(Editable e){}});
  info=text("Carregando canais...",14);info.setTextColor(SOFT);info.setPadding(0,dp(12),0,dp(12));root.addView(info);
  body=new LinearLayout(this);body.setOrientation(1);root.addView(body);search.setVisibility(View.GONE);
 }
 void load(String mode){loading=true;render();ChannelRepository.load(mode.equals("Mundo")?ChannelRepository.GLOBAL:ChannelRepository.BR,(list,error)->{loading=false;if(error!=null){info.setText("Não foi possível atualizar canais: "+error);return;}channels=list;render();});}
 boolean fav(String url){return getPreferences(0).getBoolean("fav_"+url,false);}
 void play(String url){Intent i=new Intent(this,PlayerActivity.class);i.putExtra("url",url);startActivity(i);}
 void section(String heading,String caption){TextView t=text(heading,26);t.setTypeface(null,1);add(body,t,45);TextView c=text(caption,15);c.setTextColor(SOFT);add(body,c,42);}
 void item(String label,String subtitle,String url,boolean isChannel,String logoUrl){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(78));rp.bottomMargin=dp(9);body.addView(r,rp);
 if(isChannel){ImageView iv=new ImageView(this);iv.setScaleType(ImageView.ScaleType.FIT_CENTER);iv.setBackground(shape(PANEL));LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(dp(84),dp(70));ip.rightMargin=dp(8);r.addView(iv,ip);LogoLoader.load(iv,logoUrl);}
 TextView b=btn("▶  "+label+"\n"+subtitle);b.setTextSize(16);r.addView(b,new LinearLayout.LayoutParams(0,-1,1));b.setOnClickListener(v->play(url));
 if(isChannel){TextView f=btn(fav(url)?"★":"☆");LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(dp(66),-1);fp.leftMargin=dp(8);r.addView(f,fp);f.setOnClickListener(v->{boolean n=!fav(url);getPreferences(0).edit().putBoolean("fav_"+url,n).apply();f.setText(n?"★":"☆");});}}
 void films(){section("Cinema aberto","Curtas e filmes de acesso público disponíveis para reprodução.");
 item("Big Buck Bunny","Curta de animação aberto • Filme","https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",false,null);
 item("Elephants Dream","Animação independente • Filme","https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",false,null);
 item("For Bigger Joyrides","Vídeo público de teste • Curta","https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",false,null);
 }
 void series(){section("Coleções e episódios","Experiência inicial de episódios. Séries completas dependem de acervo autorizado.");
 item("Coleção de curtas • Episódio 1","Big Buck Bunny","https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",false,null);
 item("Coleção de curtas • Episódio 2","Elephants Dream","https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",false,null);
 TextView note=text("Catálogo inicial de demonstração; não há séries comerciais completas nesta versão.",15);note.setTextColor(SOFT);add(body,note,80);
 }
 void settings(){section("Configurações","Sobre o aplicativo e créditos");
 add(body,text("LIX TV v0.6 • Android TV Box",21),65);
 add(body,text("Desenvolvimento e criação: WEBLYXX",22),62);
 TextView desc=text("Projeto e desenvolvimento: Weblyxx. Interface original em azul para navegação com controle remoto.\n\nFontes: IPTV-org para canais públicos; mídias abertas de demonstração para filmes e episódios.\n\nO LIX TV não fornece assinaturas de serviços de terceiros.",16);desc.setTextColor(SOFT);add(body,desc,175);
 TextView reload=btn("↻ Atualizar lista brasileira");add(body,reload,62);reload.setOnClickListener(v->load("Brasil"));
 TextView world=btn("🌐 Carregar canais internacionais");add(body,world,62);world.setOnClickListener(v->load("Mundo"));
 }
 void render(){if(body==null)return;body.removeAllViews();if(loading){info.setText("Sincronizando lista de canais públicos...");return;}
 info.setText(tab+"    •    LIX TV 0.6");
 if(tab.equals("Configurações")){settings();return;}
 if(tab.equals("Filmes")){films();return;}
 if(tab.equals("Séries")){series();return;}
 if(tab.equals("Início")){section("Seu entretenimento começa aqui","Canais ao vivo e conteúdos gratuitos, organizados para a sua televisão.");TextView next=btn("▶ Assistir TV ao vivo");add(body,next,70);next.setOnClickListener(v->{tab="TV ao vivo";render();});films();return;}
 section(tab.equals("Favoritos")?"Seus canais favoritos":"Canais ao vivo","Escolha uma categoria, pesquise pelo nome e pressione OK para assistir.");
  LinearLayout filters=new LinearLayout(this);filters.setOrientation(LinearLayout.HORIZONTAL);body.addView(filters);for(String opt:new String[]{"Todos","Notícias","Esportes","Entretenimento","Infantil","Música","Outros"}){TextView b=btn(opt);b.setTextSize(13);LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(0,dp(48),1);fp.rightMargin=dp(5);filters.addView(b,fp);b.setOnClickListener(v->{category=opt;page=0;render();});}
  TextView sort=btn(az?"Ordem: A–Z  ↓":"Ordem: Z–A  ↑");add(body,sort,50);sort.setOnClickListener(v->{az=!az;page=0;render();});
 String q=search.getText().toString().trim().toLowerCase(Locale.ROOT);List<ChannelRepository.Channel> filtered=new ArrayList<>();
 for(ChannelRepository.Channel c:channels){if(tab.equals("Favoritos")&&!fav(c.url))continue;if(!(c.title+" "+c.group).toLowerCase(Locale.ROOT).contains(q))continue;if(!category.equals("Todos")){String g=(c.group+" "+c.title).toLowerCase(Locale.ROOT);boolean match=false;switch(category){case "Notícias":match=g.contains("news")||g.contains("notícia")||g.contains("jornal");break;case "Esportes":match=g.contains("sport")||g.contains("esporte")||g.contains("futebol");break;case "Entretenimento":match=g.contains("entertain")||g.contains("entreten")||g.contains("general");break;case "Infantil":match=g.contains("kids")||g.contains("infantil")||g.contains("children");break;case "Música":match=g.contains("music")||g.contains("música");break;default:match=!(g.contains("news")||g.contains("sport")||g.contains("kids")||g.contains("music"));}if(!match)continue;}filtered.add(c);}
 Collections.sort(filtered,(a,b)->az?a.title.compareToIgnoreCase(b.title):b.title.compareToIgnoreCase(a.title));
 int start=Math.min(page*PAGE,filtered.size()),end=Math.min(start+PAGE,filtered.size());
 add(body,text(filtered.size()+" canais encontrados • página "+(page+1),15),46);
 if(page>0){TextView back=btn("◀ Voltar página");add(body,back,58);back.setOnClickListener(v->{page--;render();});}
 for(int i=start;i<end;i++){ChannelRepository.Channel c=filtered.get(i);item(c.title,c.group.isEmpty()?"Canal ao vivo":c.group,c.url,true,c.logo);}
 if(end<filtered.size()){TextView next=btn("Próxima página ▶");add(body,next,58);next.setOnClickListener(v->{page++;render();});}
 if(filtered.isEmpty())add(body,text("Nenhum canal encontrado nesta lista.",16),75);
 }
}