#!/bin/sh
revision=`git log --pretty=format:'' | wc -l`
classpath='-cp ../IRCChat/bin/com/trigg/irc/:/home/triggerhapp/Downloads/craftbukkit-1.4.5-R0.2.jar'

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
  scp ../${file} tekkit@mobsoc.co.uk:/home/tekkit/www/plugins
fi

