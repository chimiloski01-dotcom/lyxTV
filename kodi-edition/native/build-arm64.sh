#!/usr/bin/env bash
set -euo pipefail
: "${ANDROID_HOME:?Set ANDROID_HOME to Android SDK root}"
: "${KODI_SRC:?Set KODI_SRC to cloned xbmc/xbmc directory}"
KODI_SRC="$(realpath "$KODI_SRC")"
WORK_ROOT="${WORK_ROOT:-$PWD/.native-build}"
mkdir -p "$WORK_ROOT"/{tarballs,depends}
cd "$KODI_SRC/tools/depends"
./bootstrap
./configure --host=aarch64-linux-android --with-sdk-path="$ANDROID_HOME" --with-tarballs="$WORK_ROOT/tarballs" --prefix="$WORK_ROOT/depends" --enable-debug=no
make -j"${BUILD_JOBS:-2}"
make -C "$KODI_SRC/tools/depends/target/binary-addons" -j"${BUILD_JOBS:-2}"
make -C "$KODI_SRC/tools/depends/target/cmakebuildsys"
cd "$KODI_SRC/build"
make -j"${BUILD_JOBS:-2}"
make apk
echo "Built upstream Kodi. Custom LIX TV skin/package integration remains to be done."
