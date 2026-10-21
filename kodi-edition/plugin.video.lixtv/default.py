# -*- coding: utf-8 -*-
"""LIX TV Kodi prototype — plays public sample media, supports user-supplied authorized M3U."""
import sys
import urllib.parse
import urllib.request
import xbmc
import xbmcgui
import xbmcplugin
import xbmcaddon

HANDLE = int(sys.argv[1])
BASE = sys.argv[0]
ADDON = xbmcaddon.Addon()
SAMPLES = [
    ("Big Buck Bunny — curta aberto", "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
    ("Elephants Dream — curta aberto", "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"),
    ("Sintel — curta aberto", "https://download.blender.org/durian/trailer/sintel_trailer-480p.mp4")
]

def entry(title, action, playable=False, **options):
    params = dict(action=action, **options)
    url = BASE + "?" + urllib.parse.urlencode(params)
    item = xbmcgui.ListItem(label=title)
    item.setProperty("IsPlayable", "true" if playable else "false")
    xbmcplugin.addDirectoryItem(HANDLE, url, item, isFolder=not playable)

def menu():
    xbmcplugin.setPluginCategory(HANDLE, "LIX TV • WEBLYXX")
    entry("▶ Filmes abertos / demonstrações", "samples")
    entry("📺 TV ao vivo • minha playlist M3U", "playlist")
    entry("⚙ Configurar playlist autorizada", "configure")
    xbmcplugin.endOfDirectory(HANDLE)

def samples():
    for title, url in SAMPLES:
        entry(title, "play", playable=True, url=url)
    xbmcplugin.endOfDirectory(HANDLE)

def parse_m3u(content):
    name = ""
    for line in content.splitlines():
        line = line.strip()
        if line.startswith("#EXTINF"):
            name = line.split(",", 1)[-1].strip()
        elif line and not line.startswith("#"):
            url = urllib.parse.urlparse(line)
            if url.scheme in ("https", "http") and url.netloc:
                yield name or url.hostname, line
            name = ""

def playlist():
    url = ADDON.getSetting("playlist_url")
    parsed = urllib.parse.urlparse(url)
    if parsed.scheme != "https" or not parsed.netloc:
        xbmcgui.Dialog().notification("LIX TV", "Configure uma playlist M3U HTTPS autorizada")
        xbmcplugin.endOfDirectory(HANDLE)
        return
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "LIXTV/0.1"})
        with urllib.request.urlopen(req, timeout=12) as response:
            raw = response.read(1500000).decode("utf-8-sig", "replace")
        for index, (title, stream) in enumerate(parse_m3u(raw)):
            if index >= 500:
                break
            entry(title, "play", playable=True, url=stream)
    except Exception as exc:
        xbmcgui.Dialog().ok("LIX TV", "Não foi possível acessar a playlist: " + str(exc))
    xbmcplugin.endOfDirectory(HANDLE)

def play(url):
    parsed = urllib.parse.urlparse(url)
    if parsed.scheme not in ("https", "http") or not parsed.netloc:
        xbmcgui.Dialog().notification("LIX TV", "Endereço inválido")
        return
    item = xbmcgui.ListItem(path=url)
    item.setProperty("IsPlayable", "true")
    item.setMimeType("application/vnd.apple.mpegurl" if ".m3u8" in parsed.path else "video/mp4")
    xbmcplugin.setResolvedUrl(HANDLE, True, item)

def configure():
    ADDON.openSettings()

if __name__ == "__main__":
    args = dict(urllib.parse.parse_qsl(sys.argv[2][1:]))
    action = args.get("action", "")
    if action == "samples":
        samples()
    elif action == "playlist":
        playlist()
    elif action == "play":
        play(args.get("url", ""))
    elif action == "configure":
        configure()
    else:
        menu()
