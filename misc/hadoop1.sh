cd ../src/counting/
ant compile jar
scp -P 2222 ~/Desktop/Projekt/jars/toupload/dist/WordCount.jar ulmer@pool.cl.uni-heidelberg.de:~/Desktop/
cd ../../misc/
ssh -p 2222 ulmer@pool.cl.uni-heidelberg.de 'bash -s' < hadoop2.sh
scp -P 2222 ulmer@pool.cl.uni-heidelberg.de:~/Desktop/wordcount/res.txt ~/Desktop/
exit
