###################
#Sort Optimization#
###################

Author: Zhou Ye (Data Mining Engineer Intern)
Date: 2013/8/9

###Introduction###
This project is built to satisfy the search ranking of NetEase Cloud Music. The online ranking system at present is a linear structure of which the coefficients are manually adjusted. 
This method can be used when the product goes public at first. However, it cannot avoid the human's subjective error and the adjustment is time-consuming. 
Now based on the click through rate, we develop a machine learning model which can somehow adjust the coefficients by users' taste and more important, it is automatic. 

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
(3) Due to the privacy problem of company, all data files are not listed. 

###Contact###
Email: yezhou199032@gmail.com
