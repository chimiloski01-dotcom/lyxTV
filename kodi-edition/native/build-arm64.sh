#!/usr/bin/env bash
set -euo pipefail
: "${ANDROID_HOME:?Define ANDROID_HOME para o Android SDK}"
: "${ANDROID_NDK_HOME:?Defina ANDROID_NDK_HOME para o NDK Android r21e}"
: "${KODI_SRC:?Define KODI_SRC para o checkout xbmc/xbmc}"
KODI_SRC="$(realpath "$KODI_SRC")"
ANDROID_NDK_HOME="$(realpath "$ANDROID_NDK_HOME")"
WORK_ROOT="${WORK_ROOT:-$PWD/.native-build}"
BUILD_JOBS="${BUILD_JOBS:-2}"
mkdir -p "$WORK_ROOT"/{tarballs,depends,build,artifacts}
WORK_ROOT="$(realpath "$WORK_ROOT")"
cd "$KODI_SRC/tools/depends"
test -x "$ANDROID_NDK_HOME/ndk-build" || {
  echo "ERRO: ANDROID_NDK_HOME nao aponta para um NDK Android valido" >&2
  exit 2
}
./bootstrap
./configure --host=aarch64-linux-android \
  --with-sdk-path="$ANDROID_HOME" \
  --with-ndk-path="$ANDROID_NDK_HOME" \
  --with-tarballs="$WORK_ROOT/tarballs" \
  --prefix="$WORK_ROOT/depends" \
  --enable-debug=no
make -j"$BUILD_JOBS"
cd "$KODI_SRC"
make -j"$BUILD_JOBS" -C tools/depends/target/binary-addons
make -C tools/depends/target/cmakebuildsys BUILD_DIR="$WORK_ROOT/build"
cd "$WORK_ROOT/build"
make -j"$BUILD_JOBS"
make apk
find "$KODI_SRC" "$WORK_ROOT/build" -type f -name '*.apk' -print -exec cp -n {} "$WORK_ROOT/artifacts/" \;
if ! find "$WORK_ROOT/artifacts" -maxdepth 1 -name '*.apk' | grep -q .; then
  echo "ERRO: build terminou sem APK" >&2
  exit 1
fi
sha256sum "$WORK_ROOT"/artifacts/*.apk | tee "$WORK_ROOT/artifacts/SHA256SUMS"
echo "Kodi ARM64 compilado em $WORK_ROOT/artifacts. A personalizacao LIX TV deve ser aplicada e testada antes da distribuicao."
