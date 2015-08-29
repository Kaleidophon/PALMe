/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package counting;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
	    
	    private final static IntWritable one = new IntWritable(1);
	    private Text ngram = new Text();
	    private static int n = 1;
	      
	    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
	    	StringTokenizer itr = new StringTokenizer(value.toString());
	    	List<String> tokens = new ArrayList<>();
	    	// Collecting all sentence tokens
	    	while (itr.hasMoreTokens()) {
	    		tokens.add(itr.nextToken());
	    	}
	    	// Emitting ngrams
	    	for (int i = n-1; i < tokens.size(); i++) {
	    		ngram.set(concat(tokens.subList(i-n+1, i+1)));
	    		context.write(ngram, one);
	    	}
	    }
	    
	    /** Efficient Concatenation with StringBuilder */
	    private String concat(List<String> stringlist) {
	    	StringBuilder sb = new StringBuilder();
	    	for (int i = 0; i < stringlist.size(); i++) {
	    		sb.append(stringlist.get(i) + " ");
	    	}
	    	return sb.toString();
	    }
	    
	    /** Setting the ngram length */
	    public static void setN(int new_n) {
	    	n = new_n;
	    }
	}
	
	public static class ZipfPartitioner extends Partitioner<IntWritable,Text> {
		
		private static int NUMBER_OF_REDUCERS = 1;
		private static int max = 0;
		private static int total = 0;
		private static double k = 2;
		
		@Override
		public int getPartition(IntWritable key, Text value, int numPartitions) {
			
			if (NUMBER_OF_REDUCERS == 0) {
				return 0;
			}
			
			int word_occurences = key.get();
			total++;
			
			if (word_occurences > max) {
				max = word_occurences;
			}
			
			// If haven't encountered a high maximum, a special partition doesn't make much sense
			if (max < 100) {
				return word_occurences % NUMBER_OF_REDUCERS;
			}
			
			// For higher maxima
			double[] boundaries = new double[NUMBER_OF_REDUCERS];
			double min_x = 2 / max;
			double interval = (total - min_x) / NUMBER_OF_REDUCERS;
			for (int i = 0; i < boundaries.length; i++) {
				boundaries[i] = zipfFunction((i*interval + min_x), k);
			}
			
			return 1;
		}
		
		public static void setNumberOfReducers(int num) {
			NUMBER_OF_REDUCERS = num;
		}
		
		public static void setK(double k_) {
			k = k_;
		}
		
		private static double zipfFunction(double x, double k) {
			return (k * Math.pow(x,k)) / Math.pow(x, k+1);
		}
	
	}
  
	public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
	   
		private IntWritable result = new IntWritable();
	
	    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
	    	int sum = 0;
	    	for (IntWritable val : values) {
	    		sum += val.get();
	    	}
	    	result.set(sum);
	    	context.write(key, result);
	    }
	}

	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
	    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	    final int NUMBER_OF_NODES = 24;
	    final int MAX_NUMBER_OF_TASKS = 1000;
	    final double REDUCER_CONSTANT = 0.95; // or 1.75
	    
	    if (otherArgs.length < 5) {
	    	System.err.println("Usage: wordcount <in> [<in>...] <out> <ngram> <combiner:yes/no> <custom partioner:yes/no>");
	    	System.exit(2);
	    }
	    
	    // Setting map and reduce tasks
	    
	    
	    Job job = Job.getInstance(conf, "Word count");
	    
	    //conf.setNumMapTasks(5); // Not possible with code in line?
	    int NUMBER_OF_REDUCERS = (int) REDUCER_CONSTANT * NUMBER_OF_NODES * MAX_NUMBER_OF_TASKS;
	    System.out.println("Number of Reducers: " + NUMBER_OF_REDUCERS);
	    job.setNumReduceTasks(5);
	    
	    job.setJarByClass(WordCount.class);
	    TokenizerMapper.setN(Integer.parseInt(otherArgs[otherArgs.length-3])); // Set ngram length
	    job.setMapperClass(TokenizerMapper.class);
	    if (otherArgs[otherArgs.length-2] == "yes") {
	    	job.setCombinerClass(IntSumReducer.class);
	    }
	    if (otherArgs[otherArgs.length-1] == "yes") {
	    	job.setPartitionerClass(ZipfPartitioner.class);
	    	ZipfPartitioner.setNumberOfReducers(NUMBER_OF_REDUCERS);
	    }
	    job.setReducerClass(IntSumReducer.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    // Input paths
	    for (int i = 0; i < otherArgs.length - 4; ++i) {
	    	FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
	    }
	    // Output paths
	    FileOutputFormat.setOutputPath(job, new Path(otherArgs[otherArgs.length - 4]));

	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
