package com.jobinesh.spark.job.core

import com.typesafe.config.{Config, ConfigFactory}

package object job {


  /**
   * `TypesafeConfigBuilder` must be mixed in with a class or a trait that has an `appName` function
   * and it provides the `applicationConfiguration` function that builds a Typesafe `Config` instance
   * out of various resources including application parameters.
   */
  trait TypesafeConfigBuilder {
    this: {def appName: String} =>

    /**
     * Extract and assemble a configuration object out of the application parameters and
     * configuration files,
     * @return the application configuration object
     */
     def applicationConfiguration(): Config = {
      val CONFIGURATION_FILENAME = "application.conf"
      val config = ConfigFactory.load(CONFIGURATION_FILENAME)
      config
    }

  }

}
