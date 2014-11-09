#!/bin/bash
find ./src -name *.java > all_java.txt
echo "Start Compiling!"
javac -d ./bin -classpath .:`find * -name *.jar | tr "\n" ":"` @all_java.txt
echo "Start Running!"
nohup java -Djava.ext.dirs=./lib -cp ./bin start.SortOptimizer ./input.param ./test.extract ./AllClick.click ./OnlineWeight.txt &
