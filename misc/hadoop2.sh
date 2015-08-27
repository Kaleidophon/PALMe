cd ~/Desktop
rm -r ./wordcount
mkdir ./wordcount
hadoop fs -rm -r wordcount/default_out
hadoop jar ~/Desktop/WordCount.jar wordcount/default/ wordcount/default_out/ 3 no
hadoop fs -cat wordcount/default_out/part-r-00000 wordcount/default_out/res.txt
hadoop fs -copyToLocal wordcount/default_out/part-r-00000 ~/Desktop/wordcount/
cat ~/Desktop/wordcount/part-r-00000 > ~/Desktop/wordcount/res.txt