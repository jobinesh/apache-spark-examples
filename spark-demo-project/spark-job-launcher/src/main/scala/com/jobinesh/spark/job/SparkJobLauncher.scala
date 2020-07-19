package com.jobinesh.spark.job

import org.apache.spark.launcher.SparkAppHandle

object SparkJobLauncher {
  def main(implicit args: Array[String]): Unit = {
    import org.apache.spark.launcher.SparkLauncher
    import java.util.concurrent.CountDownLatch
    val countDownLatch = new CountDownLatch(1)
    val spark = new SparkLauncher().setAppResource("../spark-job-impl/target/spark-job-impl-LATEST-SNAPSHOT.jar")
      .setMainClass("com.jobinesh.spark.job.impl.WordCountJob")
      .setMaster("local")
      .addSparkArg("--packages", "com.typesafe:config:1.4.0,com.jobinesh.example:spark-job-common:LATEST-SNAPSHOT,com.jobinesh.example:spark-job-impl:LATEST-SNAPSHOT")
      .startApplication(new SparkAppHandle.Listener() {
        def infoChanged(handle: SparkAppHandle): Unit = {
        }

        def stateChanged(handle: SparkAppHandle): Unit = {
          val appState = handle.getState()
          println(s"Spark App Id [${handle.getAppId}] State Changed. State [${handle.getState}]")

          if (appState != null && appState.isFinal) {
            countDownLatch.countDown //waiting until spark driver exits
          }
        }
      })
    countDownLatch.await()
  }
}
