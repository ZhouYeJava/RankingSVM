###################
#Ranking SVM Model#
###################

Author: Zhou Ye 
Date: 2013/8/9

###Introduction###
This project uses Ranking-SVM(LR) for sorting. The codes is written in Java using WEKA.

###How To Use###
There are four input files:
parameter_file: input.param
extract_file: *.extract
click_file: AllClick.click
online_weight_file: OnlineWeight.txt
The shell script (compile_and_run.sh) is designed as below:
--------------------------------------
#!/bin/bash
find ./src -name *.java > all_java.txt
echo "Start Compiling!"
javac -d ./bin -classpath .:`find * -name *.jar | tr "\n" ":"` @all_java.txt
echo "Start Running!"
nohup java -Djava.ext.dirs=./lib -cp ./bin start.SortOptimizer parameter_file extract_file click_file online_weight_file &
--------------------------------------
replace the four files like: ./input.param ./test.extract ./AllClick.click ./OnlineWeight.txt
linux code: sh compile_and_run.sh

###Warning###
(1) This program generally will not have any problems. Sometimes, especially when the scale of the data set is small, the weight will be zero and WEKA will not show zero weight. Thus when 
you check the log, if you see "You need to adjust the coefficients manually!", you need to add zero weight to the file Coefficient.txt and rerun the program. 
(2) You need to set the default weights in the test environment first, then you can run the program
(3) Due to the privacy, all data files are not listed. 

