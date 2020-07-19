## Prerequisite for running this demo    
The following tools needs to be installed in your machine         
- JDK 11     
- Scala 3.0.0 : https://www.scala-lang.org/download/     
- Spark 3.0.0 : https://spark.apache.org/downloads.html     

## About the demo     
The Spark application that you may find in the spark-job-impl module reads the text from src/main/resources/demo.txt file
and generates an output file with total count for each word. The output directory location is configured in src/main/resources/application.conf
      
## To run this example in spark local mode, do the following
$ cd <spark-demo-project>      
$ mvn clean install     
$ cd spark-job-impl
$ mvn exec:java -Dexec.mainClass="com.jobinesh.spark.job.impl.WordCountJob"  

## To run this example using SparkLauncher, do the following
$ cd <spark-demo-project>      
$ mvn clean install     
$ cd spark-job-launcher  
$ mvn exec:java -Dexec.mainClass="com.jobinesh.spark.job.SparkJobLauncher"  

## To run this example using SparkSubmit, do the following
$ cd <spark-demo-project>      
$ mvn clean install     
$ cd <spark-home>
$ ./bin/spark-submit  --packages com.typesafe:config:1.4.0,com.jobinesh.example:spark-job-common:LATEST-SNAPSHOT  --class com.jobinesh.spark.job.impl.WordCountJob --master local <spark-demo-project>/spark-job-impl/target/spark-job-launcher-LATEST-SNAPSHOT.jar     
  
