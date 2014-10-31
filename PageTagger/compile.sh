#! /bin/sh

javac -cp 'lib/commons-lang3-3.3.2.jar:lib/stanford-postagger-3.4.1.jar' -d bin `find src/main/java -name '*.java'`
