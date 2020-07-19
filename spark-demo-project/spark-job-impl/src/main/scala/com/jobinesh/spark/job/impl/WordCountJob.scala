package com.jobinesh.spark.job.impl

import java.util.UUID.randomUUID

import com.jobinesh.spark.job.core.SparkApp
import com.typesafe.config.Config
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.reflect.io.File

object WordCountJob
  extends SparkApp[ApplicationContext, Unit] {
  val LOGGER = Logger.getLogger(this.getClass)
  LOGGER.setLevel(Level.INFO)

  /**
   * Builds the application context.
   */
  override def createContext(config: Config): ApplicationContext = {
    val currentApp = config.getString("demo.app.name")
    var sparkMaster = config.getString("demo.spark.master")
    val context = new ApplicationContext(currentApp, sparkMaster)
    return context
  }

  /**
   * This method contains core job implmentation logic
   *
   * @param context context instance that should contain all the application specific configuration
   * @param spark   active spark session
   * @return
   */
  override def run(implicit spark: SparkSession, context: ApplicationContext): Unit = {

    implicit val conf = applicationConfiguration
    val sc = spark.sparkContext
    val inRdd:RDD[String] = sc.textFile(conf.getString("demo.app.input"))
    // Split up into words.
    val words = inRdd.flatMap(line => line.split(" "))
    // Transform into word and count.
    val outRdd = words.map(word => (word, 1)).reduceByKey{case (x, y) => x + y}
    // Save the word count back out to a text file, causing evaluation.
    val id = randomUUID().toString
    outRdd.saveAsTextFile(conf.getString("demo.app.output")+ File.separator +id)
  }

}
