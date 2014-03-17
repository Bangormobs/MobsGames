#!/bin/sh
revision=`git log --pretty=format:'' | wc -l`
classpath='-cp ../IRCChat/bin/com/trigg/irc/:/home/triggerhapp/Minecraft/vanilla/craftbukkit-1.7.2-R0.1.jar'
find src -name \*.java -print > file.list
javac ${classpath} @file.list
file="MobsGames-0.1-${revision}.jar"
if [ -f $file ];
then
  echo 'A Jar file already exists for this version. Does the git need a commit?'
else
  jar cvf ${file} *.yml
  cd src
  jar uvf ../${file} * 
  chown triggerhapp:triggerhapp ../${file}
  chmod a+rxw ../${file}
  scp ../${file} triggerhapp@mobsoc.co.uk:/home/triggerhapp/www/plugins
fi

