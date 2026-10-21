package com.weblyxx.lixtv;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.text.*;import android.view.*;import android.widget.*;import java.util.*;
public class MainActivity extends Activity {
 final int BG=0xff060D1A,SIDE=0xff09182B,CARD=0xff162A45,BLUE=0xff237EFF,PALE=0xffB3C3DB,WHITE=-1;
 LinearLayout content,sidebar;TextView status;EditText search;String tab="Início",category="Todos";boolean az=true,loading=false;int page=0;
 List<ChannelRepository.Channel> channels=new ArrayList<>();final int PAGE=30;
 int dp(int x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
 GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 TextView text(String title,int size){TextView v=new TextView(this);v.setText(title);v.setTextColor(WHITE);v.setTextSize(size);v.setGravity(Gravity.CENTER_VERTICAL);return v;}
 TextView button(String title){TextView v=text(title,16);v.setBackground(shape(CARD,12));v.setPadding(dp(15),dp(10),dp(15),dp(10));v.setFocusable(true);v.setClickable(true);v.setOnFocusChangeListener((w,f)->{v.setBackground(shape(f?BLUE:CARD,12));v.setScaleX(f?1.035f:1f);v.setScaleY(f?1.035f:1f);});return v;}
 void margin(LinearLayout parent,View child,int height,int bottom){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,height<0?height:dp(height));p.bottomMargin=dp(bottom);parent.addView(child,p);}
 @Override public void onCreate(Bundle b){super.onCreate(b);build();load("Brasil");}
 void build(){
 LinearLayout shell=new LinearLayout(this);shell.setOrientation(0);shell.setBackgroundColor(BG);setContentView(shell);
 sidebar=new LinearLayout(this);sidebar.setOrientation(1);sidebar.setPadding(dp(16),dp(24),dp(12),dp(14));sidebar.setBackgroundColor(SIDE);
 shell.addView(sidebar,new LinearLayout.LayoutParams(dp(195),-1));
 TextView brand=text("LIX  TV",30);brand.setTypeface(null,1);brand.setTextColor(0xff61BAFF);margin(sidebar,brand,58,0);
 TextView signature=text("BY WEBLYXX",12);signature.setTextColor(PALE);margin(sidebar,signature,30,22);
 for(String nav:new String[]{"Início","TV ao vivo","Filmes","Séries","Favoritos","Configurações"}){
  TextView b=button(icon(nav)+"  "+nav);margin(sidebar,b,54,9);
  b.setOnClickListener(v->{tab=nav;page=0;render();});
 }
 TextView copyright=text("LIX TV 0.7\nWEBLYXX",11);copyright.setTextColor(PALE);sidebar.addView(copyright);
 ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);shell.addView(scroll,new LinearLayout.LayoutParams(0,-1,1));
 content=new LinearLayout(this);content.setOrientation(1);content.setPadding(dp(26),dp(20),dp(26),dp(26));scroll.addView(content);
 render();
 }
 String icon(String s){switch(s){case "Início":return "⌂";case "TV ao vivo":return "▣";case "Filmes":return "▶";case "Séries":return "▤";case "Favoritos":return "★";default:return "⚙";}}
 void title(String heading,String subtitle){TextView h=text(heading,27);h.setTypeface(null,1);margin(content,h,48,0);TextView s=text(subtitle,15);s.setTextColor(PALE);margin(content,s,33,12);}
 void hero(){
 TextView h=text("LIX TV   •   CINEMA & TV\n\nTudo em um só lugar.\nCanais e uma seleção de produções abertas.",24);
 h.setTypeface(null,1);h.setPadding(dp(30),dp(18),dp(20),dp(20));
 GradientDrawable grad=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff1768CE,0xff102D53,0xff0B182C});grad.setCornerRadius(dp(20));h.setBackground(grad);margin(content,h,210,22);
 TextView go=button("▶  Explorar canais ao vivo");margin(content,go,55,15);go.setOnClickListener(v->{tab="TV ao vivo";render();});
 }
 void load(String mode){loading=true;ChannelRepository.load(mode.equals("Mundo")?ChannelRepository.GLOBAL:ChannelRepository.BR,(list,error)->{loading=false;if(error!=null){Toast.makeText(this,"Lista indisponível: "+error,Toast.LENGTH_LONG).show();}else channels=list;render();});}
 boolean fav(String url){return getPreferences(0).getBoolean("fav_"+url,false);}
 void watch(String url){Intent i=new Intent(this,PlayerActivity.class);i.putExtra("url",url);startActivity(i);}
 void channelCard(LinearLayout parent,ChannelRepository.Channel ch,boolean compact){
 LinearLayout wrapper=new LinearLayout(this);wrapper.setOrientation(1);wrapper.setPadding(dp(9),dp(8),dp(9),dp(8));wrapper.setBackground(shape(CARD,14));
 LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(compact?dp(172):dp(205),dp(164));wp.rightMargin=dp(12);wp.bottomMargin=dp(12);parent.addView(wrapper,wp);
 ImageView logo=new ImageView(this);logo.setBackground(shape(0xff233A59,10));logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
 wrapper.addView(logo,new LinearLayout.LayoutParams(-1,dp(75)));LogoLoader.load(logo,ch.logo);
 TextView name=button(ch.title);name.setTextSize(14);name.setSingleLine(true);name.setEllipsize(TextUtils.TruncateAt.END);wrapper.addView(name,new LinearLayout.LayoutParams(-1,dp(42)));name.setOnClickListener(v->watch(ch.url));
 TextView star=button(fav(ch.url)?"★  Favorito":"☆  Favoritar");star.setTextSize(12);wrapper.addView(star,new LinearLayout.LayoutParams(-1,dp(30)));star.setOnClickListener(v->{boolean value=!fav(ch.url);getPreferences(0).edit().putBoolean("fav_"+ch.url,value).apply();star.setText(value?"★  Favorito":"☆  Favoritar");});
 }
 void channelRail(String title,List<ChannelRepository.Channel> list){TextView header=text(title,21);header.setTypeface(null,1);margin(content,header,43,5);
 HorizontalScrollView sc=new HorizontalScrollView(this);sc.setHorizontalScrollBarEnabled(false);LinearLayout row=new LinearLayout(this);sc.addView(row);margin(content,sc,174,16);
 for(int i=0;i<Math.min(list.size(),10);i++)channelCard(row,list.get(i),true);
 }
 void mediaRail(String title,boolean episodes){TextView head=text(title,22);head.setTypeface(null,1);margin(content,head,48,8);
 HorizontalScrollView sc=new HorizontalScrollView(this);sc.setHorizontalScrollBarEnabled(false);LinearLayout row=new LinearLayout(this);sc.addView(row);margin(content,sc,174,18);
 String[] names=episodes?new String[]{"Coleção aberta · Ep. 1","Coleção aberta · Ep. 2"}:new String[]{"Big Buck Bunny","Elephants Dream","For Bigger Joyrides"};
 String[] links=episodes?new String[]{"https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4","https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"}:new String[]{"https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4","https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4","https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"};
 for(int i=0;i<names.length;i++){String url=links[i];LinearLayout tile=new LinearLayout(this);tile.setOrientation(1);tile.setPadding(dp(15),dp(11),dp(15),dp(11));tile.setBackground(shape(CARD,16));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(210),dp(158));p.rightMargin=dp(12);row.addView(tile,p);
 TextView art=text("▶",52);art.setGravity(Gravity.CENTER);art.setBackground(shape(0xff193F71,10));tile.addView(art,new LinearLayout.LayoutParams(-1,dp(82)));
 TextView play=button(names[i]);play.setMaxLines(2);play.setTextSize(14);tile.addView(play,new LinearLayout.LayoutParams(-1,dp(54)));play.setOnClickListener(v->watch(url));}
 }
 void filters(){HorizontalScrollView sc=new HorizontalScrollView(this);LinearLayout row=new LinearLayout(this);sc.addView(row);margin(content,sc,58,9);
 for(String opt:new String[]{"Todos","Notícias","Esportes","Entretenimento","Infantil","Música","Outros"}){TextView b=button(opt);b.setTextSize(13);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(135),dp(45));p.rightMargin=dp(7);row.addView(b,p);b.setOnClickListener(v->{category=opt;page=0;render();});}}
 boolean matches(ChannelRepository.Channel c){String g=(c.title+" "+c.group).toLowerCase(Locale.ROOT);switch(category){case "Notícias":return g.contains("news")||g.contains("notícia")||g.contains("jornal");case "Esportes":return g.contains("sport")||g.contains("futebol")||g.contains("esporte");case "Infantil":return g.contains("kids")||g.contains("infantil")||g.contains("children");case "Música":return g.contains("music")||g.contains("música");case "Entretenimento":return g.contains("entertain")||g.contains("variedades")||g.contains("general");case "Outros":return !g.contains("news")&&!g.contains("sport")&&!g.contains("kids")&&!g.contains("music");default:return true;}}
 void channelsScreen(boolean favorites){
 title(favorites?"Minha lista":"TV ao vivo",favorites?"Canais que você salvou":"Canais públicos • A disponibilidade depende da emissora");
 EditText q=new EditText(this);q.setSingleLine(true);q.setTextColor(WHITE);q.setHintTextColor(PALE);q.setHint("⌕ Pesquisar canais");q.setPadding(dp(18),0,dp(18),0);q.setBackground(shape(CARD,13));margin(content,q,53,11);
 filters();
 TextView sort=button(az?"Ordem: A–Z":"Ordem: Z–A");margin(content,sort,48,11);sort.setOnClickListener(v->{az=!az;render();});
 LinearLayout grid=new LinearLayout(this);grid.setOrientation(1);content.addView(grid);q.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){}public void afterTextChanged(Editable e){}public void onTextChanged(CharSequence s,int start,int before,int count){page=0;fillGrid(grid,favorites,s.toString());}});
 fillGrid(grid,favorites,"");
 }
 void fillGrid(LinearLayout root,boolean favorites,String query){root.removeAllViews();List<ChannelRepository.Channel> filtered=new ArrayList<>();for(ChannelRepository.Channel c:channels){if(favorites&&!fav(c.url))continue;if(!matches(c))continue;if(!(c.title+" "+c.group).toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))continue;filtered.add(c);}
 Collections.sort(filtered,(a,b)->az?a.title.compareToIgnoreCase(b.title):b.title.compareToIgnoreCase(a.title));
 TextView counter=text(filtered.size()+" canais • página "+(page+1),14);counter.setTextColor(PALE);margin(root,counter,39,7);
 int start=Math.min(page*PAGE,filtered.size()),end=Math.min(start+PAGE,filtered.size());
 LinearLayout row=null;for(int i=start;i<end;i++){if((i-start)%3==0){row=new LinearLayout(this);root.addView(row);}channelCard(row,filtered.get(i),false);}
 if(filtered.isEmpty())margin(root,text("Nenhum canal disponível nesta seleção.",17),70,10);
 if(page>0){TextView back=button("◀  Página anterior");margin(root,back,50,8);back.setOnClickListener(v->{page--;fillGrid(root,favorites,query);});}
 if(end<filtered.size()){TextView next=button("Próxima página  ▶");margin(root,next,50,8);next.setOnClickListener(v->{page++;fillGrid(root,favorites,query);});}
 }
 void openKodi(){android.content.pm.PackageManager pm=getPackageManager();Intent intent=pm.getLaunchIntentForPackage("org.xbmc.kodi");if(intent==null){new AlertDialog.Builder(this).setTitle("Kodi não instalado").setMessage("Instale o Kodi oficial compatível com seu aparelho. Depois, no Kodi, instale o complemento LIX TV pelo arquivo ZIP da Kodi Edition.").setPositiveButton("Entendi",null).show();return;}startActivity(intent);}
 void kodiSection(){title("Kodi • Biblioteca de mídia","Acesso ao motor Kodi e ao complemento LIX TV");TextView launch=button("▶  Abrir Kodi instalado");margin(content,launch,62,12);launch.setOnClickListener(v->openKodi());TextView guide=text("No Kodi, instale o complemento LIX TV em Complementos > Instalar de um arquivo ZIP. Ele inclui vídeos abertos e suporta playlists M3U autorizadas. Não há filmes comerciais fornecidos automaticamente pelo Vikings ou Brazuca Play.",16);guide.setTextColor(PALE);margin(content,guide,166,10);}
 void settings(){title("Configurações","LIX TV • WEBLYXX");margin(content,text("Versão 0.7 • Android TV\nFonte de canais: IPTV-org\nConteúdos de vídeo: amostras abertas\nNenhuma integração Vikings/Brazuca Play está instalada nesta versão.",16),140,14);
 TextView br=button("↻ Atualizar canais do Brasil");margin(content,br,58,9);br.setOnClickListener(v->load("Brasil"));
 TextView global=button("🌐 Carregar canais do mundo");margin(content,global,58,9);global.setOnClickListener(v->load("Mundo"));}
 void render(){if(content==null)return;content.removeAllViews();
 if(tab.equals("Início")){hero();if(loading)margin(content,text("Carregando canais...",16),44,8);else channelRail("TV ao vivo • Destaques",channels);mediaRail("Cinema aberto",false);mediaRail("Coleções de episódios",true);}
 else if(tab.equals("TV ao vivo"))channelsScreen(false);
 else if(tab.equals("Favoritos"))channelsScreen(true);
 else if(tab.equals("Filmes")){title("Filmes","Cinema aberto e biblioteca pelo Kodi");mediaRail("Cinema aberto",false);kodiSection();}
 else if(tab.equals("Séries")){title("Séries","Episódios demonstrativos e biblioteca pelo Kodi");mediaRail("Episódios demonstrativos",true);kodiSection();}
 else settings();
 }
}