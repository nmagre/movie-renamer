#!/bin/sh

mv "$1$2" "$1$3"
chmod a+x "$1$3"
if [ -f "$1Uninstaller/uninstaller.jar" ];then
  chmod a+x "$1Uninstaller/uninstaller.jar"
fi
