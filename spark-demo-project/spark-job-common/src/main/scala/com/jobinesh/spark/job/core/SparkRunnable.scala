package com.jobinesh.spark.job.core

import org.apache.spark.sql.SparkSession

/**
 * A simple runnable trait for Spark applications.
 *
 * @tparam Context the type of the application context class.
 * @tparam Result The output type of the run function.
 *
 */
trait SparkRunnable[Context, Result] {

  /**
   * This function needs to be implemented and should contain the entire runnable logic.
   *
   * @param context context instance that should contain all the application specific configuration
   * @param spark active spark session
   * @return
   */
  def run(implicit spark: SparkSession, context: Context): Result

}
