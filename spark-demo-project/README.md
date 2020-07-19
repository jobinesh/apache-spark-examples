## Prerequisite for running this demo    
The following tools needs to be installed in your machine         
- JDK 11     
- Scala 3.0.0 : https://www.scala-lang.org/download/     
- Spark 3.0.0 : https://spark.apache.org/downloads.html     
     
## To run this example, do the following
$ cd <spark-demo-project>      
$ mvn clean install      
$ mvn exec:java -Dexec.mainClass="com.jobinesh.spark.job.impl.WordCountJob"    
