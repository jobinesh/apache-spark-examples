package com.jobinesh.spark.job.core

import com.jobinesh.spark.job.core.job.TypesafeConfigBuilder
import com.typesafe.config.Config
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import com.jobinesh.spark.job.core.util.utils._;
import scala.util.Try

/**
 * Trivial trait for executing basic Spark runnable applications.
 *
 * @tparam Context the type of the application context class.
 * @tparam Result  The output type of the run function.
 *
 */

trait SparkApp[Context, Result] extends SparkRunnable[Context, Result] with TypesafeConfigBuilder with Logging {

  /**
   * This is the key for basically choosing a certain app and it should have
   * the form of '_APP_NAME_....', reflected also in the configuration structure.
   *
   * By default this will return the simple class name.
   */
  def appName: String = getClass.getSimpleName.replaceAll("\\$", "")

  /**
   * This function needs to be implemented and should contain all logic related
   * to parsing the configuration settings and building the application context.
   */
  def createContext(config: Config): Context

  /**
   * Any object extending this trait becomes a runnable application.
   *
   * @param args
   */
  def main(implicit args: Array[String]): Unit = {
    log.info(s"Running $appName")
    implicit val conf = applicationConfiguration
    implicit val spark = createSparkSession(conf)

    val outcome = for {
      context <- Try(createContext(conf))
      result <- Try(run(spark, context))
    } yield result

    outcome
      .logSuccess(_ => log.info(s"$appName: Job successfully completed."))
      .logFailure(t => log.error(s"$appName: Job failed.", t))

    // Close the session so the application can exit
    Try(spark.close)
      .logSuccess(_ => log.info(s"$appName: Spark session closed."))
      .logFailure(t => log.error(s"$appName: Failed to close the spark session.", t))

    // If the application failed we exit with an exception
    outcome.get
  }

  protected def createSparkSession(config: Config) = {
    val defSparkConf = new SparkConf(true)
    val sparkConf = defSparkConf.setAppName(config.getString("demo.app.name")).
      setMaster(defSparkConf.get("spark.master", config.getString("demo.spark.master")))
      .set("spark.driver.host", config.getString("demo.spark.driver"))
    SparkSession.builder.config(sparkConf).getOrCreate()
  }
}
