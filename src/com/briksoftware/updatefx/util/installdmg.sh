#!/bin/sh

DMG_FILE="$1"
APP_PID="$2"
TMP_NAME="updatefx$(date +%s)"

while /bin/ps -p $APP_PID > /dev/null; do 
  /bin/sleep 1; 
done

/usr/bin/hdiutil convert -quiet "$DMG_FILE" -format UDTO -o /tmp/$TMP_NAME
/usr/bin/hdiutil attach -quiet -nobrowse -noautoopen -readonly -mountpoint /Volumes/$TMP_NAME /tmp/$TMP_NAME.cdr

shopt -s nullglob

pushd /Volumes/$TMP_NAME > /dev/null
for app in *.app; do
  /bin/rm -rf "/Applications/$app"
  /bin/cp -pR "/Volumes/$TMP_NAME/$app" /Applications/
done
popd > /dev/null

/usr/bin/hdiutil detach -quiet /Volumes/$TMP_NAME
/bin/rm /tmp/$TMP_NAME.cdr
/bin/rm "$DMG_FILE"

open -a "/Applications/$app"