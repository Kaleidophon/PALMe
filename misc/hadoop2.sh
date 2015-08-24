hadoop fs -rm -r wordcount/default_out
hadoop jar ~/Desktop/WordCount.jar wordcount/default/ wordcount/default_out/
hadoop fs -cat wordcount/default/part-r-00000
hadoop fs -copyToLocal wordcount/default/part-r-00000 ~/Desktop/